package tools;

import redis.clients.jedis.Jedis;

public class GenerateRandomStrings {

    public static void main(String[] args) {
        Jedis jedis = new Jedis();

        for (int i = 0; i < 100; i++) {
            String key = "string" + i;
            String value = Utils.randomString();
            jedis.set(key, value);
        }
    }
}
