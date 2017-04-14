package services;


import com.google.gson.Gson;
import utility.Constants;

import java.util.HashMap;
import java.util.Map;

public class Response {
    Map<String, Object> responseParams;
    Gson gson = new Gson();

    public Response(String responseCode) {
        responseParams = new HashMap<String, Object>();
        responseParams.put(Constants.RESPONSE_STATUS_KEY, responseCode);
    }

    public void addToResponse(String key, Object value) {
        responseParams.put(key, value);
    }

    public StringBuffer toJson() {
        return new StringBuffer(gson.toJson(responseParams));
    }

}
