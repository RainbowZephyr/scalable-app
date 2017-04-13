package datastore.datastores;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import datastore.DataStoreConnection;
import org.bson.Document;
import org.bson.types.ObjectId;
import utility.ResponseCodes;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.mongodb.client.model.Filters.eq;

public class MongodbDataStoreConnection extends DataStoreConnection {
    private MongoClient mongoClient;


    /*
    private MongoClient mongoClient;
    private static MongoClientURI mongoClientURI;


    @Override
    public void init(Map<String, Object> parameters) {
        super.init(parameters);
        if (parameters.containsKey("mongoClientURI")) {
            mongoClientURI = (MongoClientURI) parameters.get("mongoClientURI");
        }
        mongoClient = new MongoClient(mongoClientURI);
    }

    public static MongoClientURI getMongoClientURI() {
        return mongoClientURI;
    }

    public static boolean isMongoClientURISet() {
        return mongoClientURI != null;
    }
    */

    @Override
    public StringBuffer execute(Map<String, Object> parameters) throws Exception {

        mongoClient = new MongoClient((MongoClientURI) parameters.get("mongoClientURI"));

        String action = (String) parameters.get("action");
//		switch(action){
//		case "createMessagesThread":
//			return createMessagesThread((String)parameters.get("threadName"),(String)parameters.get("userId"));
//		}
        //project jdk <7...
        if (action.equals("createMessagesThread")) {
            String threadName = (String) parameters.get("threadName");
            String userId = (String) parameters.get("userId");
            return createMessagesThread(threadName, userId);
        }
        if (action.equals("searchForUser")) {
            String nameQuery = (String) parameters.get("nameQuery");
            return searchForUser(nameQuery);
        } 
        if (action.equals("searchForUsersAndThreads")) {
            String nameQuery = (String) parameters.get("nameQuery");
            return searchForUsersAndThreads(nameQuery);
        }
        if(action.equals("sendTextMessage")){
        	String threadId = (String) parameters.get("threadId");
        	String messageBody = (String) parameters.get("messageBody");
        	String userId = (String) parameters.get("fromUserId");
        	return sendTextMessage(threadId, userId, messageBody);
        }
        if(action.equals("sendImageMessage")){
        	String threadId = (String) parameters.get("threadId");
        	String messageBody = (String) parameters.get("messageBody");
        	String userId = (String) parameters.get("fromUserId");
        	String imageUrl = (String) parameters.get("imageUrl");
        	return sendImageMessage(threadId, userId, messageBody, imageUrl);
        }
        return null;
    }

    private StringBuffer searchForUsersAndThreads(String nameQuery) {
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.put("userName", java.util.regex.Pattern.compile(nameQuery));
        FindIterable<Document> findIterable = getUsersCollection().find(basicDBObject);

        JsonObject response = new JsonObject();

        JsonArray jsonArray = new JsonArray();
        for (Document document : findIterable) {
            jsonArray.add(document.getString("userName"));
        }

        basicDBObject = new BasicDBObject();
        basicDBObject.put("threadName", java.util.regex.Pattern.compile(nameQuery));
        findIterable = getMessageThreadsCollection().find(basicDBObject);

        for (Document document : findIterable) {
            jsonArray.add(document.getString("threadName"));
        }

        response.add("UsersAndThreadsNames", jsonArray);
        response.addProperty("responseCode", ResponseCodes.STATUS_OK);

        return new StringBuffer(response.getAsString());
    }

    private StringBuffer searchForUser(String nameQuery) {
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.put("userName", java.util.regex.Pattern.compile(nameQuery));
        FindIterable<Document> findIterable = getUsersCollection().find(basicDBObject);

        JsonObject response = new JsonObject();
        JsonArray jsonArray = new JsonArray();
        for (Document document : findIterable) {
            jsonArray.add(document.getString("userName"));
        }

        response.add("userNames", jsonArray);
        response.addProperty("responseCode", ResponseCodes.STATUS_OK);

        return new StringBuffer(response.getAsString());
    }

    private StringBuffer createMessagesThread(String threadName, String userId) {
        ObjectId messageThreadId = new ObjectId();
        Document messagesThread = new Document("_id", messageThreadId);
        messagesThread.append("threadName", threadName);
        messagesThread.append("users", Arrays.asList(userId));
        getMessageThreadsCollection().insertOne(messagesThread);

        Document userObject = getUsersCollection().find(eq("userId", userId)).first();
        if (userObject == null) {
            userObject = new Document("id", userId);
            userObject.append("threads", Arrays.asList(messageThreadId));
            getUsersCollection().insertOne(userObject);
        } else {
            List<String> threadsList = (List<String>) userObject.get("threads");
            threadsList.add(messageThreadId.toString());
            getUsersCollection().updateOne(eq("userId", userId), new Document("$set", userObject));
        }
        JsonObject response = new JsonObject();
        response.addProperty("responseCode", ResponseCodes.STATUS_OK);   //TODO need a known key to follow
        response.addProperty("threadId", messageThreadId.toString());
        return new StringBuffer(response.getAsString());
    }
    
    public StringBuffer sendTextMessage(String threadId, String userId, String messageBody){
    	MongoCollection<Document> messageThreadCollection = getMessageThreadsCollection();
    	Document thread = messageThreadCollection.find(eq("_id",threadId)).first();
    	List<Document> messages = (List<Document>) thread.get("messages");
    	if(messages == null){
    		messages = new LinkedList<>();
    	}
    	Document message = new Document("body",messageBody);
    	message.append("userId", userId);
    	message.append("timestamp", System.currentTimeMillis()+"");
    	message.append("type", "text");
    	messages.add(message);
    	thread.append("messages", messages);
    	messageThreadCollection.updateOne(eq("_id",threadId),new Document("$set", thread));
    	JsonObject response = new JsonObject();
        response.addProperty("responseCode", ResponseCodes.STATUS_OK);   //TODO need a known key to follow
        return new StringBuffer(response.getAsString());
    }
    
    public StringBuffer sendImageMessage(String threadId, String userId, String messageBody, String imageUrl){
    	MongoCollection<Document> messageThreadCollection = getMessageThreadsCollection();
    	Document thread = messageThreadCollection.find(eq("_id",threadId)).first();
    	List<Document> messages = (List<Document>) thread.get("messages");
    	if(messages == null){
    		messages = new LinkedList<>();
    	}
    	Document message = new Document("body",messageBody);
    	message.append("userId", userId);
    	message.append("timestamp", System.currentTimeMillis()+"");
    	message.append("imageUrl", imageUrl);
    	message.append("type", "image");
    	messages.add(message);
    	thread.append("messages", messages);
    	messageThreadCollection.updateOne(eq("_id",threadId),new Document("$set", thread));
    	JsonObject response = new JsonObject();
        response.addProperty("responseCode", ResponseCodes.STATUS_OK);   //TODO need a known key to follow
        return new StringBuffer(response.getAsString());
    }

    private MongoDatabase getMessagingAppDB() {
        return mongoClient.getDatabase("bleh");  // TODO needs to be changed to the database name
    }

    private MongoCollection<Document> getMessageThreadsCollection() {
        return getMessagingAppDB().getCollection("bleh"); // TODO needs to be changed to the messaging thread collection name
    }

    private MongoCollection<Document> getUsersCollection() {
        return getMessagingAppDB().getCollection("bleh1"); // TODO needs to be changed to the users collection name
    }

}
