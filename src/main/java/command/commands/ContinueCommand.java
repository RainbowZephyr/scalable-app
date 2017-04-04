package command.commands;

import command.Command;
import services.Response;
import threads.CommandsThreadPool;
import utility.ResponseCodes;

import java.util.Map;

public class ContinueCommand extends Command {

    public StringBuffer execute(Map<String, Object> mapUserData) throws Exception {
        CommandsThreadPool.sharedInstance().reloadThreadPool();
        Response response = new Response(ResponseCodes.STATUS_OK);
        return response.toJson();
    }
}
