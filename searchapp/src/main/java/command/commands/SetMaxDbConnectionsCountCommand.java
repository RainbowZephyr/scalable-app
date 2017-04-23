package command.commands;

import command.Command;
import services.Response;
import thread_pools.DatabaseThreadPool;
import utility.Constants;
import utility.ResponseCodes;

import java.util.Map;

public class SetMaxDbConnectionsCountCommand extends Command {

    public StringBuffer execute(Map<String, Object> requestMapData) throws Exception {
        String obj = (String) requestMapData.get(Constants.MAX_DB_CONNECTIONS_COUNT);
        // get parameter
        int maxDbConnectionsCount = Integer.parseInt(obj);
        // set size
        DatabaseThreadPool.sharedInstance().setMaxThreadPoolSize(maxDbConnectionsCount);
        // make sure it's set
        int result = DatabaseThreadPool.sharedInstance().getThreadPool().getCorePoolSize();
        // construct response
        Response response = new Response(ResponseCodes.STATUS_OK);
        response.addToResponse("thread_count", result);
        return response.toJson();
    }
}
