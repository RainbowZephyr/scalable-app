package command.commands;

import java.util.Map;

import command.Command;
import datastore.DataStoreConnection;
import datastore.DataStoreConnectionFactory;
import datastore.datastores.MongoDBStoreConnection;

public class DeletePostsCommand extends Command {

    @Override
    protected StringBuffer execute(Map<String, Object> requestMapData) throws Exception {

        // Add DB options if required


//		requestMapData.put("action", "delete");
//		requestMapData.put("collection_name", "posts");
//
//		MongoDBStoreConnection DBConnection = new MongoDBStoreConnection();
//		DBConnection.execute(requestMapData);

        Class<?> connectionClass = DataStoreConnectionFactory.sharedInstance()
                .getDataStoreConnection("mongo_db_data_store_connection");
        DataStoreConnection connection = (DataStoreConnection) connectionClass.newInstance();
        requestMapData.put("action", "delete");
        requestMapData.put("collection_name", "posts");
        connection.execute(requestMapData);


        return null;
    }

    @Override
    protected boolean shouldReturnResponse() {
        return false;
    }

}
