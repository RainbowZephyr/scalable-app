package command.commands;

import command.Command;
import services.Response;
import utility.Constants;
import utility.ResponseCodes;

import java.util.Map;

import static utility.Constants.APPLICATION_ID;

public class SetErrorLevelCommand extends Command {

    public StringBuffer execute(Map<String, Object> requestMapData) throws Exception {
        String obj = (String) requestMapData.get(Constants.ERROR_REPORTING_LEVEL);
        // should set it somehow (SO FOR NOW NO IDEA HA3ml eh hna)
        Response response = new Response(ResponseCodes.STATUS_OK);
        response.addToResponse("app_id", APPLICATION_ID);
        response.addToResponse("receiving_app_id", "Controller");
        response.addToResponse("service_type", "set_error_level");
        return response.toJson();
    }
}
