import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import nginx.clojure.NativeInputStream;
import nginx.clojure.NginxClojureRT;
import nginx.clojure.RequestBodyFetcher;
import nginx.clojure.java.NginxJavaRequest;
import nginx.clojure.java.NginxJavaRingHandler;
import org.bson.BSON;
import org.bson.Document;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static nginx.clojure.MiniConstants.DEFAULT_ENCODING;

public class Nginx implements NginxJavaRingHandler {

    private static Gson gson;
//    private final static MongoClient mongoClient = new MongoClient("localhost");
//    private final static MongoDatabase mongoDB = mongoClient.getDatabase("Servers");
//    private final static MongoCollection<Document> collection = mongoDB.getCollection("apps");
    private final static File confFile = new File("requests.conf");
    private static BufferedReader fileBuffer;
    private final static ConcurrentHashMap<String, ArrayList<String>> appToMethods = new ConcurrentHashMap<String, ArrayList<String>>();
    private final static Pattern appName = Pattern.compile("^\\s*\\[\\s*(?<app>\\w+)\\s*\\]\\s*$");


    public Object[] invoke(Map<String, Object> request) throws IOException {
        NginxJavaRequest req = (NginxJavaRequest) request;
        RequestBodyFetcher b = new RequestBodyFetcher();
        NativeInputStream body =
                (NativeInputStream) b.fetch(req.nativeRequest(), DEFAULT_ENCODING);
        NginxClojureRT.log.info("REQIEST: " + req + " ,BODY: " + streamToString(body));


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


        BSON query = new BSON();
        Document document = new Document();
//        collection.find();

        //        Gson gson = new Gson();
//        int one = gson.fromJson("1", int.class);
//        Integer one = gson.fromJson("1", Integer.class);
//        Long one = gson.fromJson("1", Long.class);
//        Boolean false = gson.fromJson("false", Boolean.class);
//        String str = gson.fromJson("\"abc\"", String.class);
//        String[] anotherStr = gson.fromJson("[\"abc\"]", String[].class);


        return "";
    }

    private static void readConfFile() {
        try {
            fileBuffer = new BufferedReader(new FileReader(confFile));
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
                    appToMethods.get(lastMatch).add(currentLine);
                }
            }

        } catch (FileNotFoundException e) {
           e.printStackTrace();
           fileBuffer=null;
        } catch (IOException e2) {
            fileBuffer = null;
        }
    }

    public static void main(String[] args) {
        readConfFile();
        System.out.println(appToMethods);
    }

}
