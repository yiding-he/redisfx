package com.hyd.redisfx.controllers.tabs;

import javafx.scene.control.Tab;

/**
 * (description)
 * created at 17/03/14
 *
 * @author yidin
 */
public class AbstractTabController {

    private Tab tab;

    public void setTab(Tab tab) {
        this.tab = tab;
        this.tab.setOnSelectionChanged(event -> {
            if (tab.isSelected()) {
                System.out.println("tab " + tab.getText() + " selected.");
                onTabSelected();
            }
        });
    }

    public void initialize() {
        Tabs.register(this);
    }

    //////////////////////////////////////////////////////////////

    protected void onTabSelected() {

    }
}
