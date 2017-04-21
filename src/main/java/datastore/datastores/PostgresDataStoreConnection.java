package datastore.datastores;



import datastore.DataStoreConnection;
import datastore.DataStoreConnectionFactory;
import services.Response;
import thread_pools.DatabaseThreadPool;
import utility.ResponseCodes;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PostgresDataStoreConnection extends DataStoreConnection {

	Connection db;
	private SecureRandom random = new SecureRandom();

	public String nextSessionId() {
		return new BigInteger(130, random).toString(32);
	}


	@Override
	public StringBuffer execute(Map<String, Object> parameters)
			throws Exception {
		LoadPostGresProperties();
		String action = (String) parameters.get("action");
		if (action == "loginUser") {
			String email = (String) parameters.get("email");
			String hashedPassword = (String) parameters.get("password");
			// hashedpassword should be decrypted
			return loginUser(email, hashedPassword);
		}

		if (action == "signupUser") {
			String email = (String) parameters.get("email");
			String hashedPassword = (String) parameters.get("password");
			String firstName = (String) parameters.get("firstName");
			String lastName = (String) parameters.get("lastName");
			String dateString = (String) parameters.get("dateOfBirth");
			Date dateOfBirth = Date.valueOf(dateString);
			String createdAtString = (String) parameters.get("createdAt");
			Timestamp createdAt = Timestamp.valueOf(createdAtString);

			return signupUser(email, hashedPassword, firstName, lastName,
					dateOfBirth, createdAt);
		}

		if (action == "removeFriend") {
			int user1id = (Integer) parameters.get("user1ID");
			int user2id = (Integer) parameters.get("user2ID");

			return removeFriend(user1id, user2id);
		}

		if (action == "getUser") {
			int userID = (Integer) parameters.get("user_id");

			return getUser(userID);
		}
		if (action == "editProfile") {
			String email = (String) parameters.get("email");
			String hashedPassword = (String) parameters.get("oldpassword");
			String newPassword = (String) parameters.get("newpassword");
			String firstName = (String) parameters.get("firstName");
			String newFirstName = (String) parameters.get("newfirstName");
			String lastName = (String) parameters.get("lastName");
			String newLastName = (String) parameters.get("newlastName");
			Date dateOfBirth = (Date) parameters.get("dateOfBirth");
			Timestamp createdAt = (Timestamp) parameters.get("createdAt");

			editProfile(email, hashedPassword, newPassword, firstName,
					newFirstName, lastName, newLastName, dateOfBirth, createdAt);

		}

		if (action == "declineFriendRequest") {
			int user1id = (Integer) parameters.get("user1ID");
			int user2id = (Integer) parameters.get("user2ID");

			return declineFriend(user1id, user2id);
		}

		if (action == "addFriend_request") {
			int user1id = (Integer) parameters.get("user1ID");
			int user2id = (Integer) parameters.get("user2ID");

			return addFriend(user1id, user2id);
		}

		if (action == "acceptFriendRequest") {
			int user1id = (Integer) parameters.get("user1ID");
			int user2id = (Integer) parameters.get("user2ID");

			return acceptFriend(user1id, user2id);
		}

		if (action == "logOut_request") {

			int sessionID = (Integer) parameters.get("sessionID");
			return logoutUser(sessionID);
		}

		return null;
	}

	protected void LoadPostGresProperties() throws IOException, ClassNotFoundException {
		        Properties prop = new Properties();
		        InputStream in = new FileInputStream("config/postgres_config.properties");
		        prop.load(in);
		        in.close();
		        String dbUrl = "jdbc:postgresql://";
		        String username = prop.get("username").toString();
		        String password = prop.get("password").toString();
		        String host = prop.get("hostname").toString();
		        String port = prop.get("port").toString();
		        dbUrl = dbUrl+host+":"+port+"/"+prop.get("dbname");
		        JDBC jdbc = new JDBC();
		        db = jdbc.Connect(dbUrl,username, password);
		    }

	private StringBuffer loginUser(String email, String hashedPassword)
			throws InstantiationException, IllegalAccessException {
		Statement con = null;
		// Response response = new Response(ResponseCodes.STATUS_OK);

		try {
			con = db.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ResultSet result;
		Response response = null;
		try {
			result = con.executeQuery("SELECT * FROM member WHERE email = "
					+ email + "AND password_hash = " + hashedPassword + "LIMIT 1");
			result.beforeFirst();
			response = new Response(ResponseCodes.STATUS_OK);
			if (result.next()) {
				int id = result.getInt("id");
				// generate a random string (session)
				String session = nextSessionId();
				// connect to shared cache.
				Class<?> connectionClass = DataStoreConnectionFactory
						.sharedInstance().getDataStoreConnection(
								"redis_data_store_connection");
				DataStoreConnection connection = (DataStoreConnection) connectionClass
						.newInstance();
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("action", "addSession");
				// add to hashmap, randomly generated string as a key, and value
				// the member id.
				parameters.put("sessionId", session);
				parameters.put("userId", id);

				connection.init(parameters);
				DatabaseThreadPool.sharedInstance().getThreadPool()
						.execute(connection);
				response.addToResponse("userId", id);
				response.addToResponse("sessionId", session);

			} else {
				response = new Response(ResponseCodes.STATUS_NOT_FOUND);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response.toJson();

	}

	private StringBuffer signupUser(String email, String hashedPassword,
			String firstName, String lastName, Date dateOfBirth,
			Timestamp createdAt) {
		Statement con = null;
		try {
			con = db.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Response response;
		try {
			String query = "INSERT INTO member(email,password_hash,first_name,last_name,date_of_birth,created_at) " +
					"VALUES ("
					+ "'" + email + "',"
					+ "'" + hashedPassword + "',"
					+ "'" + firstName + "',"
					+ "'" + lastName + "',"
					+ "'" + dateOfBirth + "',"
					+ "'" + createdAt + "')";
			con.execute(query); // use Execute if inserting (executeQuery otherwise)
			response = new Response(ResponseCodes.STATUS_OK);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response = new Response(ResponseCodes.STATUS_DATA_BASE_ERROR);
		}
		return response.toJson();
	}

	private StringBuffer removeFriend(int user1id, int user2id) {
		Statement con = null;
		try {
			con = db.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ResultSet result;
		try {
			result = con.executeQuery("DELETE FROM friends WHERE user_1 = "
					+ user1id + "AND user_2 =" + user2id);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Response response = new Response(ResponseCodes.STATUS_OK);
		return response.toJson();
	}

	private StringBuffer getUser(int userID) {
		Response response = null;
		Statement con = null;
		try {
			con = db.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ResultSet result;
		try {
			result = con.executeQuery("SELECT * FROM member WHERE  id  = "
					+ userID);
			result.beforeFirst();

			if (result.next()) {
				String email = result.getString("email");
				String firstName = result.getString("first_name");
				String lastName = result.getString("last_name");
				String dateOfBirth = result.getString("date_of_birth");
				response = new Response(ResponseCodes.STATUS_OK);
				response.addToResponse("email", email);
				response.addToResponse("firstName", firstName);
				response.addToResponse("lastName", lastName);
				response.addToResponse("dateOfBirth", dateOfBirth);

			} else {
				response = new Response(ResponseCodes.STATUS_NOT_FOUND);

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return response.toJson();
	}

	private StringBuffer editProfile(String email, String hashedPassword,
			String newPassword, String firstName, String newFirstname,
			String lastName, String newLastname, Date dateOfBirth,
			Timestamp createdAt) {
		Statement con = null;
		try {
			con = db.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ResultSet result;
		try {
			result = con.executeQuery("UPDATE member SET password_hash ="
					+ newPassword + "AND first_name = " + newFirstname
					+ "AND last_name =" + newLastname + "WHERE first_name= "
					+ firstName + "AND last_name=" + lastName + "AND email ="
					+ email);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Response response = new Response(ResponseCodes.STATUS_OK);
		return response.toJson();
	}

	private StringBuffer declineFriend(int user1id, int user2id) {
		Statement con = null;
		try {
			con = db.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ResultSet result;
		try {
			result = con
					.executeQuery("UPDATE friends SET accepted = 0 WHERE  user_1 ="
							+ user1id + "AND user_2 = " + user2id);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Response response = new Response(ResponseCodes.STATUS_OK);
		return response.toJson();
	}

	private StringBuffer addFriend(int user1id, int user2id) {
		Statement con = null;
		try {
			con = db.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ResultSet result;
		try {
			result = con
					.executeQuery("INSERT INTO `friends`(user_1,user_2,accepted) VALUES ("
							+ user1id + "','" + user2id + "',' 0" + "')");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Response response = new Response(ResponseCodes.STATUS_OK);
		return response.toJson();
	}

	private StringBuffer acceptFriend(int user1id, int user2id) {
		Statement con = null;
		try {
			con = db.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ResultSet result;
		try {
			result = con
					.executeQuery("UPDATE friends SET accepted = 1 WHERE  user_1 ="
							+ user1id + "AND user_2 = " + user2id);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Response response = new Response(ResponseCodes.STATUS_OK);
		return response.toJson();
	}

	private StringBuffer logoutUser(int sessionID)
			throws InstantiationException, IllegalAccessException {
		Statement con = null;
		try {
			con = db.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Class<?> connectionClass = DataStoreConnectionFactory.sharedInstance()
				.getDataStoreConnection("redis_data_store_connection");
		DataStoreConnection connection = (DataStoreConnection) connectionClass
				.newInstance();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("action", "removeSession");
		// add to hashmap, randomly generated string as a key, and value
		// the member id.
		parameters.put("sessionId", sessionID);

		connection.init(parameters);
		DatabaseThreadPool.sharedInstance().getThreadPool().execute(connection);
		Response response = new Response(ResponseCodes.STATUS_OK);
		return response.toJson();
	}

}
