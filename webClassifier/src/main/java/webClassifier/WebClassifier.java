package webClassifier;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration.Dynamic;

import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import webClassifier.configuration.WebClassifierConfiguration;
import webClassifier.daos.TweetDAO;
import webClassifier.resources.TweetResource;

public class WebClassifier extends Application<WebClassifierConfiguration> {
	
	@Override
	public void run(WebClassifierConfiguration webConfiguration, Environment environment) throws Exception {
		addCORSSupport(environment);
		
		String mongoIp = webConfiguration.getMongoConfiguration().getIp();
		String mongoDb = webConfiguration.getMongoConfiguration().getDbName();
		
		MongoClient mongoClient = new MongoClient(mongoIp);
		MongoDatabase database = mongoClient.getDatabase(mongoDb);

		Morphia morphia = new Morphia();
		morphia.mapPackage("webClassifier.entities");
		Datastore ds = morphia.createDatastore(mongoClient, mongoDb);
		
		TweetDAO tweetDAO = new TweetDAO(ds, database);
		TweetResource tweetResource = new TweetResource(tweetDAO);
		environment.jersey().register(tweetResource);
	}

	private void addCORSSupport(Environment environment) {
		Dynamic filter = environment.servlets().addFilter("CORS", CrossOriginFilter.class);
		filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
		filter.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,PUT,POST,DELETE,OPTIONS,PATCH");
		filter.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
		filter.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
		filter.setInitParameter("allowedHeaders", "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin");
		filter.setInitParameter("allowCredentials", "true");
	}

	public static void main(String[] args) throws Exception {
		WebClassifier webClassifier = new WebClassifier();
		webClassifier.run(args);
	}
}
