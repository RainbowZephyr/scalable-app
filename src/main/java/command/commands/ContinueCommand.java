package command.commands;

import command.Command;
import connections.QueueConsumerListenerThread;
import services.Response;
import thread_pools.CommandsThreadPool;
import utility.Constants;
import utility.ResponseCodes;

import java.util.Map;

public class ContinueCommand extends Command {

    public StringBuffer execute(Map<String, Object> mapUserData) throws Exception {
        synchronized (QueueConsumerListenerThread.sharedInstance()){
            QueueConsumerListenerThread.sharedInstance().notify();
        }
        CommandsThreadPool.sharedInstance().reloadThreadPool();
        Response response = new Response(ResponseCodes.STATUS_OK);
        response.addToResponse("app_id", Constants.APPLICATION_ID);
        response.addToResponse("service_type", "continue");
        return response.toJson();
    }
}
