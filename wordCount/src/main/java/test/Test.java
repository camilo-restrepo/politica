package test;

import com.mongodb.hadoop.MongoInputFormat;
import org.apache.hadoop.conf.Configuration;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.bson.BSONObject;

/**
 * Created by Pisco on 10/18/15.
 */
public class Test {

    public static void main(String[] args){

        Configuration mongodbConfig = new Configuration();
        mongodbConfig.set("mongo.job.input.format", "com.mongodb.hadoop.MongoInputFormat");
        mongodbConfig.set("mongo.input.uri", "mongodb://192.168.0.16:27017/boarddb.classifiedTweets");

        SparkConf conf = new SparkConf().setMaster("spark://0.0.0.0:7077").setAppName("Test");
        JavaSparkContext sc = new JavaSparkContext(conf);

        JavaPairRDD<Object, BSONObject> documents = sc.newAPIHadoopRDD(mongodbConfig, MongoInputFormat.class,
                Object.class, BSONObject.class);

        Vectors.
        LabeledPoint point = new LabeledPoint(double label, Vector features);
    }
}
