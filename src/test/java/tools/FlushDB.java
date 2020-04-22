package tools;

import redis.clients.jedis.Jedis;

public class FlushDB {

    public static void main(String[] args) {
        Jedis jedis = new Jedis();
        jedis.flushDB();
        System.out.println("数据库已清空。");
    }
}
