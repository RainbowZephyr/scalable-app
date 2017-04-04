package command.commands;

import command.Command;
import services.RequestHandle;
import threads.CommandsThreadPool;
import utility.Constants;
import services.Dispatcher;

import java.util.Map;

public class SetMaxThreadCountCommand extends Command {

    public StringBuffer execute(Map<String, Object> requestMapData) throws Exception {
        String obj = (String)requestMapData.get(Constants.MAX_THREAD_COUNT);
        int maxPoolSize = Integer.parseInt(obj);
        CommandsThreadPool.sharedInstance().getThreadPool().setCorePoolSize(maxPoolSize);
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append(CommandsThreadPool.sharedInstance().getThreadPool().getCorePoolSize());
        return strBuffer;
    }
}
