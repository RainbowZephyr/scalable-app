package command.commands;

import command.Command;
import datastore.DataStoreConnection;
import datastore.DataStoreConnectionFactory;
import services.Response;
import sun.security.provider.certpath.OCSPResponse;
import thread_pools.DatabaseThreadPool;
import utility.ResponseCodes;

import java.util.Map;

public class SASearchByNameCommand extends Command {

	@Override
	protected StringBuffer execute(Map<String, Object> requestMapData) throws Exception {
		Class<?> connectionClass = DataStoreConnectionFactory.sharedInstance()
				.getDataStoreConnection("titan_data_store_connection");
		DataStoreConnection connection = (DataStoreConnection) connectionClass.newInstance();

		requestMapData.put("action", "search_by_name");
		
		connection.init(requestMapData);
		DatabaseThreadPool.sharedInstance().getThreadPool().execute(connection);
		Response response = new Response(ResponseCodes.STATUS_OK);

		return null;
	}

	@Override
	protected boolean shouldReturnResponse() {
		return false;
	}
}
