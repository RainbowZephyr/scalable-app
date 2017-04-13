package services;

import java.util.Map;

public class SetMaxThreadCount extends Command {
    public StringBuffer execute(Map<String, Object> requestMapData) throws Exception {
        String obj = (String)requestMapData.get(Constants.MAX_THREAD_COUNT);
        int maxPoolSize = Integer.parseInt(obj);
        Dispatcher.sharedInstance().get_threadPoolCmds().setCorePoolSize(maxPoolSize);
        return null;
    }
}
