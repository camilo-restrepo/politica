package crawler.twitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.dynamodbv2.util.Tables;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import crawler.entities.Tweet;
import crawler.entities.TwitterTarget;
import crawler.entities.TwitterUser;

public class TwitterDynamoDAO implements TwitterDAO {

	private static final Logger LOGGER = LoggerFactory.getLogger(TwitterDynamoDAO.class);

	public static final String ACTIVE_STATUS = "ACTIVE";

	private final ObjectMapper objectMapper;
	private final AmazonDynamoDB dynamoDBClient;

	public boolean createTableIfDontExists(String tableName, String primaryKeyName, ScalarAttributeType primaryKeyType) throws InterruptedException {

		LOGGER.info("createTableIfDontExists: " + tableName + ", " + primaryKeyName);

		boolean tableWasCreated = false;

		DynamoDB dynamoDB = new DynamoDB(dynamoDBClient);
		boolean doesTableExist = Tables.doesTableExist(dynamoDBClient, tableName);

		if (!doesTableExist) {

			List<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
			attributeDefinitions.add(new AttributeDefinition().withAttributeName(primaryKeyName).withAttributeType(primaryKeyType));

			List<KeySchemaElement> keySchemaElements = new ArrayList<KeySchemaElement>();
			keySchemaElements.add(new KeySchemaElement().withAttributeName(primaryKeyName).withKeyType(KeyType.HASH));

			ProvisionedThroughput provisionedThroughput = new ProvisionedThroughput()
					.withReadCapacityUnits(5L)
					.withWriteCapacityUnits(6L);

			CreateTableRequest createTableRequest = new CreateTableRequest()
					.withTableName(tableName)
					.withKeySchema(keySchemaElements)
					.withAttributeDefinitions(attributeDefinitions)
					.withProvisionedThroughput(provisionedThroughput);

			Table table = dynamoDB.createTable(createTableRequest);
			table.waitForActive();

			TableDescription tableDescription = dynamoDB.getTable(tableName).describe();
			tableWasCreated = tableDescription.getTableStatus().equalsIgnoreCase(ACTIVE_STATUS);

		} else {

			tableWasCreated = true;
		}

		LOGGER.info("createTableIfDontExists: tableWasCreated? " + tableWasCreated);

		return tableWasCreated;
	}

	public void createTwitterTablesIfDontExist() {

		try {

			createTableIfDontExists(TARGETS_COLLECTION, "id", ScalarAttributeType.S);
			createTableIfDontExists(USERS_COLLECTION, "twitterId", ScalarAttributeType.N);
			createTableIfDontExists(TWEETS_COLLECTION, "twitterId", ScalarAttributeType.N);

		} catch (InterruptedException e) {

			LOGGER.error("createTwitterTablesIfDontExist", e);
			throw new IllegalStateException(e);
		}
	}

	public TwitterDynamoDAO(ObjectMapper objectMapper, AmazonDynamoDB dynamoDBClient) {

		this.objectMapper = objectMapper;
		this.dynamoDBClient = dynamoDBClient;
		createTwitterTablesIfDontExist();
	}

	@Override
	public List<TwitterTarget> getTargets() {

		LOGGER.info("getTargets");

		List<TwitterTarget> targets = new ArrayList<TwitterTarget>();

		try {

			DynamoDB dynamoDB = new DynamoDB(dynamoDBClient);
			Table table = dynamoDB.getTable(TARGETS_COLLECTION);
			ItemCollection<ScanOutcome> items = table.scan();

			Iterator<Item> iterator = items.iterator();
			while (iterator.hasNext()) {

				Item item = iterator.next();
				LOGGER.info(item.toJSONPretty());
				TwitterTarget facebookTarget = objectMapper.readValue(item.toJSON(), TwitterTarget.class);
				targets.add(facebookTarget);
			}

		} catch (IOException e) {

			LOGGER.error("getTargets", e);
			throw new IllegalArgumentException(e);
		}

		return targets;
	}

	private boolean insertObjectInTable(String tableName, Object object) {

		boolean objectWasInsertedInTable = false;

		try {

			DynamoDB dynamoDB = new DynamoDB(dynamoDBClient);
			Table table = dynamoDB.getTable(tableName);

			String jsonObject = objectMapper.writeValueAsString(object);
			Item itemObject = Item.fromJSON(jsonObject);
			table.putItem(itemObject);
			objectWasInsertedInTable = true;

		} catch (JsonProcessingException e) {

			LOGGER.error("insertObjectInTable", e);
			throw new IllegalArgumentException(e);
		}

		return objectWasInsertedInTable;
	}

	@Override
	public void insertUser(TwitterUser twitterUser) {
		
		LOGGER.info("insertUser");
		insertObjectInTable(USERS_COLLECTION, twitterUser);
	}

	@Override
	public void insertTweet(Tweet tweet) {
		
		LOGGER.info("insertTweet");
		insertObjectInTable(TWEETS_COLLECTION, tweet);
	}
	
	@Override
	public List<Tweet> getAllTweets(int offset, int limit) {
		
		String message = "DynamoDB does not support this type of pagination.";
		throw new IllegalStateException(message);
	}

	@Override
	public List<Tweet> getAllTweets(String lastEvaluatedTwitterId, int limit) {
		
		LOGGER.info("getAllTweets");
		List<Tweet> tweets = new ArrayList<Tweet>();
		
		try {
			
			DynamoDB dynamoDB = new DynamoDB(dynamoDBClient);
			Table table = dynamoDB.getTable(TARGETS_COLLECTION);
			
			QuerySpec querySpec = new QuerySpec()
					.withMaxPageSize(limit)
					.withExclusiveStartKey("twitterId", lastEvaluatedTwitterId);
			
			ItemCollection<QueryOutcome> items = table.query(querySpec);
			Iterator<Item> iterator = items.iterator();
			
			while (iterator.hasNext()) {

				Item item = iterator.next();
				LOGGER.info(item.toJSONPretty());
				Tweet tweet = objectMapper.readValue(item.toJSON(), Tweet.class);
				tweets.add(tweet);
			}
			
		} catch (IOException e) {
			
			LOGGER.error("getAllTweets", e);
		}
		
		return tweets;
	}
}