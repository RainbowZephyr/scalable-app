package services;

import java.util.ArrayList;
import java.util.Map;


abstract class Command implements Runnable{

    protected ServiceRequest _serviceRequest; // the formatted request
    private RequestHandle _serviceHandle; // handles the response for the user
    // should have a data source to access the database

    public void init(RequestHandle serviceHandle,
                     ServiceRequest serviceRequest ){
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


    public abstract StringBuffer execute(Map<String, Object> requestMapData ) throws Exception;
}