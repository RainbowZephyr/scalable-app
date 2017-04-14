package datastore.datastores;


import datastore.DataStoreConnection;

import command.*;

import utility.JSONUtility;

import java.util.ArrayList;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.apache.commons.lang.builder.StandardToStringStyle;
import org.bson.BasicBSONEncoder;

public class MongoDBStoreConnection extends DataStoreConnection{
	
	private static MongoClient client;
//	private static Mongo mongo;

	@Override
	public StringBuffer execute(Map<String, Object> parameters) throws Exception {
		
		if(client == null){
			String dbUrl = (String) parameters.get("db_url");
			if(dbUrl != null){
				client = new MongoClient(dbUrl);
			} else {
				client = new MongoClient();  
			}
		}
		
//		if(mongo == null){
//			String dbUrl = (String) parameters.get("db_url");
//			if(dbUrl != null){
//				mongo = new Mongo(dbUrl, 27013);
//			} else {
//				mongo = new Mongo("localhost", 27017);  
//			}
//		}
		
		String databaseName = (String)parameters.get("db_name");
		MongoDatabase db = (databaseName != null)? client.getDatabase(databaseName) : client.getDatabase("default");
//		@SuppressWarnings("deprecation")
//		DB db = mongo.getDB(databaseName);
		
		String collectionName = (String)parameters.get("collection_name");
		if(collectionName == null) throw new Exception("No collection name was provided");
		MongoCollection<Document> coll = db.getCollection(collectionName);
		
		
		String action = ((String)parameters.get("action")).toLowerCase();
		
		String service = (String)parameters.get("service_type");
		
		Map<String, Object> request_params = (Map<String, Object>) parameters.get("request_paramters");
		

				if(action == "get") {
//					BasicDBObject allQuery = new BasicDBObject();
//					BasicDBObject fields = new BasicDBObject();
//					for (Map.Entry<String, Object> entry : request_params.entrySet()) {
//						fields.put(entry.getKey(), entry.getValue());
//					}
//					
//					DBCursor cursor2 = coll.find(allQuery, fields);
//					while (cursor2.hasNext()) {
//						System.out.println(cursor2.next());
//					}
					Document params = new Document(request_params);
					FindIterable<Document> found = coll.find(params);
					MongoCursor<Document> cursor = found.iterator();

					ArrayList<Document> docs = new ArrayList<Document>();
					while (cursor.hasNext()) {
						docs.add(cursor.next());
					}
					cursor.close();
					return new StringBuffer(docs.toString());
				}

				if(action ==  "post") {
					Document doc = new Document();
					for (Map.Entry<String, Object> entry : request_params.entrySet()) {
						doc.append(entry.getKey(), entry.getValue());
					}
					coll.insertOne(doc);
					return new StringBuffer(new String(""));
				}
				if(action == "delete") {
					Document deleteParams = new Document(request_params);
//					FindIterable<Document> deleteFound = coll.find(deleteParams);
//					MongoCursor<Document> deleteCursor = deleteFound.iterator();
//					
//					ArrayList<Document> deleteDocs = new ArrayList<Document>();
//					while(deleteCursor.hasNext()){
//						deleteDocs.add(deleteCursor.next());
//					}
//					deleteCursor.close();
					coll.deleteMany(deleteParams);
					return new StringBuffer(new String(""));
				}
				if(action == "update") {
					// TODO JSON request params should have all updates as update_text not post_text
					String updateText = (String) ((request_params.get("update_text") != null) ? request_params.get("update_text") : request_params.get("post_text"));
					request_params.remove("update_text");
					request_params.remove("post_text");
					Document replaceDoc = coll.find((Bson) request_params).limit(1).first();
					// TODO all JSON format should have the text field as text and NOT post_text
					replaceDoc.append("text", updateText);
					coll.updateOne((Bson) request_params, replaceDoc);
					return new StringBuffer(new String(""));
				}
				return null;

	}
	
}