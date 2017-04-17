package command.commands;


import java.util.Map;


import thread_pools.DatabaseThreadPool;
import command.Command;
import datastore.DataStoreConnection;
import datastore.DataStoreConnectionFactory;

public class UserSignUpCommand extends Command {

	@Override
	protected StringBuffer execute(Map<String, Object> requestMapData)
			throws Exception {
		
		
		Class<?> connectionClass = DataStoreConnectionFactory.sharedInstance()
				.getDataStoreConnection("Postgresql_database_connection");
		DataStoreConnection connection = (DataStoreConnection) connectionClass
				.newInstance();

	
		requestMapData.put("action", "signupUser");
	    

		connection.init(requestMapData);
		DatabaseThreadPool.sharedInstance().getThreadPool().execute(connection);

		return null;
	}
	

	@Override
	protected boolean shouldReturnResponse() {
		return false;
	}


}
