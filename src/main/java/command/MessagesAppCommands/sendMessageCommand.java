package command.MessagesAppCommands;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;

import command.Command;
import datastore.DataStoreConnection;
import datastore.DataStoreConnectionFactory;
import services.RequestHandle;
import thread_pools.DatabaseThreadPool;
import utility.ResponseCodes;

public class sendMessageCommand extends Command {

	@Override
	protected StringBuffer execute(Map<String, Object> requestMapData) throws Exception {
		
        String userId = (String) requestMapData.get("fromUserId");
        String messageBody= (String) requestMapData.get("messageBody");
        String imageUrl = (String) requestMapData.get("imageUrl");
       	String threadId = (String) requestMapData.get("threadId");
       	
       	if(userId == null || messageBody == null || threadId == null){
       		JsonObject response = new JsonObject();
       		response.addProperty("responseCode", ResponseCodes.STATUS_BAD_REQUEST);
       		return new StringBuffer(response.toString());
       	}
       	
		Class<?> connectionClass = DataStoreConnectionFactory.sharedInstance()
                .getDataStoreConnection("mongodb_data_store_connection");
        DataStoreConnection connection = (DataStoreConnection) connectionClass.newInstance();
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
        parameters.put("fromUserId", userId);
        parameters.put("messageBody", messageBody);
        parameters.put("threadId", threadId);
        
        if(imageUrl == null){
        	parameters.put("action", "sendTextMessage");
            RequestHandle requestHandle = (RequestHandle)
                    this.parameters.get(RequestHandle.class.getSimpleName());
            parameters.put(RequestHandle.class.getSimpleName(), requestHandle);
            connection.init(parameters);
            DatabaseThreadPool.sharedInstance().getThreadPool().execute(connection);	
        }
        else{
        	parameters.put("action", "sendImageMessage");
        	parameters.put("imageUrl", imageUrl);
            RequestHandle requestHandle = (RequestHandle)
                    this.parameters.get(RequestHandle.class.getSimpleName());
            parameters.put(RequestHandle.class.getSimpleName(), requestHandle);
            connection.init(parameters);
            DatabaseThreadPool.sharedInstance().getThreadPool().execute(connection);	
        }
        return null;
	}
	
    protected boolean shouldReturnResponse() {
        return false;
    }
	

}
