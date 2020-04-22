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
                onTabSelected();
            }
        });
    }

    public void initialize() {
        Tabs.register(this);
    }

    protected Tab getTab() {
        return tab;
    }

    //////////////////////////////////////////////////////////////

    protected void onTabSelected() {

    }
}
