package commands.user;

import java.util.HashMap;
import java.util.Map;

import services.RequestHandle;
import thread_pools.DatabaseThreadPool;
import command.Command;
import datastore.DataStoreConnection;
import datastore.DataStoreConnectionFactory;

public class LoginCommand extends Command {

	@Override
	protected StringBuffer execute(Map<String, Object> requestMapData)
			throws Exception {

		String email = (String)requestMapData.get("email");
		String password = (String)requestMapData.get("passWord");
		Class<?> connectionClass = DataStoreConnectionFactory.sharedInstance()
		.getDataStoreConnection("Postgresql_database_connection");
		DataStoreConnection connection = (DataStoreConnection) connectionClass.newInstance();
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("action", "loginUser");
		parameters.put("email", email);
		parameters.put("password", password);
		RequestHandle requestHandle = (RequestHandle)
		this.parameters.get(RequestHandle.class.getSimpleName());
		parameters.put(RequestHandle.class.getSimpleName(), requestHandle);
		connection.init(parameters);
		DatabaseThreadPool.sharedInstance().getThreadPool().execute(connection);
		
		
		return null;
	}

}
