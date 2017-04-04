package command.commands;

import command.Command;
import services.Dispatcher;
import threads.CommandsThreadPool;

import java.util.Map;

public class ContinueCommand extends Command {

    public StringBuffer execute(Map<String, Object> mapUserData) throws Exception {
        CommandsThreadPool.sharedInstance().reloadThreadPool();
        return null;
    }
}
