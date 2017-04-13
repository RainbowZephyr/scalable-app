package datastore.datastores;

import java.util.Map;
import java.sql.*;

import utility.ResponseCodes;

import com.google.gson.JsonObject;

import datastore.DataStoreConnection;

public class PostgresDataStoreConnection extends DataStoreConnection {

	Connection db;
	
    public void init(Map<String, Object> parameters) {
    	String url = (String)parameters.get("databaseURL");
    	String username = (String)parameters.get("databaseURL");
    	String password = (String)parameters.get("databaseURL");
    	
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
		if(action == "loginUser"){
			String email = (String) parameters.get("email");
			String hashedPassword = (String) parameters.get("passWord");
			//hashedpassword should be decrypted
			return loginUser(email, hashedPassword);
		}
		
		if(action == "signupUser"){
			String email = (String) parameters.get("email");
			String hashedPassword = (String) parameters.get("passWord");
			String phoneNumber = (String) parameters.get("phoneNumber");
			String firstName = (String) parameters.get("firstName");
			String lastName = (String) parameters.get("lastName");
			String age = (String) parameters.get("age");
			String gender = (String) parameters.get("gender");
			
			
			return signupUser(email, hashedPassword,phoneNumber, firstName,lastName,age, gender);
		}
		
		if(action == "removeFriend"){
			String friendEmail = (String) parameters.get("friendEmail");
			
			return removeFriend (friendEmail);
		}
		
		if(action == "getUser"){
			int userID= (Integer) parameters.get("user_id");
			
			return getUser (userID);
		}
		if(action == "editProfile"){
			String email = (String) parameters.get("email");
			String hashedPassword = (String) parameters.get("passWord");
			String newPassword = (String) parameters.get("newpassWord");
			String phoneNumber = (String) parameters.get("phoneNumber");
			String firstName = (String) parameters.get("firstName");
			String lastName = (String) parameters.get("lastName");
			String age = (String) parameters.get("age");
			String gender = (String) parameters.get("gender");
			
			editProfile(email, hashedPassword,newPassword,phoneNumber, firstName,lastName,age, gender);
			
		}
		
		if(action == "declineFriendRequest"){
			String friendEmail= (String) parameters.get("friendEmail");
			
			return declineFriend (friendEmail);
		}
		
		if(action == "addFriend_request"){
			String friendEmail= (String) parameters.get("friendEmail");
			
			return addFriend (friendEmail);
		}
		
		if(action == "acceptFriendRequest"){
			String friendEmail= (String) parameters.get("friendEmail");
			
			return acceptFriend (friendEmail);
		}
		
		if(action == "logOut_request"){
			return logoutUser ();
		}
		
		return null;
	}
	
	private StringBuffer loginUser(String username,String hashedPassword){
		Statement con = null;
		try {
			con = db.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ResultSet result;
		try {
			result = con.executeQuery("SELECT *");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JsonObject json = new JsonObject(); 
		json.addProperty("status", ResponseCodes.STATUS_OK);
		return new StringBuffer(json.toString());
	}
	
	private StringBuffer signupUser(String username,String hashedPassword , String phoneNumber,String firstName, String lastName, String age, String gender){
		Statement con = null;
		try {
			con = db.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ResultSet result;
		try {
			result = con.executeQuery("SELECT *");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JsonObject json = new JsonObject(); 
		json.addProperty("status", ResponseCodes.STATUS_OK);
		return new StringBuffer(json.toString());
	}
	
	
	private StringBuffer removeFriend(String friendEmail){
		Statement con = null;
		try {
			con = db.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ResultSet result;
		try {
			result = con.executeQuery("SELECT *");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JsonObject json = new JsonObject(); 
		json.addProperty("status", ResponseCodes.STATUS_OK);
		return new StringBuffer(json.toString());
	}
	
	
	private StringBuffer getUser(int userID){
		Statement con = null;
		try {
			con = db.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ResultSet result;
		try {
			result = con.executeQuery("SELECT *");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JsonObject json = new JsonObject(); 
		json.addProperty("status", ResponseCodes.STATUS_OK);
		return new StringBuffer(json.toString());
	}
	
	
	private StringBuffer editProfile(String username,String hashedPassword ,String newpassWord, String phoneNumber,String firstName, String lastName, String age, String gender){
		Statement con = null;
		try {
			con = db.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ResultSet result;
		try {
			result = con.executeQuery("SELECT *");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JsonObject json = new JsonObject(); 
		json.addProperty("status", ResponseCodes.STATUS_OK);
		return new StringBuffer(json.toString());
	}
	
	private StringBuffer declineFriend(String friendEmail){
		Statement con = null;
		try {
			con = db.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ResultSet result;
		try {
			result = con.executeQuery("SELECT *");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JsonObject json = new JsonObject(); 
		json.addProperty("status", ResponseCodes.STATUS_OK);
		return new StringBuffer(json.toString());
	}
	
	private StringBuffer addFriend(String friendEmail){
		Statement con = null;
		try {
			con = db.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ResultSet result;
		try {
			result = con.executeQuery("SELECT *");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JsonObject json = new JsonObject(); 
		json.addProperty("status", ResponseCodes.STATUS_OK);
		return new StringBuffer(json.toString());
	}
	
	
	private StringBuffer acceptFriend(String friendEmail){
		Statement con = null;
		try {
			con = db.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ResultSet result;
		try {
			result = con.executeQuery("SELECT *");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JsonObject json = new JsonObject(); 
		json.addProperty("status", ResponseCodes.STATUS_OK);
		return new StringBuffer(json.toString());
	}

	private StringBuffer logoutUser(){
		Statement con = null;
		try {
			con = db.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ResultSet result;
		try {
			result = con.executeQuery("SELECT *");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JsonObject json = new JsonObject(); 
		json.addProperty("status", ResponseCodes.STATUS_OK);
		return new StringBuffer(json.toString());
	}
	
}