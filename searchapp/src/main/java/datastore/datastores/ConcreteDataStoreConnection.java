package datastore.datastores;


import datastore.DataStoreConnection;
import services.Response;
import utility.ResponseCodes;

import java.util.Map;

/* This is an example class (you should create a similar one to this if your application uses
* SQL Database Connection OR NoSQL Database Connection
* THIS IS MAINLY FOR DECOUPLING , Don't Forget to add its name to the config/data_store.properties
* */
public class ConcreteDataStoreConnection extends DataStoreConnection {

    public StringBuffer execute(Map<String, Object> parameters) {
        System.out.println("ECHO EXECUTED, CHECK the Response Queue");
        Response response = new Response(ResponseCodes.STATUS_CREATED);
        return response.toJson();
    }
}
