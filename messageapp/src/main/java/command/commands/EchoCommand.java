package command.commands;

import command.Command;
import datastore.DataStoreConnection;
import datastore.DataStoreConnectionFactory;
import thread_pools.DatabaseThreadPool;

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
        connection.init(mapUserData);
        DatabaseThreadPool.sharedInstance().getThreadPool().execute(connection);
//        Thread.sleep(5000);
        return null;
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
        return false;
    }
}
