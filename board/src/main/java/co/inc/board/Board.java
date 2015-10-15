package co.inc.board;

import co.inc.board.api.resources.BroadcasterResource;
import co.inc.board.api.resources.ScoreResource;
import co.inc.board.api.resources.TargetResource;
import co.inc.board.api.resources.TweetResource;
import co.inc.board.api.resources.WordResource;
import co.inc.board.api.ws.BroadcastServlet;
import co.inc.board.domain.business.ScoreBusiness;
import co.inc.board.domain.business.TargetBusiness;
import co.inc.board.domain.business.TweetBusiness;
import co.inc.board.domain.business.WordBusiness;
import co.inc.board.infrastructure.config.BoardConfig;
import co.inc.board.infrastructure.config.MongoConfig;
import co.inc.board.persistence.daos.ScoreDAO;
import co.inc.board.persistence.daos.TargetDAO;
import co.inc.board.persistence.daos.TweetDAO;
import co.inc.board.persistence.daos.WordDAO;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import io.dropwizard.Application;
import io.dropwizard.java8.Java8Bundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration.Dynamic;
import java.util.EnumSet;

public class Board extends Application<BoardConfig> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Board.class);

    @Override
    public void initialize(Bootstrap<BoardConfig> bootstrap) {
        bootstrap.addBundle(new Java8Bundle());
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

    private ObjectMapper configureJackson(Environment environment) {

        ObjectMapper objectMapper = environment.getObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
        objectMapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    private MongoClient getMongoClient(BoardConfig boardConfig) {
        MongoConfig mongoConfig = boardConfig.getMongoConfig();
        return new MongoClient("10.132.1.53");
    }

    @Override
    public void run(BoardConfig boardConfig, Environment environment) throws Exception {

        // add CORS support.
        addCORSSupport(environment);

        // Configure Jackson serialization and deserialization.
        ObjectMapper objectMapper = configureJackson(environment);

        // Initialize mongo client.
        MongoClient mongoClient = getMongoClient(boardConfig);
        MongoDatabase mongoDatabase = mongoClient.getDatabase(boardConfig.getMongoConfig().getDbName());

        TargetDAO targetDAO = new TargetDAO(mongoDatabase, objectMapper);
        TargetBusiness targetBusiness = new TargetBusiness(targetDAO);
        TargetResource targetResource = new TargetResource(targetBusiness);
        environment.jersey().register(targetResource);

        TweetDAO tweetDAO = new TweetDAO(mongoDatabase);
        TweetBusiness tweetBusiness = new TweetBusiness(tweetDAO);
        TweetResource tweetResource = new TweetResource(tweetBusiness, targetBusiness);
        environment.jersey().register(tweetResource);

        WordDAO wordDAO = new WordDAO(mongoDatabase);
        WordBusiness wordBusiness = new WordBusiness(wordDAO);
        WordResource wordResource = new WordResource(wordBusiness, targetBusiness);
        environment.jersey().register(wordResource);
        
        ScoreDAO scoreDAO = new ScoreDAO(mongoDatabase);
        ScoreBusiness scoreBusiness = new ScoreBusiness(scoreDAO, targetBusiness);
        ScoreResource scoreResource = new ScoreResource(scoreBusiness);
        environment.jersey().register(scoreResource);
        
        // webSockets

        environment.jersey().register(new BroadcasterResource(objectMapper));
        environment.getApplicationContext().getServletHandler().addServletWithMapping(
                BroadcastServlet.class, "/ws/*"
        );
    }

    public static void main(String[] args) {
        try {
            Board board = new Board();
            board.run(args);
        } catch (Exception e) {
            LOGGER.error("main", e);
        }
    }
}
