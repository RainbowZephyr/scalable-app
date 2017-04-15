package command.commands;

import java.util.HashMap;
import java.util.Map;

import services.RequestHandle;
import thread_pools.DatabaseThreadPool;
import command.Command;
import datastore.DataStoreConnection;
import datastore.DataStoreConnectionFactory;

public class UserAcceptFriendCommand extends Command {

	@Override
	protected StringBuffer execute(Map<String, Object> requestMapData)
			throws Exception {

		int user1id = (Integer) requestMapData.get("user1ID");
		int user2id = (Integer) requestMapData.get("user2ID");
		Class<?> connectionClass = DataStoreConnectionFactory.sharedInstance()
				.getDataStoreConnection("Postgresql_database_connection");
		DataStoreConnection connection = (DataStoreConnection) connectionClass
				.newInstance();

		// Map<String, Object> parameters = new HashMap<String, Object>();
		requestMapData.put("action", "acceptFriendRequest");
		// parameters.put("user1ID", user1id);
		// parameters.put("user2ID", user2id);

		RequestHandle requestHandle = (RequestHandle) this.parameters
				.get(RequestHandle.class.getSimpleName());
		requestMapData.put(RequestHandle.class.getSimpleName(), requestHandle);
		connection.init(requestMapData);
		DatabaseThreadPool.sharedInstance().getThreadPool().execute(connection);

		return null;
	}

	@Override
	protected boolean shouldReturnResponse() {
		return false;
	}
}
