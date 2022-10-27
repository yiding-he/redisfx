package com.hyd.redisfx.controllers;

import com.hyd.redisfx.App;
import com.hyd.redisfx.Fx;
import com.hyd.redisfx.jedis.JedisManager;
import com.hyd.redisfx.controllers.dialogs.ChangeDatabaseDialog;
import com.hyd.redisfx.controllers.tabs.AbstractTabController;
import com.hyd.redisfx.controllers.tabs.Tabs;
import com.hyd.redisfx.event.EventType;
import com.hyd.redisfx.i18n.I18n;
import javafx.event.ActionEvent;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * (description)
 * created at 2017/3/14
 *
 * @author yidin
 */
public class MainController {

    private static final Logger LOG = LoggerFactory.getLogger(MainController.class);

    public TabPane tabs;

    public Menu mnuCurrentDatabase;

    private Stage primaryStage;

    public void initialize() {
        App.setMainController(this);
        Tabs.setTabs(tabs);
        initializeTabs();
        initDatabaseMenuItems();
    }

    private void initDatabaseMenuItems() {
        for (int i = 0; i < 16; i++) {
            CheckMenuItem item = new CheckMenuItem(String.valueOf(i));
            final int index = i;
            item.setOnAction(event -> JedisManager.setCurrentDatabase(index));
            mnuCurrentDatabase.getItems().add(item);
        }

        App.getEventBus().on(EventType.DatabaseChanged, event -> {
            int currentDatabase = JedisManager.getCurrentDatabase();
            for (int i = 0; i < mnuCurrentDatabase.getItems().size(); i++) {
                CheckMenuItem menuItem = (CheckMenuItem) mnuCurrentDatabase.getItems().get(i);
                menuItem.setSelected(i == currentDatabase);
            }
        });

        JedisManager.setCurrentDatabase(0);
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    private void initializeTabs() {
        this.tabs.getTabs().forEach(tab -> {
            String tabName = (String) tab.getUserData();
            AbstractTabController tabController = Tabs.getTabController(tabName);
            if (tabController != null) {
                tabController.setTab(tab);
            }
        });

        App.getEventBus().on(EventType.ConnectionOpened, event -> {
            tabs.setVisible(true);
            updateTitle();
        });

        App.getEventBus().on(EventType.DatabaseChanged, event -> updateTitle());
    }

    private void updateTitle() {
        // 作为小应用不再操作主窗体
    }

    public void openConnectionManager() {
        String fxml = "/fxml/conn/ConnectionManager.fxml";
        Fx.showDialog(primaryStage, I18n.getString("title_conn_manager"), fxml);
    }

    public void openConnection() {
        String host = "localhost";
        int port = 6379;

        try {
            JedisManager.connect(host, port);
            App.getEventBus().post(EventType.ConnectionOpened);
        } catch (Exception e) {
            LOG.error("", e);
            Fx.error("连接失败", "连接到 " + host + ":" + port + " 失败：\n\n" + e.toString());
        }
    }

    public void changeDatabaseClicked() {
        ChangeDatabaseDialog dialog = new ChangeDatabaseDialog(App.getDatabases());
        dialog.show();
    }

    public void switchDatabaseClicked() {
    }

    public void clearDatabaseClicked() {
    }
}
