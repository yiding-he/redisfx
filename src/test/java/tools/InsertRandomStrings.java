package tools;

import redis.clients.jedis.Jedis;

import java.util.Random;

public class InsertRandomStrings {

    public static void main(String[] args) {
        Jedis jedis = new Jedis();
        Random random = new Random();

        for (int i = 0; i < 100; i++) {
            String key = "string" + i;
            String value = generateValue(random);
            jedis.set(key, value);
        }
    }

    private static String generateValue(Random random) {
        int length = random.nextInt(10) + 10;
        char[] chars = new char[length];

        for (int i = 0; i < length; i++) {
            chars[i] = (char) (random.nextInt(52) + 'A');
        }

        return new String(chars);
    }
}
