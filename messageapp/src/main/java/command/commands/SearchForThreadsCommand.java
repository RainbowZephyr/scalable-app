package command.commands;

import command.Command;
import datastore.DataStoreConnection;
import datastore.DataStoreConnectionFactory;
import services.RequestHandle;
import thread_pools.DatabaseThreadPool;

import java.util.HashMap;
import java.util.Map;

public class SearchForThreadsCommand extends Command{

	@Override
	protected StringBuffer execute(Map<String, Object> requestMapData) throws Exception {
		Class<?> connectionClass = DataStoreConnectionFactory.sharedInstance()
                .getDataStoreConnection("mongodb_data_store_connection");
        DataStoreConnection connection = (DataStoreConnection) connectionClass.newInstance();

        requestMapData.put("action", "searchForThreads");

        connection.init(requestMapData);
        DatabaseThreadPool.sharedInstance().getThreadPool().execute(connection);
        return null;
	}
	
    @Override
    protected boolean shouldReturnResponse() {
        return false;
    }
	
}
