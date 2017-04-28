package Cache;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Set;

/**
 * Created by ahmed on 4/1/17.
 */
public class Cache {
    protected static JedisPool redisPool = new JedisPool(new JedisPoolConfig(), "localhost");

    public synchronized  static void cacheRequest(){
        Jedis jedis = redisPool.getResource();
            /// ... do stuff here ... for example
            jedis.set("foo", "bar");
            String foobar = jedis.get("foo");
            jedis.zadd("sose", 0, "car"); jedis.zadd("sose", 0, "bike");
            Set<String> sose = jedis.zrange("sose", 0, -1);


    }

    public static void getRequest(){

    }



}
