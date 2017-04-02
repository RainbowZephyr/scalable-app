package services;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import com.zaxxer.hikari.HikariDataSource;


abstract class Command implements Runnable{

    protected ServiceRequest _serviceRequest; // the formatted request
    private RequestHandle _serviceHandle; // handles the response for the user
    protected HikariDataSource _postgresDataSource;  //to get a postgres connection, call getPostgresConnection()

    public void init(HikariDataSource postgresDataSource, RequestHandle serviceHandle,
                     ServiceRequest serviceRequest ){
    	_postgresDataSource = postgresDataSource;
        _serviceRequest	= serviceRequest;
        _serviceHandle = serviceHandle;
    }

    public void run() {
        try {
            Map<String, Object> map = _serviceRequest.getData();
            StringBuffer strbufResponse = execute(map);
            if(_serviceHandle != null){ // can be null in case of request coming from controller
                _serviceHandle.send(strbufResponse); // sends back the response to the requester
            }
        } catch (Exception exp) {
            System.err.println(exp.toString());
        }
    }
    
    protected StringBuffer makeJSONResponseEnvelope(int nResponse,
                                                    StringBuffer strbufRequestData,
                                                    StringBuffer strbufResponseData){
        StringBuffer    		strbufJSON;
        String					strStatusMsg;
        String					strData = "";
        Map<String, Object>		mapInputData;
        String 					strKey;

        strbufJSON    	= new StringBuffer( );
        strbufJSON.append( "{" );
        strbufJSON.append( "\"responseTo\":\"" +  _serviceRequest.getAction( ) + "\","  );
        if( _serviceRequest.getSessionID( ) != null )
            strbufJSON.append( "\"sessionID\":\""  +  _serviceRequest.getSessionID( ) + "\","  );

        strbufJSON.append( "\"StatusID\":\""     +  nResponse + "\","  );
        strStatusMsg  = (String) ResponseCodes.getMessage( Integer.toString( nResponse ) );
        strbufJSON.append( "\"StatusMsg\":\""     +  strStatusMsg + "\","  );

        if( strbufRequestData != null )
            strbufJSON.append( "\"requestData\":{" +  strbufRequestData + "},"   );

        if( strbufResponseData != null ){
            if( strbufResponseData.charAt( 0 ) == '[' )
                strbufJSON.append( "\"responseData\":" +  strbufResponseData  );	// if it is a list, no curley
            else
                strbufJSON.append( "\"responseData\":{" +  strbufResponseData  + "}"  );
        }
        if( strbufJSON.charAt( strbufJSON.length( ) - 1 ) == ',' )
            strbufJSON.deleteCharAt( strbufJSON.length( ) - 1 );

        strbufJSON.append( "}" );
        return strbufJSON;
    }

    protected StringBuffer serializeMaptoJSON( Map<String, Object>  map, ArrayList arrFieldstoKeep ){

        StringBuffer strbufData;

        strbufData = new StringBuffer( );
        if( arrFieldstoKeep == null ){
            for( Map.Entry<String, Object> entry : map.entrySet())
                strbufData.append(  "\"" + entry.getKey() + "\":\"" + entry.getValue().toString( )  + "\"," );
        }
        else{
            for( Map.Entry<String, Object> entry : map.entrySet()){
                if( arrFieldstoKeep.contains( entry.getKey( ) ) )
                    strbufData.append(  "\"" + entry.getKey() + "\":\"" + entry.getValue().toString( )  + "\"," );
            }
        }

        if( strbufData.charAt( strbufData.length( ) - 1  ) == ',' )
            strbufData.setLength( strbufData.length( ) - 1 );

        return strbufData;
    }

    protected Connection getPostgresConnection() throws SQLException{
    	Connection connection = null;
    	try{
    		connection =  _postgresDataSource.getConnection( );
    	} catch(Exception e){
    		 System.err.println( e.toString( ) );
    	} finally{
    		closeConnectionQuietly( connection );
    	}
    	return connection;
    }
    
	protected void closeConnectionQuietly( Connection	connection ){
		try{
			if( connection != null )
				connection.close( );
		}
		catch( Exception exp ){
			// log this...
			exp.printStackTrace( );
		}
	}

    public abstract StringBuffer execute(Map<String, Object> requestMapData ) throws Exception;
}