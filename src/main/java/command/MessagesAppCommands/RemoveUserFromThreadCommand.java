package command.MessagesAppCommands;

import java.util.HashMap;
import java.util.Map;

import command.Command;
import datastore.DataStoreConnection;
import datastore.DataStoreConnectionFactory;
import services.RequestHandle;
import thread_pools.DatabaseThreadPool;

public class RemoveUserFromThreadCommand extends Command
{

	@Override
	protected StringBuffer execute(Map<String, Object> requestMapData) throws Exception
	{
		Class<?> connectionClass = DataStoreConnectionFactory.sharedInstance()
				.getDataStoreConnection("mongodb_data_store_connection");

		DataStoreConnection connection = (DataStoreConnection) connectionClass.newInstance();

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("action", "removeUserFromThread");

		String threadId = requestMapData.get("threadId").toString();
		parameters.put("threadId", threadId);

		String userId = requestMapData.get("userId").toString();
		parameters.put("userId", userId);

		RequestHandle requestHandle = (RequestHandle) this.parameters.get(RequestHandle.class.getSimpleName());
		parameters.put(RequestHandle.class.getSimpleName(), requestHandle);
		connection.init(parameters);
		DatabaseThreadPool.sharedInstance().getThreadPool().execute(connection);
		return null;
	}

}
