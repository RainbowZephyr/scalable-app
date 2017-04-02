package command.commands;

import command.Command;
import utility.Constants;

import java.util.Map;

/**
 * Created by abdoo on 4/1/17.
 */
public class SetMaxDbConnectionsCountCommand extends Command {
    public StringBuffer execute(Map<String, Object> requestMapData) throws Exception {
        String obj = (String)requestMapData.get(Constants.MAX_DB_CONNECTIONS_COUNT);
        int maxDbConnectionsCount = Integer.parseInt(obj);
        // should set it somehow
        return null;
    }
}
