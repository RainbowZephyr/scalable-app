package command.commands;

import java.util.Map;

import command.Command;
import datastore.DataStoreConnection;
import datastore.DataStoreConnectionFactory;
import thread_pools.DatabaseThreadPool;

public class SASearchByNameCommand extends Command {

	@Override
	protected StringBuffer execute(Map<String, Object> requestMapData) throws Exception {
		Class<?> connectionClass = DataStoreConnectionFactory.sharedInstance()
				.getDataStoreConnection("titan_data_store_connection");
		DataStoreConnection connection = (DataStoreConnection) connectionClass.newInstance();
		
		requestMapData.put("action", "search_by_name");
		
		connection.init(requestMapData);
		DatabaseThreadPool.sharedInstance().getThreadPool().execute(connection);
		
		return null;
	}

	@Override
	protected boolean shouldReturnResponse() {
		return false;
	}
}
