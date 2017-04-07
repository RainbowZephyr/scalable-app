package command.commands;

import command.Command;
import services.Response;
import threads.CommandsThreadPool;
import utility.ResponseCodes;

import java.util.Map;


public class FreezeCommand extends Command {
    public StringBuffer execute(Map<String, Object> requestMapData) throws Exception {
        CommandsThreadPool.sharedInstance().getThreadPool().shutdown();
        Response response = new Response(ResponseCodes.STATUS_OK);
        return response.toJson();
    }
}
