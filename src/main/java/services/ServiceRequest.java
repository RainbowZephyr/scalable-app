package services;

import java.util.Map;

public class ServiceRequest {

    private String _strAction, _strSessionID;
    private Map<String, Object> _mapRequestData;

    public ServiceRequest(String strAction,
                          String strSessionID,
                          Map<String, Object> mapRequestData) {
        _strAction = strAction;
        _strSessionID = strSessionID;
        _mapRequestData = mapRequestData;
    }

    public String getAction() {
        return _strAction;
    }

    public String getSessionID() {
        return _strSessionID;
    }

    public Map<String, Object> getData() {
        return _mapRequestData;
    }
}