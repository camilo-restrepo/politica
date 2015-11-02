package userCount;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.hadoop.MongoInputFormat;
import org.apache.hadoop.conf.Configuration;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.bson.BSONObject;
import org.bson.Document;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Pisco on 11/2/15.
 */
public class Venn {

    public static void main(String[] args){

        Configuration mongodbConfig = new Configuration();
        mongodbConfig.set("mongo.job.input.format", "com.mongodb.hadoop.MongoInputFormat");
        mongodbConfig.set("mongo.input.uri", "mongodb://" + Constants.IP + ":27017/boarddb.users");

        SparkConf conf = new SparkConf().setAppName("Venn");

        JavaSparkContext sc = new JavaSparkContext(conf);

        JavaPairRDD<Object, BSONObject> documents = sc.newAPIHadoopRDD(mongodbConfig, MongoInputFormat.class,
                Object.class, BSONObject.class).filter(f -> !f._2().get("target").equals("HOLLMANMORRIS") && !f._2().get("target").equals("MMMaldonadoC"));

        JavaPairRDD<String, Iterable<String>> userCandidates = documents.mapToPair(d -> new Tuple2<>((String) d._2().get("user"), (String) d._2().get("target"))).groupByKey();

        Map<Iterable<String>, Object> count = userCandidates.mapToPair(Tuple2::swap).countByKey();

        MongoClient mongoClient = new MongoClient(Constants.IP);
        MongoDatabase db = mongoClient.getDatabase("boarddb");
        MongoCollection<Document> collection = db.getCollection("venn");

        Iterator<Iterable<String>> it = count.keySet().iterator();
        while(it.hasNext()){
            Iterable<String> k = it.next();
            Object num = count.get(k);
            List<String> candidates = new ArrayList<>();
            Iterator<String> kIt = k.iterator();
            while(kIt.hasNext()){
                candidates.add(kIt.next());
            }
            Document d = new Document();
            d.put("candidates", candidates);
            d.put("count", num);
            collection.insertOne(d);
        }
        mongoClient.close();
        sc.stop();
    }
}
