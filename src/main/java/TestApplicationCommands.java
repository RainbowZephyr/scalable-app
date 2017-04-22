import com.google.gson.Gson;
import com.rabbitmq.client.*;
import com.rabbitmq.client.AMQP.BasicProperties.Builder;
import org.apache.commons.lang.SerializationUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

// listens for requests on the InboundQueue & sends it back on the outbound Queue
public class TestApplicationCommands {
    private static String MQ_SERVER_ADDRESS = "127.0.0.1";
    private static int MQ_SERVER_PORT = 5672; // default port(change from rabbitMq config file 8albn fi /etc/rabbitMQ/config
    private static Channel channel;
    private static Connection connection;
    private static String SERVICES_QUEUE_NAME = "media1_OutboundQueue";
    private static String SERVICES_RESPONSE_QUEUE = "media1_InboundQueue";
    private static String JSON_MESSAGE = "application/json";
    private static Builder basicProperties;


    public static void main(String[] args) throws IOException, TimeoutException {
        TestApplicationCommands testAppCmds = new TestApplicationCommands();
        testAppCmds.initProducer();
        testAppCmds.initConsumer();


        System.out.println("Paste in your formatted JSON & press Enter(Return) twice...");
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        StringBuffer toBeSentData = new StringBuffer();
        for (; ; ) {
            String line = in.readLine();
            if (line == null) {
                break;
            }

            if (line.isEmpty()) {
                toBeSentData.append("\n");
                publishMessage(toBeSentData.toString(), basicProperties.build());
                toBeSentData.setLength(0);
                System.out.println("Request Added To Queue: " + SERVICES_QUEUE_NAME);
            }
            // Sends the received line to the server.
            toBeSentData.append(line);
        }
    }


    //  ------------------------MQ SERVER CONNECTION --------------------------

    public static void publishMessage(Serializable object, AMQP.BasicProperties props) throws IOException {
        channel.basicPublish("", SERVICES_QUEUE_NAME, props,
                SerializationUtils.serialize(object));
    }


    public void initProducer() throws IOException, TimeoutException {
        //Create a connection factory
        ConnectionFactory factory = new ConnectionFactory();
        //hostAddress rabbitmq server
        factory.setHost(MQ_SERVER_ADDRESS);
        factory.setPort(MQ_SERVER_PORT);

        //getting a connection
        connection = factory.newConnection();

        //creating a channel
        channel = connection.createChannel();

        //declaring a queue for this channel. If queue does not exist,
        //it will be created on the server.
        channel.queueDeclare(SERVICES_QUEUE_NAME, false, false, false, null);
        basicProperties = new Builder();
        basicProperties.contentType(JSON_MESSAGE);
    }

    public void initConsumer() throws IOException, TimeoutException {
        MyConsumer myConsumer = new MyConsumer();
        myConsumer.setMQ_SERVER_ADDRESS(MQ_SERVER_ADDRESS);
        myConsumer.setMQ_SERVER_PORT(MQ_SERVER_PORT);
        myConsumer.setQUEUE_NAME(SERVICES_RESPONSE_QUEUE);
        myConsumer.init();
        Thread myThread = new Thread(myConsumer);
        myThread.start();
    }

    public class MyConsumer implements Runnable, Consumer {
        // default vlaues
        private String QUEUE_NAME;
        private String MQ_SERVER_ADDRESS;
        private int MQ_SERVER_PORT; // default port(change from rabbitMq config file 8albn fi /etc/rabbitMQ/config
        private Channel channel;
        private Connection connection;

        public MyConsumer() {
        }

        public void setQUEUE_NAME(String QUEUE_NAME) {
            this.QUEUE_NAME = QUEUE_NAME;
        }

        public void setMQ_SERVER_ADDRESS(String MQ_SERVER_ADDRESS) {
            this.MQ_SERVER_ADDRESS = MQ_SERVER_ADDRESS;
        }

        public void setMQ_SERVER_PORT(int MQ_SERVER_PORT) {
            this.MQ_SERVER_PORT = MQ_SERVER_PORT;
        }

        public void init() throws IOException, TimeoutException {
            //Create a connection factory
            ConnectionFactory factory = new ConnectionFactory();

            //host address of MQ server
            factory.setHost(MQ_SERVER_ADDRESS);
            factory.setPort(MQ_SERVER_PORT);

            //getting a connection
            connection = factory.newConnection();

            //creating a channel
            channel = connection.createChannel();
        }

        public void run() {
            try {
                boolean autoAck = true;
                channel.basicConsume(QUEUE_NAME, autoAck, this);
                System.out.println("Listening @ : " + QUEUE_NAME);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * Called when consumer is registered.
         */
        public void handleConsumeOk(String consumerTag) {
        }

        /**
         * Called when new message is available.
         */
        public void handleDelivery(String consumerTag, Envelope env,
                                   AMQP.BasicProperties props, byte[] body) throws IOException {
            String jsonStr = (String) SerializationUtils.deserialize(body);

            // parse JSONString
            Gson gson = new Gson();
            Map<String, Object> map = gson.fromJson(jsonStr, Map.class);
            map.clear();
            Map<String, Object> reponseLoad = new HashMap<>();
            map.put("app_id", "testApp");
            map.put("response_status", "200");
            map.put("response_load", reponseLoad);
            System.out.println("RECEIVED: " + jsonStr + " At Response Queue Of the App");
            // Construct Service Request
            System.out.println("ID: " + props.getCorrelationId());
            TestApplicationCommands.publishMessage(gson.toJson(map), props);
        }

        public void handleCancel(String consumerTag) {
        }

        public void handleCancelOk(String consumerTag) {
        }

        public void handleRecoverOk(String consumerTag) {
        }

        public void handleShutdownSignal(String consumerTag, ShutdownSignalException arg1) {
        }
    }
}
