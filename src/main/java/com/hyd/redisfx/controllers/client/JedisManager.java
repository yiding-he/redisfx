package com.hyd.redisfx.controllers.client;

import redis.clients.jedis.Jedis;

/**
 * @author yiding.he
 */
public class JedisManager {

    private static Jedis jedis;

    public static void connect(String host, int port) {
        jedis = new Jedis(host, port);
    }
}
