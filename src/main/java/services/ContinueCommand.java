package services;

import services.Command;

import java.util.Map;

public class ContinueCommand extends Command {
    public StringBuffer execute(Map<String, Object> mapUserData) throws Exception {
        Dispatcher.sharedInstance().loadThreadPool();
        return null;
    }
}
