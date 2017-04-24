package command.commands;

import command.Command;
import services.Response;
import thread_pools.DatabaseThreadPool;
import utility.Constants;
import utility.ResponseCodes;

import java.util.Map;

import static utility.Constants.APPLICATION_ID;

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
        response.addToResponse("count", result);
        response.addToResponse("app_id", APPLICATION_ID);
        response.addToResponse("receiving_app_id", "Controller");
        response.addToResponse("service_type", "set_max_db_connections_count");
        return response.toJson();
    }
}
