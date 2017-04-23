package utility;

import java.util.HashMap;
import java.util.Map;


public class ResponseCodes {

    public static final Map<String, String> _mapCodes;
    public static final String
            STATUS_OK = "200",
            STATUS_CREATED = "201",
            STATUS_BAD_REQUEST = "400",
            STATUS_NOT_FOUND = "404",
            STATUS_INTERNAL_SERVER_ERROR = "500",
            STATUS_NOT_IMPLEMENTED = "501",
            STATUS_SERVICE_UNAVAILABLE = "503",
            STATUS_DATA_BASE_ERROR = "600";

    static {
        _mapCodes = new HashMap<String, String>();
        String[][] pairs = {

                {STATUS_OK, "OK"},
                {STATUS_CREATED, "Created"},
                {STATUS_BAD_REQUEST, "Bad Request"},
                {STATUS_NOT_FOUND, "Not Found"},
                {STATUS_INTERNAL_SERVER_ERROR, "Internal Server Error"},
                {STATUS_NOT_IMPLEMENTED, "Not Implemented"},
                {STATUS_SERVICE_UNAVAILABLE, "Service Unavailable"},
                {STATUS_DATA_BASE_ERROR, "Database Error: Couldn't Insert, Duplicate"}

        };
        for (String[] pair : pairs) {
            _mapCodes.put(pair[0], pair[1]);
        }
    }

    public static String getMessage(String strCode) {
        return _mapCodes.get(strCode);
    }
}