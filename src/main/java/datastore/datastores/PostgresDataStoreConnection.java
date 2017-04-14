package datastore.datastores;

import java.util.HashMap;
import java.util.Map;
import java.awt.List;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.*;

import services.RequestHandle;
import thread_pools.DatabaseThreadPool;
import utility.ResponseCodes;

import com.google.gson.JsonObject;

import datastore.DataStoreConnection;
import datastore.DataStoreConnectionFactory;

public class PostgresDataStoreConnection extends DataStoreConnection {

	Connection db;
	private SecureRandom random = new SecureRandom();

	public String nextSessionId() {
		return new BigInteger(130, random).toString(32);
	}

	public void init(Map<String, Object> parameters) {
		String url = (String) parameters.get("databaseURL");
		String username = (String) parameters.get("databaseURL");
		String password = (String) parameters.get("databaseURL");

		try {
			db = DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public StringBuffer execute(Map<String, Object> parameters)
			throws Exception {
		String action = (String) parameters.get("action");
		if (action == "loginUser") {
			String email = (String) parameters.get("email");
			String hashedPassword = (String) parameters.get("password");
			// hashedpassword should be decrypted
			return loginUser(email, hashedPassword);
		}

		if (action == "signupUser") {
			String email = (String) parameters.get("email");
			String hashedPassword = (String) parameters.get("passWord");
			String firstName = (String) parameters.get("firstName");
			String lastName = (String) parameters.get("lastName");
			Date dateOfBirth = (Date) parameters.get("dateOfBirth");
			Timestamp createdAt = (Timestamp) parameters.get("createdAt");

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
			String hashedPassword = (String) parameters.get("passWord");
			String newPassword = (String) parameters.get("newpassWord");
			String firstName = (String) parameters.get("firstName");
			String newFirstName = (String) parameters.get("newfirstName");
			String lastName = (String) parameters.get("lastName");
			String newLastName = (String) parameters.get("newlastName");
			Date dateOfBirth = (Date) parameters.get("dateOfBirth");
			Timestamp createdAt = (Timestamp) parameters.get("createdAt");

			editProfile( email, hashedPassword,
					 newPassword,  firstName,newFirstName,
					 lastName,  newLastName,  dateOfBirth,
					 createdAt);

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
			return logoutUser();
		}

		return null;
	}

	private StringBuffer loginUser(String email, String hashedPassword)
			throws InstantiationException, IllegalAccessException {
		Statement con = null;
		JsonObject json = new JsonObject();

		try {
			con = db.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ResultSet result;
		try {
			result = con.executeQuery("SELECT * FROM member WHERE email = "
					+ email + "AND password_hash = " + hashedPassword);
			result.beforeFirst();

			json.addProperty("status", ResponseCodes.STATUS_OK);
			if (result.next()) {
				int id = result.getInt("id");
				// generate a random string (session)
				String session = nextSessionId();
				// connect to shared cache.
				Class<?> connectionClass = DataStoreConnectionFactory
						.sharedInstance().getDataStoreConnection(
								"Redis_datastore_connection");
				DataStoreConnection connection = (DataStoreConnection) connectionClass
						.newInstance();
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("action", "addSession");
				// add to hashmap, randomly generated string as a key, and value
				// the member id.
				parameters.put("sessionId", session);
				parameters.put("userId", id);

				RequestHandle requestHandle = (RequestHandle) this.parameters
						.get(RequestHandle.class.getSimpleName());
				parameters.put(RequestHandle.class.getSimpleName(),
						requestHandle);
				connection.init(parameters);
				DatabaseThreadPool.sharedInstance().getThreadPool()
						.execute(connection);

				json.addProperty("loginStatus", "Success");
				json.addProperty("userId", id);
				json.addProperty("sessionId", session);

			} else {
				json.addProperty("loginStatus", "Failed");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new StringBuffer(json.toString());

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
		ResultSet result;
		try {
			result = con
					.executeQuery("INSERT INTO `member`(id,email,password_hash,first_name,last_name,date_of_birth,created_at) VALUES ('?','"
							+ email
							+ "','"
							+ hashedPassword
							+ "',"
							+ firstName
							+ ",'"
							+ lastName
							+ ",'"
							+ dateOfBirth
							+ ",'"
							+ createdAt + "')");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JsonObject json = new JsonObject();
		json.addProperty("status", ResponseCodes.STATUS_OK);
		return new StringBuffer(json.toString());
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
		JsonObject json = new JsonObject();
		json.addProperty("status", ResponseCodes.STATUS_OK);
		return new StringBuffer(json.toString());
	}

	private StringBuffer getUser(int userID) {
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
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JsonObject json = new JsonObject();
		json.addProperty("status", ResponseCodes.STATUS_OK);
		return new StringBuffer(json.toString());
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
		JsonObject json = new JsonObject();
		json.addProperty("status", ResponseCodes.STATUS_OK);
		return new StringBuffer(json.toString());
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
		JsonObject json = new JsonObject();
		json.addProperty("status", ResponseCodes.STATUS_OK);
		return new StringBuffer(json.toString());
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
		JsonObject json = new JsonObject();
		json.addProperty("status", ResponseCodes.STATUS_OK);
		return new StringBuffer(json.toString());
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
		JsonObject json = new JsonObject();
		json.addProperty("status", ResponseCodes.STATUS_OK);
		return new StringBuffer(json.toString());
	}

	private StringBuffer logoutUser() {
		Statement con = null;
		try {
			con = db.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ResultSet result;
		try {
			result = con.executeQuery("DELETE from ");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JsonObject json = new JsonObject();
		json.addProperty("status", ResponseCodes.STATUS_OK);
		return new StringBuffer(json.toString());
	}

}