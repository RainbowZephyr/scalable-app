package command.commands;

import command.Command;
import services.Dispatcher;
import services.Response;
import utility.ResponseCodes;

import java.util.Map;

import static utility.Constants.*;


public class UpdateCommand extends Command {
    protected StringBuffer execute(Map<String, Object> requestMapData) throws Exception {
        String commandKey = (String) requestMapData.get(COMMAND_NAME_KEY);
        String commandClassName = (String) requestMapData.get(CLASS_NAME);
        boolean adminCommand = Boolean.parseBoolean(
                String.valueOf(requestMapData.get(IS_ADMIN_COMMAND_KEY)));

        Class<?> innerClass = Class.forName(commandClassName);
        Response response;
        if (innerClass != null) {
            Dispatcher.sharedInstance().updateCommandsTable(commandKey, innerClass, adminCommand);
            response = new Response(ResponseCodes.STATUS_OK);
        } else {
            response = new Response(ResponseCodes.STATUS_BAD_REQUEST);
        }
        return response.toJson();
    }
}
