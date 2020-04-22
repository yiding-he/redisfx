package tools;

import redis.clients.jedis.Jedis;

public class FillRedis {

    public static void main(String[] args) {
        Jedis jedis = new Jedis();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            jedis.set("string-key-" + (i + start), "string-value-" + (i + start));
        }

        String listKey = "sample-list-" + start;
        for (int i = 0; i < 10000; i++) {
            jedis.lpush(listKey, "list-item-" + (i + start));
        }

        String hashKey = "sample-hash-" + start;
        for (int i = 0; i < 10000; i++) {
            jedis.hset(hashKey, "hash-key-" + (i + start), "hash-value-" + (i + start));
        }
    }
}
