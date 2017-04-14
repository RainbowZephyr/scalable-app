package commands.user;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import services.RequestHandle;
import thread_pools.DatabaseThreadPool;
import command.Command;
import datastore.DataStoreConnection;
import datastore.DataStoreConnectionFactory;

public class EditProfileCommand extends Command {

	@Override
	protected StringBuffer execute(Map<String, Object> requestMapData)
			throws Exception {
		
		String email = (String) requestMapData.get("email");
		String hashedPassword = (String) requestMapData.get("oldpassword");
		String newPassword = (String) requestMapData.get("newpassword");
		String firstName = (String) requestMapData.get("firstName");
		String newFirstName = (String) requestMapData.get("newfirstName");

		String lastName = (String) requestMapData.get("lastName");
		String newLastName = (String) requestMapData.get("newlastName");

		Date dateOfBirth = (Date) requestMapData.get("dateOfBirth");
		Timestamp createdAt = (Timestamp) requestMapData.get("createdAt");
		
		Class<?> connectionClass = DataStoreConnectionFactory.sharedInstance()
				.getDataStoreConnection("Postgresql_database_connection");
		DataStoreConnection connection = (DataStoreConnection) connectionClass
				.newInstance();

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("action", "editProfile");
		parameters.put("email", email);
		parameters.put("hashedPassword", hashedPassword);
		parameters.put("newpassword", newPassword);
		parameters.put("firstName", firstName);
		parameters.put("newfirstName", newFirstName);
		parameters.put("lastName", lastName);
		parameters.put("newlastName", newLastName);
		parameters.put("dateOfBirth", dateOfBirth);
		parameters.put("createdAt", createdAt);


		RequestHandle requestHandle = (RequestHandle) this.parameters
				.get(RequestHandle.class.getSimpleName());
		parameters.put(RequestHandle.class.getSimpleName(), requestHandle);
		connection.init(parameters);
		DatabaseThreadPool.sharedInstance().getThreadPool().execute(connection);

		return null;
		
		
		
		
	
	}

}
