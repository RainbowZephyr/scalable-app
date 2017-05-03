package command.commands;

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

        
        if(imageUrl == null){
            requestMapData.put("action", "sendTextMessage");

            connection.init(requestMapData);
            DatabaseThreadPool.sharedInstance().getThreadPool().execute(connection);	
        }
        else{

            requestMapData.put("action", "sendImageMessage");

            connection.init(requestMapData);
            DatabaseThreadPool.sharedInstance().getThreadPool().execute(connection);	
        }
        return null;
	}
	
    protected boolean shouldReturnResponse() {
        return false;
    }
	

}
