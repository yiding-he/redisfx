package com.hyd.redisfx.conn;

import com.alibaba.fastjson.JSON;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * (description)
 * created at 17/03/20
 *
 * @author yiding_he
 */
public class ConnectionManager {

    private static final Logger LOG = LoggerFactory.getLogger(ConnectionManager.class);

    private static final String saveFilePath = System.getProperty("user.home") + "/.redisfx/connections.json";

    private static boolean canSave = true;

    private static ObservableList<Connection> connections = null;

    static {
        File file = new File(saveFilePath);

        try {
            if (!file.exists()) {
                File parent = file.getParentFile();
                if (!parent.exists() && !parent.mkdirs()) {
                    canSave = false;
                }
                file.createNewFile();
                connections = FXCollections.observableArrayList();
            } else {
                String json = FileUtils.readFileToString(file, "UTF-8");
                connections = FXCollections.observableArrayList(JSON.parseArray(json, Connection.class));
            }
        } catch (IOException e) {
            LOG.error("", e);
            canSave = false;
        }
    }

    public static void saveConnection(Connection connection) {
        connections.add(connection);

        try {
            File file = new File(saveFilePath);
            FileUtils.write(file, JSON.toJSONString(new ArrayList<>(connections), true), "UTF-8");
        } catch (IOException e) {
            LOG.error("", e);
        }
    }

    public static ObservableList<Connection> connectionsProperty() {
        return connections;
    }
}
