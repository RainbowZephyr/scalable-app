package command.commands;

import command.Command;
import connections.QueueConsumerListenerThread;
import services.Response;
import thread_pools.CommandsThreadPool;
import utility.ResponseCodes;

import java.util.Map;

public class ContinueCommand extends Command {

    public StringBuffer execute(Map<String, Object> mapUserData) throws Exception {
        synchronized (QueueConsumerListenerThread.sharedInstance()){
            QueueConsumerListenerThread.sharedInstance().notify();
        }
        CommandsThreadPool.sharedInstance().reloadThreadPool();
        Response response = new Response(ResponseCodes.STATUS_OK);

        System.out.println(this.getClass().getCanonicalName());
        return response.toJson();
    }
}
