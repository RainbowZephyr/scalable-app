package services;

import java.util.Map;

/**
 * Created by abdoo on 4/1/17.
 */
public class FreezeCommand extends Command{
    public StringBuffer execute(Map<String, Object> requestMapData) throws Exception {
        Dispatcher.sharedInstance().get_threadPoolCmds().shutdown();
        return null;
    }
}
