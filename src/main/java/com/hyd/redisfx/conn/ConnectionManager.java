package com.hyd.redisfx.conn;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.hyd.redisfx.UserConfig;
import com.hyd.redisfx.fx.Alerts;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionManager {

    private static final Logger LOG = LoggerFactory.getLogger(ConnectionManager.class);

    private static final String saveFilePath = System.getProperty("user.home") + "/.redisfx/connections.json";

    private static boolean canSave = true;

    private static ObservableList<Connection> connections = FXCollections.observableArrayList();

    private static UserConfig userConfig;

    static {
        File file = new File(saveFilePath);

        try {
            if (!file.exists()) {
                initNewUserConfig(file);
            } else {
                readAndParseUserConfig(file);
            }
        } catch (IOException e) {
            LOG.error("", e);
            canSave = false;
        }
    }

    private static void initNewUserConfig(File file) throws IOException {
        File parent = file.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            canSave = false;
        }
        if (!file.createNewFile()) {
            canSave = false;
        }
        userConfig = new UserConfig();
        connections = FXCollections.observableArrayList();
    }

    private static void readAndParseUserConfig(File file) throws IOException {
        try {
            String json = FileUtils.readFileToString(file, "UTF-8");
            UserConfig userConfig = JSON.parseObject(json, UserConfig.class);
            List<Connection> connectionList = userConfig.getConnections();

            if (connectionList != null) {
                connections = FXCollections.observableArrayList(connectionList);
            } else {
                connections = FXCollections.observableArrayList();
            }
        } catch (JSONException e) {
            userConfig = new UserConfig();
            Alerts.error("title_op_error", "msg_user_config_error");
        }
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
        if (!canSave) {
            return;
        }

        try {
            File file = new File(saveFilePath);
            FileUtils.write(file, JSON.toJSONString(userConfig, true), "UTF-8");
        } catch (IOException e) {
            LOG.error("", e);
        }
    }

    public static ObservableList<Connection> connectionsProperty() {
        return connections;
    }
}
