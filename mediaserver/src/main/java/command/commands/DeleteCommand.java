package command.commands;

import command.Command;
import services.Dispatcher;
import services.Response;
import utility.ResponseCodes;

import java.util.Map;

import static utility.Constants.*;


public class DeleteCommand extends Command {
    protected StringBuffer execute(Map<String, Object> requestMapData) throws Exception {
        String className = (String) requestMapData.get(COMMAND_NAME_KEY);
        boolean adminCommand = Boolean.parseBoolean(
                String.valueOf(requestMapData.get(IS_ADMIN_COMMAND_KEY)));
        Dispatcher.sharedInstance().removeCommand(className, adminCommand);

        Response response = new Response(ResponseCodes.STATUS_OK);
        response.addToResponse("app_id", APPLICATION_ID);
        response.addToResponse("receiving_app_id", "Controller");
        response.addToResponse("service_type", "delete");
        return response.toJson();
    }
}
