package com.hyd.redisfx.controllers;

import com.hyd.redisfx.controllers.tabs.AbstractTabController;
import com.hyd.redisfx.controllers.tabs.Tabs;
import javafx.event.ActionEvent;
import javafx.scene.control.TabPane;

/**
 * (description)
 * created at 2017/3/14
 *
 * @author yidin
 */
public class MainController {

    public TabPane tabs;

    public void initialize() {
        initializeTabs();
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

    }
}
