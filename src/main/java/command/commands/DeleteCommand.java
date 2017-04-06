package command.commands;

import command.Command;
import services.Dispatcher;
import services.Response;
import utility.ResponseCodes;

import java.util.Map;

import static utility.Constants.COMMAND_NAME_KEY;
import static utility.Constants.IS_ADMIN_COMMAND_KEY;


public class DeleteCommand extends Command {
    protected StringBuffer execute(Map<String, Object> requestMapData) throws Exception {
        String className = (String) requestMapData.get(COMMAND_NAME_KEY);
        boolean adminCommand = Boolean.parseBoolean(
                String.valueOf(requestMapData.get(IS_ADMIN_COMMAND_KEY)));
        Dispatcher.sharedInstance().removeCommand(className, adminCommand);

        Response response = new Response(ResponseCodes.STATUS_OK);
        return response.toJson();
    }
}
