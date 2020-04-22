package tools;

import redis.clients.jedis.Jedis;

public class GenerateHash {

    public static void main(String[] args) {
        Jedis jedis = new Jedis();
        String key = "hash1";

        for (int i = 0; i < 500; i++) {
            String propName = "prop" + i;
            String propValue = Utils.randomString();
            jedis.hset(key, propName, propValue);
        }

        System.out.println("Finished.");
    }
}
