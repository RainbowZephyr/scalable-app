package connections;


import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public class RedisConnection {
    private RedissonClient redisson;
    private static RedisConnection ourInstance = new RedisConnection();

    public static RedisConnection getInstance() {
        return ourInstance;
    }

    private RedisConnection() {
        Config config = new Config();
        config.useSingleServer().setAddress("localhost:6379");

        redisson = Redisson.create(config);
    }

    public RedissonClient getRedisson() {
        return redisson;
    }
}
