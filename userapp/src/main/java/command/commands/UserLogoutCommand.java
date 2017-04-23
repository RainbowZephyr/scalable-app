package command.commands;


import command.Command;
import datastore.DataStoreConnection;
import datastore.DataStoreConnectionFactory;
import thread_pools.DatabaseThreadPool;

import java.util.Map;

public class UserLogoutCommand extends Command{

	@Override
	protected StringBuffer execute(Map<String, Object> requestMapData)
			throws Exception {
		
		
		Class<?> connectionClass = DataStoreConnectionFactory.sharedInstance()
				.getDataStoreConnection("Postgresql_database_connection");
		DataStoreConnection connection = (DataStoreConnection) connectionClass
				.newInstance();

		
		requestMapData.put("action", "logOut_request");
	
		connection.init(requestMapData);
		DatabaseThreadPool.sharedInstance().getThreadPool().execute(connection);
		
		
		
		return null;
	}
	
	@Override
	protected boolean shouldReturnResponse() {
		return false;
	}

}
