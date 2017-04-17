package command.commands;

import command.Command;
import services.Response;
import thread_pools.CommandsThreadPool;
import utility.Constants;
import utility.ResponseCodes;

import java.util.Map;

public class SetMaxThreadCountCommand extends Command {

    public StringBuffer execute(Map<String, Object> requestMapData) {
        String obj = (String) requestMapData.get(Constants.MAX_THREAD_COUNT);
        // get parameter
        int maxPoolSize = Integer.parseInt(obj);
        // set size
        CommandsThreadPool.sharedInstance().setMaxThreadPoolSize(maxPoolSize);
        //make sure it's correct
        int result = CommandsThreadPool.sharedInstance().getThreadPool().getCorePoolSize();
        // construct response
        Response response = new Response(ResponseCodes.STATUS_OK);
        response.addToResponse("app_id", Constants.APPLICATION_ID);
        response.addToResponse("service_type", "set_max_thread_count");
        response.addToResponse("count", result);

        return response.toJson();
    }
}
