package services;

import java.util.Map;

/**
 * Created by abdoo on 4/1/17.
 */
public class SetErrorLevel extends Command {
    public StringBuffer execute(Map<String, Object> requestMapData) throws Exception {
        String obj = (String)requestMapData.get(Constants.ERROR_REPORTING_LEVEL);
        // should set it somehow
        return null;
    }
}
