package datastore;

import java.util.HashMap;
import java.util.Map;

public class DataStoreConnectionFactory {
    final private static DataStoreConnectionFactory instance = new DataStoreConnectionFactory();
    final private static Map<String, Class<?>> dataStoreConnectionHtbl =
            new HashMap<String, Class<?>>();

    private DataStoreConnectionFactory() {
    }

    public static DataStoreConnectionFactory sharedInstance() {
        return instance;
    }

    public Class<?> getDataStoreConnection(String id) {
        return dataStoreConnectionHtbl.get(id);
    }

    public void registerDataStoreConnection(String id,
                                            Class<?> dataStoreConnection) {
        dataStoreConnectionHtbl.put(id, dataStoreConnection);
    }
}
