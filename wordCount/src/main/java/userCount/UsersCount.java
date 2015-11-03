package userCount;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Pisco on 10/30/15.
 */
public class UsersCount {

    public static void main(String[] args) {

        Configuration mongodbConfig = new Configuration();
        mongodbConfig.set("mongo.job.input.format", "com.mongodb.hadoop.MongoInputFormat");
        mongodbConfig.set("mongo.input.uri", "mongodb://" + Constants.IP + ":27017/boarddb.tweets");

        SparkConf conf = new SparkConf().setAppName("Users Count Twitter");

        JavaSparkContext sc = new JavaSparkContext(conf);

        JavaPairRDD<Object, BSONObject> documents = sc.newAPIHadoopRDD(mongodbConfig, MongoInputFormat.class,
                Object.class, BSONObject.class);

        JavaPairRDD<Long, DataTweets> tweetsUsers = documents.flatMap(d -> {
            List<DataTweets> data = new ArrayList<>();
            try {
                Long id = (Long) d._2().get("id");
                BSONObject user = (BSONObject) d._2().get("user");
                String name = (String) user.get("screen_name");
                String TWITTER = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
                String date = (String) user.get("created_at");
                SimpleDateFormat sdf = new SimpleDateFormat(TWITTER);
                Date parse = sdf.parse(date);
                Calendar c = Calendar.getInstance();
                c.setTime(parse);
                c.set(Calendar.DATE, 0);
                c.set(Calendar.HOUR, 0);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.SECOND, 0);

                DataTweets t = new DataTweets(name, id, c.getTimeInMillis());
                data.add(t);
            }catch(NullPointerException e){

            }
            return data;
        }).mapToPair(d -> new Tuple2<>(d.getId(), d));

        mongodbConfig.clear();
        mongodbConfig.set("mongo.job.input.format", "com.mongodb.hadoop.MongoInputFormat");
        mongodbConfig.set("mongo.input.uri", "mongodb://" + Constants.IP + ":27017/boarddb.minimumTweets");

        documents = sc.newAPIHadoopRDD(mongodbConfig, MongoInputFormat.class,
                Object.class, BSONObject.class);

        JavaPairRDD<Long, String> candidateData = documents.mapToPair(d -> new Tuple2<>((Long) d._2().get("id"), (String) d._2().get("targetTwitterId")));

        JavaPairRDD<Long, Tuple2<String, DataTweets>> join = candidateData.join(tweetsUsers);

        JavaRDD<CandidateUser> candidateUser = join.flatMap(j -> {
            List<CandidateUser> data = new ArrayList<>();
            String candidate = j._2()._1();

            DataTweets user = j._2()._2();
            if (user != null && candidate != null)
                data.add(new CandidateUser(candidate, user.getUser(), user.getCreatedAt()));
            return data;
        });

        JavaPairRDD<CandidateUser, Integer> result = candidateUser.mapToPair(cu -> new Tuple2<>(cu, 1)).reduceByKey((i1, i2) -> i1 + i2);

        JavaPairRDD<Object, BSONObject> save = result.mapToPair(t -> {
            BSONObject bson = new BasicBSONObject();
            bson.put("user", t._1().getUser());
            bson.put("target", t._1().getTargetTwitterId());
            bson.put("timestamp", t._1().getCreatedAt());
            bson.put("count", t._2());
            return new Tuple2<>(null, bson);
        });

        Configuration outputConfig = new Configuration();
        outputConfig.set("mongo.output.uri", "mongodb://" + Constants.IP + ":27017/boarddb.users");
        save.saveAsNewAPIHadoopFile("file:///empty", Object.class, Object.class, MongoOutputFormat.class, outputConfig);
        sc.stop();
    }
}
