package userCreation;

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
import userCount.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Pisco on 10/31/15.
 */
public class TargetUserCreation {

    public static void main(String[] args){
        Configuration mongodbConfig = new Configuration();
        mongodbConfig.set("mongo.job.input.format", "com.mongodb.hadoop.MongoInputFormat");
        mongodbConfig.set("mongo.input.uri", "mongodb://" + Constants.IP + ":27017/boarddb.tweets");

        SparkConf conf = new SparkConf().setAppName("Target Users Creation Twitter");

        JavaSparkContext sc = new JavaSparkContext(conf);

        JavaPairRDD<Object, BSONObject> documents = sc.newAPIHadoopRDD(mongodbConfig, MongoInputFormat.class,
                Object.class, BSONObject.class) ;

        JavaPairRDD<Long, Long> userCreation = documents.flatMap(d -> {
            List<UserCreationEntity> data = new ArrayList<>();
            try {
                String TWITTER = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
                Long id = (Long) d._2().get("id");
                BSONObject user = (BSONObject) d._2().get("user");
                String date = (String) user.get("created_at");
                SimpleDateFormat sdf = new SimpleDateFormat(TWITTER);
                Date parse = sdf.parse(date);
                Calendar c = Calendar.getInstance();
                c.setTime(parse);
                c.set(Calendar.DATE, 0);
                c.set(Calendar.HOUR, 0);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.SECOND, 0);
                data.add(new UserCreationEntity(id, c.getTimeInMillis()));
            } catch (NullPointerException e) {

            }
            return data;
        }).mapToPair(t -> new Tuple2<>(t.getId(), t.getTimestamp()));

        mongodbConfig.clear();
        mongodbConfig.set("mongo.job.input.format", "com.mongodb.hadoop.MongoInputFormat");
        mongodbConfig.set("mongo.input.uri", "mongodb://" + Constants.IP + ":27017/boarddb.minimumTweets");

        documents = sc.newAPIHadoopRDD(mongodbConfig, MongoInputFormat.class,
                Object.class, BSONObject.class);

        JavaPairRDD<Long, String> candidateData = documents.mapToPair(d -> new Tuple2<>((Long) d._2().get("id"), (String) d._2().get("targetTwitterId")));

        JavaPairRDD<Long, Tuple2<String, Long>> join = candidateData.join(userCreation);

        JavaRDD<CandidateTime> data = join.flatMap(j -> {
            List<CandidateTime> result = new ArrayList<>();
            result.add(new CandidateTime(j._2()._1(), j._2()._2()));
            return result;
        });

        JavaPairRDD<CandidateTime, Integer> counts = data.mapToPair(s -> new Tuple2<>(s, 1)).reduceByKey((i1, i2) -> i1 + i2);

        JavaPairRDD<Object, BSONObject> save = counts
                .mapToPair(tuple -> {
                    BSONObject bson = new BasicBSONObject();
                    bson.put("timestamp", tuple._1().getTimestamp());
                    bson.put("target", tuple._1().getTarget());
                    bson.put("count", tuple._2());
                    return new Tuple2<>(null, bson);
                });

        Configuration outputConfig = new Configuration();
        outputConfig.set("mongo.output.uri", "mongodb://"+ Constants.IP+":27017/boarddb.targetUserCreation");
        save.saveAsNewAPIHadoopFile("file:///empty", Object.class, Object.class, MongoOutputFormat.class, outputConfig);
        sc.stop();
    }
}
