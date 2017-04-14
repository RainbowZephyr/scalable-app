import com.google.gson.Gson;
import com.rabbitmq.client.*;
import org.apache.commons.lang.SerializationUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * TESTING CLASS FOR SERVICES
 */
public class TestServices {
    private static String MQ_SERVER_ADDRESS="127.0.0.1";
    private static int MQ_SERVER_PORT=5672; // default port(change from rabbitMq config file 8albn fi /etc/rabbitMQ/config
    private static Channel channel;
    private static Connection connection;
    private static String SERVICES_QUEUE_NAME = "app_consumer_queue";
    private static String SERVICES_RESPONSE_QUEUE = "app_producer_queue";
    private static String JSON_MESSAGE="application/json";
    private static AMQP.BasicProperties.Builder basicProperties;

    public static void main(String [] args) throws IOException, TimeoutException {
        // create a publisher & send serialized jsons
        TestServices testServices = new TestServices();
        org.apache.log4j.BasicConfigurator.configure();

        testServices.initProducer();
        testServices.initConsumer();

        Gson gson = new Gson();
        Map<String, Object> jsonRequest = new HashMap<String, Object>();
        jsonRequest.put("session_id", "sessionId");
        jsonRequest.put("app_id", "appId");
        jsonRequest.put("recieving_app_id", "someId");
        jsonRequest.put("service_type", "echo");
        Map<String, Object> jsonParams = new HashMap<String, Object>();
        jsonRequest.put("request_parameters", jsonParams);

        testServices.publishMessage(gson.toJson(jsonRequest));
    }

    public void publishMessage(Serializable object) throws IOException {
        channel.basicPublish("",SERVICES_QUEUE_NAME, basicProperties.build(),
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
        basicProperties = new AMQP.BasicProperties.Builder();
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

        public void setQUEUE_NAME(String QUEUE_NAME) {
            this.QUEUE_NAME = QUEUE_NAME;
        }

        public void setMQ_SERVER_ADDRESS(String MQ_SERVER_ADDRESS) {
            this.MQ_SERVER_ADDRESS = MQ_SERVER_ADDRESS;
        }

        public void setMQ_SERVER_PORT(int MQ_SERVER_PORT) {
            this.MQ_SERVER_PORT = MQ_SERVER_PORT;
        }

        public MyConsumer(){}

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
                channel.basicConsume(QUEUE_NAME, autoAck,this);
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
            String jsonStr = (String)SerializationUtils.deserialize(body);

            // parse JSONString
            Gson gson = new Gson();

            System.out.println("RECEIVED: " + jsonStr + " At Response Queue Of the App");
            // Construct Service Request
        }

        public void handleCancel(String consumerTag) {}
        public void handleCancelOk(String consumerTag) {}
        public void handleRecoverOk(String consumerTag) {}
        public void handleShutdownSignal(String consumerTag, ShutdownSignalException arg1) {}
    }
}
