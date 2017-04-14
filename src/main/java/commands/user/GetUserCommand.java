package commands.user;

import java.util.HashMap;
import java.util.Map;

import command.Command;

import services.RequestHandle;
import thread_pools.DatabaseThreadPool;
import datastore.DataStoreConnection;
import datastore.DataStoreConnectionFactory;

public class GetUserCommand extends Command{

	
	
	protected StringBuffer execute(Map<String, Object> requestMapData)
			throws Exception {

		int userID = (Integer) requestMapData.get("userID");
		
		Class<?> connectionClass = DataStoreConnectionFactory.sharedInstance()
		.getDataStoreConnection("Postgresql_database_connection");
		DataStoreConnection connection = (DataStoreConnection) connectionClass.newInstance();
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("action", "getUser");
		parameters.put("email", userID);
	
		RequestHandle requestHandle = (RequestHandle)
		this.parameters.get(RequestHandle.class.getSimpleName());
		parameters.put(RequestHandle.class.getSimpleName(), requestHandle);
		connection.init(parameters);
		DatabaseThreadPool.sharedInstance().getThreadPool().execute(connection);
		
		
		return null;
	}
	
	
	
	
}
