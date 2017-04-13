package command.commands;

import java.util.Map;

import command.Command;
import datastore.datastores.MongoDBStoreConnection;

public class UpdatePostCommand extends Command {

	@Override
	protected StringBuffer execute(Map<String, Object> requestMapData) throws Exception {
		
		// Add DB options if required
		
		
		requestMapData.put("action", "update");
		requestMapData.put("collection_name", "posts");
		
		MongoDBStoreConnection DBConnection = new MongoDBStoreConnection();
		DBConnection.execute(requestMapData);
		
		
		
		return null;
	}
	
	@Override
	protected boolean shouldReturnResponse(){
		return false;
	}

}
