import com.mongodb.hadoop.MongoInputFormat;
import com.mongodb.hadoop.MongoOutputFormat;
import org.apache.hadoop.conf.Configuration;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public final class WordCount {

	private static final Pattern UNDESIRABLES = Pattern.compile("[\\d+\\]\\[\\+(){}\",.:;¡!¿?<>%/\\\\]");
	private static final Pattern SPACE = Pattern.compile(" +");
    private static final String PUNCTUATION = "\\p{Punct}";

    public static void main(String[] args) {

		Configuration mongodbConfig = new Configuration();
		mongodbConfig.set("mongo.job.input.format", "com.mongodb.hadoop.MongoInputFormat");
		mongodbConfig.set("mongo.input.uri", "mongodb://172.24.99.50:27017/boarddb.minimumTweets");

		SparkConf conf = new SparkConf().setAppName("Word Count Twitter");

		JavaSparkContext sc = new JavaSparkContext(conf);

		JavaPairRDD<Object, BSONObject> documents = sc.newAPIHadoopRDD(mongodbConfig, MongoInputFormat.class,
                Object.class, BSONObject.class);

        JavaRDD<CandidateWord> words = documents
				.flatMap(t -> {
                    String targets = (String) t._2.get("targetTwitterId");
                    String text = (String) t._2.get("text");
                    text = UNDESIRABLES.matcher(text).replaceAll("");
                    List<CandidateWord> words1 = new ArrayList<>();
                    if (text != null) {
                        List<String> tweetTokens = Twokenize.tokenizeRawTweetText(text);
                        StopwordsSpanish stopwords = new StopwordsSpanish();
                        for (String token : tweetTokens) {
                            if (!token.toLowerCase().equals("rt") && !token.toLowerCase().startsWith("htt")
                                    && !token.toLowerCase().startsWith("@") && !stopwords.isStopword(token)) {
                                words1.add(new CandidateWord(token.toLowerCase(), targets));
                            }
                        }
                    }
                    return words1;
                });

		JavaPairRDD<CandidateWord, Integer> ones = words
				.mapToPair(s -> new Tuple2<>(s, 1));

		JavaPairRDD<CandidateWord, Integer> counts = ones.reduceByKey((i1, i2) -> i1 + i2);

        JavaPairRDD<Object, BSONObject> save = counts
				.mapToPair(tuple -> {
                    BSONObject bson = new BasicBSONObject();
                    bson.put("word", tuple._1.getWord());
                    bson.put("target", tuple._1.getCandidate());
                    bson.put("count", tuple._2);
                    return new Tuple2<>(null, bson);
                });

		Configuration outputConfig = new Configuration();
		outputConfig.set("mongo.output.uri", "mongodb://172.24.99.50:27017/boarddb.words2");
		save.saveAsNewAPIHadoopFile("file:///empty", Object.class, Object.class, MongoOutputFormat.class, outputConfig);
        sc.stop();
	}
}
