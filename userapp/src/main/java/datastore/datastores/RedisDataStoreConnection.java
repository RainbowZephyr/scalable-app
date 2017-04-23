package datastore.datastores;

import com.google.gson.JsonObject;
import datastore.DataStoreConnection;
import redis.clients.jedis.Jedis;
import utility.ResponseCodes;

import java.util.Map;

public class RedisDataStoreConnection extends  DataStoreConnection{
	Jedis jedis;
	@Override
	public StringBuffer execute(Map<String, Object> parameters) throws Exception {
		 jedis = new Jedis("localhost");  //TODO redis client address 
		String action = (String) parameters.get("action");
		if(action == null){
			return badRequest();
		}
		if(action.equals("addSession")){
			String sessionId = (String) parameters.get("sessionId");
			String userId = (String) parameters.get("userId");
			if(sessionId == null || userId == null){
				return badRequest();
			}
			return addSession(sessionId, userId);
		}
		if(action.equals("removeSession")){
			String sessionId = (String) parameters.get("sessionId");
			if(sessionId == null){
				return badRequest();
			}
			return removeSession(sessionId);
		}
		if(action.equals("getUserIdFromSessionId")){
			String sessionId = (String) parameters.get("sessionId");
			if(sessionId == null){
				return badRequest();
			}
			return getUserIdFromSessionId(sessionId);
		}
		return null;
	}
	
	private StringBuffer addSession(String sessionId, String userId){
		jedis.set(sessionId, userId);
		JsonObject response = new JsonObject();
		response.addProperty("responseCode", ResponseCodes.STATUS_OK);
		return new StringBuffer(response.toString());
	}
	
	private StringBuffer removeSession(String sessionId){
		jedis.del(sessionId);
		JsonObject response = new JsonObject();
		response.addProperty("responseCode", ResponseCodes.STATUS_OK);
		return new StringBuffer(response.toString());
	}
	
	private StringBuffer getUserIdFromSessionId(String sessionId){
		if(jedis.exists(sessionId)){
			String userId = jedis.get(sessionId);
			JsonObject response = new JsonObject();
			response.addProperty("responseCode", ResponseCodes.STATUS_OK);
			response.addProperty("hasKey", true);
			response.addProperty("userId", userId);
			return new StringBuffer(response.toString());
		}
		else{
			JsonObject response = new JsonObject();
			response.addProperty("responseCode", ResponseCodes.STATUS_OK);
			response.addProperty("hasKey", false);
			return new StringBuffer(response.toString());
		}
		
	}
	
	private StringBuffer badRequest(){
		JsonObject response = new JsonObject();
		response.addProperty("responseCode", ResponseCodes.STATUS_BAD_REQUEST);
		return new StringBuffer(response.toString());
	}
	
}
