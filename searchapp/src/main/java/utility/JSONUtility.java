package utility;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Ahmed Abdelbadie on 4/2/17.
 */
public class JSONUtility {

    public static StringBuffer makeJSONResponseEnvelope(int nResponse,
                                                        StringBuffer strbufRequestData,
                                                        StringBuffer strbufResponseData,
                                                        String requestAction,
                                                        String requestSessionID) {
        StringBuffer strbufJSON;
        String strStatusMsg;
        String strData = "";
        Map<String, Object> mapInputData;
        String strKey;

        strbufJSON = new StringBuffer();
        strbufJSON.append("{");
        strbufJSON.append("\"responseTo\":\"" + requestAction + "\",");
        if (requestSessionID != null)
            strbufJSON.append("\"sessionID\":\"" + requestSessionID + "\",");

        strbufJSON.append("\"StatusID\":\"" + nResponse + "\",");
        strStatusMsg = (String) ResponseCodes.getMessage(Integer.toString(nResponse));
        strbufJSON.append("\"StatusMsg\":\"" + strStatusMsg + "\",");

        if (strbufRequestData != null)
            strbufJSON.append("\"requestData\":{" + strbufRequestData + "},");

        if (strbufResponseData != null) {
            if (strbufResponseData.charAt(0) == '[')
                strbufJSON.append("\"responseData\":" + strbufResponseData);    // if it is a list, no curley
            else
                strbufJSON.append("\"responseData\":{" + strbufResponseData + "}");
        }
        if (strbufJSON.charAt(strbufJSON.length() - 1) == ',')
            strbufJSON.deleteCharAt(strbufJSON.length() - 1);

        strbufJSON.append("}");
        return strbufJSON;
    }

    public static StringBuffer serializeMaptoJSON(Map<String, Object> map, ArrayList arrFieldstoKeep) {

        StringBuffer strbufData;

        strbufData = new StringBuffer();
        if (arrFieldstoKeep == null) {
            for (Map.Entry<String, Object> entry : map.entrySet())
                strbufData.append("\"" + entry.getKey() + "\":\"" + entry.getValue().toString() + "\",");
        } else {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (arrFieldstoKeep.contains(entry.getKey()))
                    strbufData.append("\"" + entry.getKey() + "\":\"" + entry.getValue().toString() + "\",");
            }
        }

        if (strbufData.charAt(strbufData.length() - 1) == ',')
            strbufData.setLength(strbufData.length() - 1);

        return strbufData;
    }
}
