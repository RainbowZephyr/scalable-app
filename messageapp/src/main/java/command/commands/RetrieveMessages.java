package command.commands;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;

import command.Command;
import datastore.DataStoreConnection;
import datastore.DataStoreConnectionFactory;
import services.RequestHandle;
import thread_pools.DatabaseThreadPool;
import utility.ResponseCodes;

public class RetrieveMessages extends Command {

	@Override
	protected StringBuffer execute(Map<String, Object> requestMapData) throws Exception {
		// TODO Auto-generated method stub
		String threadId = (String) requestMapData.get("threadId");
		Date startDate = (Date) requestMapData.get("startDate");
		Date endDate = (Date) requestMapData.get("endDate"); 
		if(startDate == null || endDate == null || threadId == null){
       		JsonObject response = new JsonObject();
       		response.addProperty("responseCode", ResponseCodes.STATUS_BAD_REQUEST);
       		return new StringBuffer(response.toString());
       	}
		
		parameters.put("startDate", startDate);
        parameters.put("endDate", endDate);
        parameters.put("threadId", threadId);
        parameters.put("action", "RetriveMessages");
		
		
		Class<?> connectionClass = DataStoreConnectionFactory.sharedInstance()
                .getDataStoreConnection("mongodb_data_store_connection");
        DataStoreConnection connection = (DataStoreConnection) connectionClass.newInstance();
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        
		RequestHandle requestHandle = (RequestHandle)
                this.parameters.get(RequestHandle.class.getSimpleName());
        parameters.put(RequestHandle.class.getSimpleName(), requestHandle);
        connection.init(parameters);
        DatabaseThreadPool.sharedInstance().getThreadPool().execute(connection);
		return null;
	}

}
