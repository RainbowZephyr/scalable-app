import com.google.gson.Gson;
import nginx.clojure.NativeInputStream;
import nginx.clojure.NginxClojureRT;
import nginx.clojure.RequestBodyFetcher;
import nginx.clojure.java.NginxJavaRequest;
import nginx.clojure.java.NginxJavaRingHandler;
import nginx.clojure.util.NginxSharedHashMap;
import org.bson.BSON;
import org.bson.Document;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static nginx.clojure.MiniConstants.DEFAULT_ENCODING;

public class Nginx implements NginxJavaRingHandler {

    private static Gson gson;
//    private final static MongoClient mongoClient = new MongoClient("localhost");
//    private final static MongoDatabase mongoDB = mongoClient.getDatabase("Servers");
//    private final static MongoCollection<Document> collection = mongoDB.getCollection("apps");
    private final static File requestsConf = new File("config/requests.conf");
    private final static File appLocationsConf = new File("config/app_locations.conf");
    private static volatile BufferedReader fileBuffer;
    private final static ConcurrentHashMap<String, ArrayList<String>> appToMethods = new ConcurrentHashMap<>();
    private final static Pattern appName = Pattern.compile("^\\s*\\[\\s*(?<app>\\w+)\\s*\\]\\s*$");
    private final static Pattern appInstance = Pattern.compile("(?<name>\\w+)(?<instance>\\d+)");
    private final static ConcurrentHashMap<String, HashMap<String, String>> appLocations = new ConcurrentHashMap<>();
    private final static ArrayList<String> roundRobinQueue=  new ArrayList<>();
    private static volatile int counter = 0;
    private static volatile NginxSharedHashMap<String, Integer> sharedMemory = NginxSharedHashMap.build("roundRobin");

    public Object[] invoke(Map<String, Object> request) throws IOException {
        NginxJavaRequest req = (NginxJavaRequest) request;
        RequestBodyFetcher b = new RequestBodyFetcher();
        NativeInputStream body = (NativeInputStream) b.fetch(req.nativeRequest(), DEFAULT_ENCODING);
        NginxClojureRT.log.info("REQUEST: " + req + " ,BODY: " + streamToString(body));



        return null;
    }

    private String streamToString(InputStream is) throws IOException {
        final int PKG_SIZE = 1024;
        byte[] data = new byte[PKG_SIZE];
        StringBuilder buffer = new StringBuilder(PKG_SIZE * 10);
        int size;

        size = is.read(data, 0, data.length);
        while (size > 0) {
            String str = new String(data, 0, size);
            buffer.append(str);
            size = is.read(data, 0, data.length);
        }
        return buffer.toString();
    }

    private String parseJSON(String json) {
        readRequestsConf();

        gson = new Gson();
        Map<String, Object> map = gson.fromJson(json, Map.class);


        if (map.containsKey("service_type")) {
        // find the appname from the request
            String appName = appToMethods.entrySet()
                                         .stream()
                                         .filter(e -> e.getValue().equals(map.get("service_type")))
                                         .map(Map.Entry::getKey)
                                         .findFirst()
                                         .orElse(null);

            Object apps[] = appLocations.keySet()
                                        .stream()
                                        .filter(k -> k.startsWith(appName.toLowerCase()))
                                        .sorted((k1,k2) -> k1.compareTo(k2))
                                        .toArray();

            if(sharedMemory.containsKey(appName.toLowerCase())) {
                counter = sharedMemory.get(appName.toLowerCase());
            } else {
                sharedMemory.putIntIfAbsent(appName.toLowerCase(), 0);
                counter = 0;
            }

            //place in queue of App, you can obtain the queue location of the app number using apps array above
            //and placing it in the applocations maps

            Object x =appToMethods.keySet().stream().sorted().toArray()[counter];

        } else {

        }


        return "";
    }

    private static void readRequestsConf() {
        try {
            appToMethods.clear();
            fileBuffer = new BufferedReader(new FileReader(requestsConf));
            String currentLine;
            Matcher matcher;
            String lastMatch = "";
            while ((currentLine = fileBuffer.readLine()) != null) {
                if (currentLine.equals("")) {
                    continue;
                }
                matcher = appName.matcher(currentLine);

                if (matcher.find()) {
                    lastMatch = matcher.group("app");
                    if (!appToMethods.containsKey(matcher.group("app"))) {
                        appToMethods.put(matcher.group("app"), new ArrayList<String>());
                    }
                } else {
                    if (!appToMethods.get(lastMatch).contains(currentLine)) {
                        appToMethods.get(lastMatch).add(currentLine);
                    }
                }
            }
            fileBuffer.close();
            fileBuffer = null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fileBuffer = null;
        } catch (IOException e2) {
            fileBuffer = null;
        }
    }


    private static void readAppsConf() {
        appLocations.clear();
        String res = "";
        try {
            fileBuffer = new BufferedReader(new FileReader(appLocationsConf));
            String currentLine;
            String parameters[];
            Matcher instanceMatcher;
            while ((currentLine = fileBuffer.readLine()) != null) {
                if (!currentLine.contains("#")) {
                    parameters = currentLine.split(" ");
                    appLocations.put(parameters[0], new HashMap<String, String>());


                    for (int i = 1; i < parameters.length; i++) {
                        switch (i) {
                            case 1:
                                appLocations.get(parameters[0]).put("running", parameters[1]);
                                instanceMatcher = appInstance.matcher(parameters[0]);
                                if (instanceMatcher.find()) {
                                    appLocations.get(parameters[0]).put("instance_number", instanceMatcher.group("instance"));
                                } else {
                                    appLocations.get(parameters[0]).put("instance_number", "0");
                                }
                                break;
                            case 2:
                                appLocations.get(parameters[0]).put("ip", parameters[2]);
                                break;
                            case 3:
                                appLocations.get(parameters[0]).put("port", parameters[2]);
                                break;
                            case 4:
                                appLocations.get(parameters[0]).put("threads", parameters[1]);
                                break;
                            case 5:
                                appLocations.get(parameters[0]).put("database_threads", parameters[1]);
                                break;
                            case 6:
                                appLocations.get(parameters[0]).put("attached_mq", parameters[1]);
                                break;
                            default:
                                NginxClojureRT.log.info("Unknown parameter: " + parameters[i]);
                                break;
                        }
                    }
                }
            }
            fileBuffer.close();
            fileBuffer = null;
        } catch (IOException e) {
            fileBuffer = null;
        }

    }

    public static void main(String[] args) {
    readRequestsConf();
    readAppsConf();
        System.out.println(appToMethods);
        System.out.println(appLocations);
    }

}
