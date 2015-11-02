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
public class UserCreation {

    public static void main(String[] args){
        Configuration mongodbConfig = new Configuration();
        mongodbConfig.set("mongo.job.input.format", "com.mongodb.hadoop.MongoInputFormat");
        mongodbConfig.set("mongo.input.uri", "mongodb://" + Constants.IP + ":27017/boarddb.tweets");

        SparkConf conf = new SparkConf().setAppName("Users Creation Twitter");

        JavaSparkContext sc = new JavaSparkContext(conf);

        JavaPairRDD<Object, BSONObject> documents = sc.newAPIHadoopRDD(mongodbConfig, MongoInputFormat.class,
                Object.class, BSONObject.class) ;

        JavaRDD<Long> dates = documents.flatMap(d -> {
            List<Long> data = new ArrayList<>();
            try {
                String TWITTER = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
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
                data.add(c.getTimeInMillis());
            } catch (NullPointerException e) {

            }
            return data;
        });

        JavaPairRDD<Long, Integer> ones = dates.mapToPair(s -> new Tuple2<>(s, 1));

        JavaPairRDD<Long, Integer> counts = ones.reduceByKey((i1, i2) -> i1 + i2);

        JavaPairRDD<Object, BSONObject> save = counts
                .mapToPair(tuple -> {
                    BSONObject bson = new BasicBSONObject();
                    bson.put("users", tuple._2());
                    bson.put("time", tuple._1());
                    return new Tuple2<>(null, bson);
                });

        Configuration outputConfig = new Configuration();
        outputConfig.set("mongo.output.uri", "mongodb://"+ Constants.IP+":27017/boarddb.userCreation");
        save.saveAsNewAPIHadoopFile("file:///empty", Object.class, Object.class, MongoOutputFormat.class, outputConfig);
        sc.stop();
    }
}