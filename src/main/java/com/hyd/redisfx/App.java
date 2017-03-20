package com.hyd.redisfx;

import com.hyd.redisfx.conn.ConnectionManager;
import com.hyd.redisfx.preference.PreferenceManager;

/**
 * Standalone service providers
 *
 * @author yiding_he
 */
public class App {

    private static PreferenceManager preferenceManager = new PreferenceManager();

    private static ConnectionManager connectionManager = new ConnectionManager();

    public static PreferenceManager getPreferenceManager() {
        return preferenceManager;
    }

    public static ConnectionManager getConnectionManager() {
        return connectionManager;
    }
}
