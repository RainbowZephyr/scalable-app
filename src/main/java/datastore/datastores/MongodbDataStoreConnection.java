package datastore.datastores;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import datastore.DataStoreConnection;
import org.bson.Document;
import org.bson.types.ObjectId;
import utility.ResponseCodes;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static com.mongodb.client.model.Filters.eq;

public class MongodbDataStoreConnection extends DataStoreConnection {
    private MongoClient mongoClient;
    private static Properties properties;

    @Override
    public StringBuffer execute(Map<String, Object> parameters) throws Exception {

    	String host = getConfigProperty("host");
    	int port = Integer.parseInt(getConfigProperty("port"));
        mongoClient = new MongoClient(host, port);

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
        if (action.equals("searchForThreads")) {
            String nameQuery = (String) parameters.get("nameQuery");
            return searchForThreads(nameQuery);
        }
        if (action.equals("sendTextMessage")) {
            String threadId = (String) parameters.get("threadId");
            String messageBody = (String) parameters.get("messageBody");
            String userId = (String) parameters.get("fromUserId");
            return sendTextMessage(threadId, userId, messageBody);
        }
        if (action.equals("sendImageMessage")) {
            String threadId = (String) parameters.get("threadId");
            String messageBody = (String) parameters.get("messageBody");
            String userId = (String) parameters.get("fromUserId");
            String imageUrl = (String) parameters.get("imageUrl");
            return sendImageMessage(threadId, userId, messageBody, imageUrl);
        }
        if (action.equals("getUsersInThread")) {
            String threadId = (String) parameters.get("threadId").toString();
            return getUsersInThread(threadId);
        }
        if (action.equals("removeUserFromThread")) {
            String threadId = parameters.get("threadId").toString();
            String userId = parameters.get("userId").toString();
            removeUserFromThread(threadId, userId);
        }
        if(action.equals("RetrieveMessage")){
        	String threadId = (String) parameters.get("threadId");
        	Date startDate = (Date) parameters.get("startDate");
    		Date endDate = (Date) parameters.get("endDate");
    		return RetrieveMessages(threadId, startDate, endDate);
        }
        if(action.equals("AddUserToThread")){
        	String threadId = (String) parameters.get("threadId");
        	String userId = (String) parameters.get("userId");
    		return AddUserToThread(threadId, userId);
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("responseCode", ResponseCodes.STATUS_SERVICE_UNAVAILABLE);
        return new StringBuffer(jsonObject.getAsString());
    }

    private StringBuffer searchForThreads(String nameQuery) {
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.put("threadName", java.util.regex.Pattern.compile(nameQuery));
        FindIterable<Document> findIterable = getMessageThreadsCollection().find(basicDBObject);
        JsonObject response = new JsonObject();
        JsonArray jsonArray = new JsonArray();
        for (Document document : findIterable) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("threadId", document.getString("obectId"));
            jsonObject.addProperty("threadName", document.getString("threadName"));
            jsonArray.add(jsonObject);
        }
        response.add("ThreadNames", jsonArray);
        response.addProperty("responseCode", ResponseCodes.STATUS_OK);
        String s = response.toString();
        return new StringBuffer(s);
    }
    
	private StringBuffer createMessagesThread(String threadName, String userId) {
		ObjectId messageThreadId = new ObjectId();
		Document messagesThread = new Document("_id", messageThreadId);
		messagesThread.append("threadName", threadName);
		messagesThread.append("users", Arrays.asList(userId));
		messagesThread.append("messages", Arrays.asList());
		getMessageThreadsCollection().insertOne(messagesThread);

		Document userObject = getUsersCollection().find(eq("userId", userId)).first();
		if (userObject == null) {
			userObject = new Document("userId", userId);
			userObject.append("threads", Arrays.asList(messageThreadId));
			getUsersCollection().insertOne(userObject);
		} else {
			List<ObjectId> threadsList = (List<ObjectId>) userObject.get("threads");
			threadsList.add(messageThreadId);
			getUsersCollection().updateOne(eq("userId", userId), new Document("$set", userObject));
		}
		JsonObject response = new JsonObject();
		response.addProperty("responseCode", ResponseCodes.STATUS_OK); // TODO need a known key follow
		response.addProperty("threadId", messageThreadId.toHexString());
		return new StringBuffer(response.toString());
	}
    
    public StringBuffer sendTextMessage(String threadId, String userId, String messageBody){
    	MongoCollection<Document> messageThreadCollection = getMessageThreadsCollection();
    	ObjectId messageThreadId = new ObjectId(threadId);
    	Document thread = messageThreadCollection.find(eq("_id",messageThreadId)).first();
    	List<Document> messages = (List<Document>) thread.get("messages");
    	Document message = new Document("body",messageBody);
    	message.append("userId", userId);
    	message.append("timestamp", System.currentTimeMillis()+"");
    	message.append("type", "text");
    	messages.add(message);
    	thread.append("messages", messages);
    	messageThreadCollection.updateOne(eq("_id",messageThreadId),new Document("$set", thread));
    	JsonObject response = new JsonObject();
        response.addProperty("responseCode", ResponseCodes.STATUS_OK);   //TODO need a known key to follow
        return new StringBuffer(response.toString());
    }
    
    public StringBuffer sendImageMessage(String threadId, String userId, String messageBody, String imageUrl){
    	MongoCollection<Document> messageThreadCollection = getMessageThreadsCollection();
    	ObjectId messageThreadId = new ObjectId(threadId);
    	Document thread = messageThreadCollection.find(eq("_id",messageThreadId)).first();
    	List<Document> messages = (List<Document>) thread.get("messages");
    	if(messages == null){
    		messages = new LinkedList<Document>();
    	}
    	Document message = new Document("body",messageBody);
    	message.append("userId", userId);
    	message.append("timestamp", System.currentTimeMillis()+"");
    	message.append("imageUrl", imageUrl);
    	message.append("type", "image");
    	messages.add(message);
    	thread.append("messages", messages);
    	messageThreadCollection.updateOne(eq("_id",messageThreadId),new Document("$set", thread));
    	JsonObject response = new JsonObject();
        response.addProperty("responseCode", ResponseCodes.STATUS_OK);   //TODO need a known key to follow
        return new StringBuffer(response.toString());
    }

    public StringBuffer getUsersInThread(String threadId) {
        BasicDBObject inQuery = new BasicDBObject();
        inQuery.put("_id", new ObjectId(threadId));
        FindIterable<Document> findIterable = getMessageThreadsCollection().find(inQuery);

        JsonObject response = new JsonObject();
        JsonArray jsonArray = new JsonArray();

        for (Document document : findIterable) {
            jsonArray.add(document.getString("userId"));
        }

        response.add("GetUsersInThread", jsonArray);
        response.addProperty("responseCode", ResponseCodes.STATUS_OK);

        return new StringBuffer(response.getAsString());
    }

    public StringBuffer removeUserFromThread(String threadId, String userId) {
        BasicDBObject removeQuery = new BasicDBObject();
        removeQuery.put("_id", new ObjectId(threadId));
        removeQuery.put("userId", userId);

        DBCollection db = (DBCollection) getMessageThreadsCollection();
        db.remove(removeQuery);

        JsonObject response = new JsonObject();
        response.addProperty("responseCode", ResponseCodes.STATUS_OK);

        return new StringBuffer(response.getAsString());
    }
    
    public StringBuffer RetrieveMessages(String threadId, Date startDate, Date endDate){
    	MongoCollection<Document> messageThreadCollection = getMessageThreadsCollection();
    	BasicDBObject query = new BasicDBObject("startDate", new BasicDBObject("$lt", endDate));
    	FindIterable<Document> cursor = messageThreadCollection.find(query);
    	
    	JsonObject response = new JsonObject();
        JsonArray jsonArray = new JsonArray();
        
    	for (Document document : cursor) {
    		jsonArray.add(document.getString("threadId"));
        }
    	
    	response.add("RetrieveMessages", jsonArray);
        response.addProperty("responseCode", ResponseCodes.STATUS_OK);   //TODO need a known key to follow
        return new StringBuffer(response.getAsString());
    }
    
    public StringBuffer AddUserToThread(String threadId, String userId){
    	MongoCollection<Document> messageThreadCollection = getMessageThreadsCollection();
    	Document thread = messageThreadCollection.find(eq("_id",threadId)).first(); 
        
        List<String> users = (List<String>) thread.get("users");
        users.add(userId.toString());
        
        JsonObject response = new JsonObject();
        response.addProperty("responseCode", ResponseCodes.STATUS_OK);   //TODO need a known key to follow
        return new StringBuffer(response.getAsString());
    }

    private MongoDatabase getMessagingAppDB() {

        return mongoClient.getDatabase(getConfigProperty("messages_database_name"));
    }

    private MongoCollection<Document> getMessageThreadsCollection() {
        return getMessagingAppDB().getCollection(getConfigProperty("messageThreads_table"));
    }

    private MongoCollection<Document> getUsersCollection() {
        return getMessagingAppDB().getCollection(getConfigProperty("users_table"));
    }
    
    private String getConfigProperty(String key){
    	if(properties == null){
	    	properties = new Properties();
	    	InputStream input = null;
	    	try {
				input = new FileInputStream("config/mongodb.properties");
				// load a properties file
		    	properties.load(input);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

    	}
    	// get the property value and print it out
		return properties.getProperty(key);
    }

}
