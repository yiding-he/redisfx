package com.hyd.redisfx;

import com.hyd.redisfx.conn.ConnectionManager;
import com.hyd.redisfx.controllers.MainController;
import com.hyd.redisfx.controllers.client.JedisManager;
import com.hyd.redisfx.event.EventBus;
import com.hyd.redisfx.event.EventType;

/**
 * Standalone service providers
 *
 * @author yiding_he
 */
public class App {

    private static EventBus<EventType> eventBus = new EventBus<>();

    private static ConnectionManager connectionManager = new ConnectionManager();

    private static MainController mainController;

    private static int databases;

    //////////////////////////////////////////////////////////////

    static {
        eventBus.on(EventType.ConnectionOpened, event ->
                JedisManager.withJedis(jedis ->
                        databases = Integer.parseInt(jedis.configGet("databases").get(1))));
    }

    //////////////////////////////////////////////////////////////

    public static int getDatabases() {
        return databases;
    }

    public static void setDatabases(int databases) {
        App.databases = databases;
    }

    public static void setMainController(MainController mainController) {
        App.mainController = mainController;
    }

    public static MainController getMainController() {
        return mainController;
    }

    public static ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    public static EventBus<EventType> getEventBus() {
        return eventBus;
    }
}
