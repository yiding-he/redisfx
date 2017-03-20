package com.hyd.redisfx.controllers;

import com.hyd.redisfx.Fx;
import com.hyd.redisfx.controllers.client.JedisManager;
import com.hyd.redisfx.controllers.tabs.AbstractTabController;
import com.hyd.redisfx.controllers.tabs.Tabs;
import com.hyd.redisfx.i18n.I18n;
import javafx.event.ActionEvent;
import javafx.scene.control.Menu;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

/**
 * (description)
 * created at 2017/3/14
 *
 * @author yidin
 */
public class MainController {

    public TabPane tabs;

    public Menu mnuRecentConnections;

    private Stage primaryStage;

    public void initialize() {
        initializeTabs();
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    private void initializeTabs() {
        this.tabs.getTabs().forEach(tab -> {
            String tabName = (String) tab.getUserData();
            AbstractTabController tabController = Tabs.getTabController(tabName);
            if (tabController != null) {
                tabController.setTab(tab);
            }
        });
    }

    public void openConnectionManager(ActionEvent actionEvent) {
        String fxml = "/fxml/conn/ConnectionManager.fxml";
        Fx.showDialog(primaryStage, I18n.getString("title_conn_manager"), fxml);
    }

    public void openConnection(ActionEvent actionEvent) {
        String host = "localhost";
        int port = 6379;

        JedisManager.connect(host, port);
        tabs.setVisible(true);
        primaryStage.setTitle(I18n.getString("app_title") + " - " + host + ":" + port);
    }
}
