package com.hyd.redisfx.conn;

import com.alibaba.fastjson.JSON;
import com.hyd.redisfx.UserConfig;
import com.hyd.redisfx.fx.Alerts;
import java.util.List;
import java.util.prefs.Preferences;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ConnectionManager {

    private static final String CONNECTIONS = "connections";

    private static ObservableList<Connection> connections = FXCollections.observableArrayList();

    private static UserConfig userConfig = new UserConfig();

    static {
        readAndParseUserConfig();
    }

    private static void readAndParseUserConfig() {
        try {
            Preferences preferences = getReferences();

            String json = preferences.get(CONNECTIONS, "{}");
            UserConfig userConfig = JSON.parseObject(json, UserConfig.class);
            List<Connection> connectionList = userConfig.getConnections();

            if (connectionList != null) {
                connections = FXCollections.observableArrayList(connectionList);
            } else {
                connections = FXCollections.observableArrayList();
            }
        } catch (Exception e) {
            userConfig = new UserConfig();
            Alerts.error("title_op_error", e);
        }
    }

    private static Preferences getReferences() {
        return Preferences.userRoot().node("com.github.yiding-he").node("redisfx");
    }

    public static void saveConnection(Connection connection) {

        if (!connections.contains(connection)) {
            connections.add(connection);
        } else {
            int index = connections.indexOf(connection);
            connections.set(index, connection);
        }

        userConfig.setConnections(connections);
        saveConnections();
    }

    public static void saveConnections() {
        getReferences().put(CONNECTIONS, JSON.toJSONString(userConfig));
    }

    public static ObservableList<Connection> connectionsProperty() {
        return connections;
    }
}
