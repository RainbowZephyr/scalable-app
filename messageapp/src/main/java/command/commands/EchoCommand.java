package command.commands;

import command.Command;
import datastore.DataStoreConnection;
import datastore.DataStoreConnectionFactory;
import services.Response;
import utility.ResponseCodes;

import java.util.Map;

public class EchoCommand extends Command {
    /**
     * TEST COMMAND CLASS
     *
     * @param mapUserData
     * @return
     * @throws Exception
     */
    public StringBuffer execute(Map<String, Object> mapUserData) throws Exception {
        Class<?> connectionClass = DataStoreConnectionFactory.sharedInstance()
                .getDataStoreConnection("concrete_data_store_connection");
        DataStoreConnection connection = (DataStoreConnection) connectionClass.newInstance();

        mapUserData.put("action", "query"); // or send elli enta 3awzo
        connection.init(mapUserData);
//        DatabaseThreadPool.sharedInstance().getThreadPool().execute(connection);
//        Thread.sleep(5000);
        Response response = new Response(ResponseCodes.STATUS_OK);
        return response.toJson();
    }

    /**
     * this means that the current thread won't be responsible for the
     * response but another thread (here the case is that database connection thread
     * is the one responsible)
     *
     * @return
     */
    @Override
    protected boolean shouldReturnResponse() {
        return true;
    }
}
