package command.commands;

import command.Command;
import services.Response;
import thread_pools.CommandsThreadPool;
import utility.Constants;
import utility.ResponseCodes;

import java.util.Map;


public class FreezeCommand extends Command {
    public StringBuffer execute(Map<String, Object> requestMapData) throws Exception {
        CommandsThreadPool.sharedInstance().getThreadPool().shutdown();
        Response response = new Response(ResponseCodes.STATUS_OK);
        response.addToResponse("app_id", Constants.APPLICATION_ID);
        response.addToResponse("service_type", "freeze");
        return response.toJson();
    }
}
