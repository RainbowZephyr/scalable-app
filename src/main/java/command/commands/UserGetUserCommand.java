package command.commands;

import java.util.Map;

import command.Command;
import thread_pools.DatabaseThreadPool;
import datastore.DataStoreConnection;
import datastore.DataStoreConnectionFactory;

public class UserGetUserCommand extends Command {

	protected StringBuffer execute(Map<String, Object> requestMapData)
			throws Exception {

		Class<?> connectionClass = DataStoreConnectionFactory.sharedInstance()
				.getDataStoreConnection("Postgresql_database_connection");
		DataStoreConnection connection = (DataStoreConnection) connectionClass
				.newInstance();

		requestMapData.put("action", "getUser");

		connection.init(requestMapData);
		DatabaseThreadPool.sharedInstance().getThreadPool().execute(connection);

		return null;
	}

	@Override
	protected boolean shouldReturnResponse() {
		return false;
	}

}
