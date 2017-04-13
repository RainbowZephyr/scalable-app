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
			String username = (String) parameters.get("email");
			//do same
			return loginUser(username, hashedPassword)
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

}
