import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;

import com.mongodb.BasicDBList;
import com.mongodb.hadoop.MongoInputFormat;
import com.mongodb.hadoop.MongoOutputFormat;

import scala.Tuple2;

public final class WordCount {

	private static final Pattern UNDESIRABLES = Pattern.compile("[\\d+\\]\\[\\+(){},.;¡!¿?<>%]");
	private static final Pattern SPACE = Pattern.compile(" +");

	public static void main(String[] args) {
		Configuration mongodbConfig = new Configuration();
		mongodbConfig.set("mongo.job.input.format", "com.mongodb.hadoop.MongoInputFormat");
		mongodbConfig.set("mongo.input.uri", "mongodb://localhost:27017/boarddb.tweets");

		SparkConf conf = new SparkConf().setMaster("spark://0.0.0.0:7077").setAppName("Word Count");

		JavaSparkContext sc = new JavaSparkContext(conf);

		JavaPairRDD<Object, BSONObject> documents = sc.newAPIHadoopRDD(mongodbConfig, MongoInputFormat.class,
				Object.class, BSONObject.class);

		JavaRDD<CandidateWord> words = documents
				.flatMap(t -> {
                    BasicDBList targets = (BasicDBList) t._2.get("targetTwitterIds");
                    String text = (String) t._2.get("text");

                    List<CandidateWord> words1 = new ArrayList<>();
                    if (text != null) {
                        text = text.replace(".", "").replace("\u2026", "").replace(",", "").replace(":", "")
                                .replace("\r", "").replace("\n", "").replace("\"", "").replace("|", "").trim()
                                .toLowerCase();
                        text = UNDESIRABLES.matcher(text).replaceAll("");

                        String[] tweetTokens = SPACE.split(text);
                        StopwordsSpanish stopwords = new StopwordsSpanish();
                        for (String token : tweetTokens) {
                            if (!token.equals("rt") && !token.startsWith("@") && !token.startsWith("#")
                                    && !token.startsWith("http") && !stopwords.isStopword(token)) {
                                if (targets != null && targets.size() > 0 && !token.isEmpty()) {
                                    for(int i = 0 ; i < targets.size() ; i++){
                                        String target = (String) targets.get(i);
                                        words1.add(new CandidateWord(token, target));
                                    }
                                }
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
		outputConfig.set("mongo.output.uri", "mongodb://localhost:27017/boarddb.words");
		save.saveAsNewAPIHadoopFile("file:///empty", Object.class, Object.class, MongoOutputFormat.class, outputConfig);
	}
}
