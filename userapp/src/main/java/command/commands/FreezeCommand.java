package command.commands;

import command.Command;
import services.Response;
import thread_pools.CommandsThreadPool;
import utility.ResponseCodes;

import java.util.Map;

import static utility.Constants.APPLICATION_ID;


public class FreezeCommand extends Command {
    public StringBuffer execute(Map<String, Object> requestMapData) throws Exception {
        CommandsThreadPool.sharedInstance().getThreadPool().shutdown();
        Response response = new Response(ResponseCodes.STATUS_OK);
        response.addToResponse("app_id", APPLICATION_ID);
        response.addToResponse("receiving_app_id", "Controller");
        response.addToResponse("service_type", "freeze");
        return response.toJson();
    }
}
