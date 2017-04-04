package command.commands;

import command.Command;
import services.Dispatcher;
import threads.CommandsThreadPool;

import java.util.Map;


public class FreezeCommand extends Command {
    public StringBuffer execute(Map<String, Object> requestMapData) throws Exception {
        CommandsThreadPool.sharedInstance().getThreadPool().shutdown();
        return null;
    }
}
