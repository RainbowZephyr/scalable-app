package command.commands;

import command.Command;
import utility.Constants;
import services.Dispatcher;

import java.util.Map;

public class SetMaxThreadCountCommand extends Command {
    public StringBuffer execute(Map<String, Object> requestMapData) throws Exception {
        String obj = (String)requestMapData.get(Constants.MAX_THREAD_COUNT);
        int maxPoolSize = Integer.parseInt(obj);
        Dispatcher.sharedInstance().get_threadPoolCmds().setCorePoolSize(maxPoolSize);
        return null;
    }
}
