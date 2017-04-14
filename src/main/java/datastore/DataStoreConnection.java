package datastore;

import services.RequestHandle;

import java.util.Map;

public abstract class DataStoreConnection implements Runnable {
    Map<String, Object> parameters;

    /**
     * This method is used to perform any action on the data store itself.
     * For example, if connecting to the data store, first handle that in the
     * execute function also, a query, pass {"action" : "query", "query" : queryToExecute}
     * note that queryToExecute is anything (Object).
     * Since that runs in a seperate thread, make sure to handle everything in the execute
     * for example if handling multiple Queries, first connect to the DB then execute the queries
     * Simply put, pass everything needed to perform a specific action on the data store.
     *
     * @param parameters Data required to execute an action.
     */
    public abstract StringBuffer execute(Map<String, Object> parameters) throws Exception;

    final public void run() {
        try {
            StringBuffer strBuffer = execute(parameters);
            RequestHandle requestHandle = (RequestHandle)
                    parameters.get(RequestHandle.class.getSimpleName());
//            requestHandle.send(strBuffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
}
