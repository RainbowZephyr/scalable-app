package connections;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.commons.lang.SerializationUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeoutException;


/**
 * The producer endpoint that writes to the queue.
 *
 * @author syntx
 */
public class Producer implements SocketConnection {

    final private static Producer instance = new Producer();
    private AMQP.BasicProperties.Builder basicProperties;
    private String PRODUCER_QUEUE_NAME, CONSUMER_QUEUE_NAME,
            JSON_MESSAGE = "application/json";
    private String MQ_SERVER_ADDRESS = "127.0.0.1";
    private int MQ_SERVER_PORT = 5672; // default port(change from rabbitMq config file 8albn fi /etc/rabbitMQ/config
    private Channel channel;
    private Connection connection;

    private Producer() {
    }

    public static Producer sharedInstance() {
        return instance;
    }

    public String getMQ_SERVER_ADDRESS() {
        return MQ_SERVER_ADDRESS;
    }

    public void setMQ_SERVER_ADDRESS(String MQ_SERVER_ADDRESS) {
        this.MQ_SERVER_ADDRESS = MQ_SERVER_ADDRESS;
    }

    public int getMQ_SERVER_PORT() {
        return MQ_SERVER_PORT;
    }

    public void setMQ_SERVER_PORT(int MQ_SERVER_PORT) {
        this.MQ_SERVER_PORT = MQ_SERVER_PORT;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void init() throws IOException, TimeoutException {
        //Create a connection factory
        ConnectionFactory factory = new ConnectionFactory();
        loadConfig();
        //hostAddress rabbitmq server
        factory.setHost(MQ_SERVER_ADDRESS);
        factory.setPort(MQ_SERVER_PORT);

        //getting a connection
        connection = factory.newConnection();

        //creating a channel
        channel = connection.createChannel();

        //declaring a queue for this channel. If queue does not exist,
        //it will be created on the server.
        channel.queueDeclare(PRODUCER_QUEUE_NAME, false, false, false, null);
        channel.queueDeclare(CONSUMER_QUEUE_NAME, false, false, false, null);

        basicProperties = new AMQP.BasicProperties.Builder();
        basicProperties.contentType(JSON_MESSAGE);
    }

    private void loadConfig() throws IOException {
        Properties prop = new Properties();
        InputStream in = new FileInputStream("config/message_queues.properties");
        prop.load(in);
        in.close();
        MQ_SERVER_ADDRESS = prop.getProperty(this.getClass().getSimpleName() + "_HOST");
        MQ_SERVER_PORT = Integer.parseInt(
                prop.getProperty(this.getClass().getSimpleName() + "_PORT"));
        PRODUCER_QUEUE_NAME = prop.getProperty(this.getClass().getSimpleName() + "_QUEUE");
        CONSUMER_QUEUE_NAME = prop.getProperty(QueueConsumer.sharedInstance().getClass()
                .getSimpleName() + "_QUEUE");
    }

    public void sendMessage(String repsonse) throws IOException {
        channel.basicPublish("", PRODUCER_QUEUE_NAME, basicProperties.build(),
                SerializationUtils.serialize(repsonse.toString()));
    }
}
