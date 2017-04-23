package load_balancer;

import connections.InboundMessageQueue;
import connections.OutboundMessageQueue;
import javafx.util.Pair;
import nginx.clojure.java.NginxJavaRingHandler;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class NginxInitialization implements NginxJavaRingHandler {
    private static NginxInitialization ourInstance = new NginxInitialization();

    private String mqServerAddress;
    private int mqServerPort;
    private final String DELIMITER = ";";
    private final String appInstanceConfPath = "/home/abdoo/IdeaProjects/scalable-app/config/apps_instances.properties";
    private final String messageQueueConfPath = "/home/abdoo/IdeaProjects/scalable-app/config/message_queue_server.properties";

    @Override
    public Object[] invoke(Map<String, Object> map) throws IOException {
        getInstance();
        return new Object[0];
    }

    public static NginxInitialization getInstance() {
        return ourInstance;
    }

    public NginxInitialization() {
        loadMessageQueueServerLocation();
        readInstancesQueuesIntoSharedMemory();
    }

    /**
     * Reads from the config file all instances & puts them in the shared map
     */
    public void readInstancesQueuesIntoSharedMemory() {
        // load the names of the instances into sharedHashMap
        Properties prop = new Properties();
        InputStream in = null;
        try {
            in = new FileInputStream(appInstanceConfPath);
            prop.load(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Enumeration e = prop.propertyNames();
        // write to the shared map
        while(e.hasMoreElements()){
            String key = (String) e.nextElement();
            String [] temp = prop.getProperty(key).split(DELIMITER);
            List<Pair<Boolean, OutboundMessageQueue>> arr = new ArrayList<>();
            Nginx.getCountersHashMap().put(key, 0);
            for(int i=0; i<temp.length; i++){
                String mqOutboundQueueName = temp[i].split("=")[0] + "_InboundQueue"; // inbound for the instance
                boolean state = (Integer.parseInt(temp[i].split("=")[1]) == 1); // is the instance running
                arr.add(new Pair<>(state, new OutboundMessageQueue(mqServerAddress, mqServerPort,
                        mqOutboundQueueName)));
                startMessageQueueListener(temp[i].split("=")[0]);
            }
            Nginx.getMessageQueuesHashMap().put(key, arr);
        }
    }

    public void startMessageQueueListener(String instanceName){
        String inboundQueueName = instanceName + "_OutboundQueue"; // outbound for the instance
        Thread thread = new Thread(new InboundMessageQueue(mqServerAddress, mqServerPort, inboundQueueName));
        thread.start();
    }


    public void loadMessageQueueServerLocation() {
        Properties prop = new Properties();
        InputStream in = null;
        try {
            in = new FileInputStream(messageQueueConfPath);
            prop.load(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mqServerAddress = prop.getProperty("MessageQueueServerAddress");
        mqServerPort = Integer.parseInt(prop.getProperty("MessageQueueServerPort"));
    }

    public void changeInstanceState(String instanceName, int state) {
        Properties props = new Properties();
        FileInputStream in = null;
        try {
            in = new FileInputStream(appInstanceConfPath);
            props.load(in);
            in.close();

            FileOutputStream out = new FileOutputStream(appInstanceConfPath);
            props.setProperty(instanceName, state+"");
            props.store(out, null);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
