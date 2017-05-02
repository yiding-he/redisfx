package com.hyd.redisfx;

import com.hyd.redisfx.conn.ConnectionManager;
import com.hyd.redisfx.controllers.MainController;
import com.hyd.redisfx.event.EventBus;
import com.hyd.redisfx.event.EventType;
import com.hyd.redisfx.preference.PreferenceManager;

/**
 * Standalone service providers
 *
 * @author yiding_he
 */
public class App {

    private static EventBus<EventType> eventBus = new EventBus<>();

    private static PreferenceManager preferenceManager = new PreferenceManager();

    private static ConnectionManager connectionManager = new ConnectionManager();

    private static MainController mainController;

    public static void setMainController(MainController mainController) {
        App.mainController = mainController;
    }

    public static MainController getMainController() {
        return mainController;
    }

    public static PreferenceManager getPreferenceManager() {
        return preferenceManager;
    }

    public static ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    public static EventBus<EventType> getEventBus() {
        return eventBus;
    }
}
