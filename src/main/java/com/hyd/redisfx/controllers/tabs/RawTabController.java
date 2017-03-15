package com.hyd.redisfx.controllers.tabs;

import javafx.scene.control.TextField;

@TabName("Raw")
public class RawTabController extends AbstractTabController {

    public TextField txtCommand;

    @Override
    protected void onTabSelected() {
        this.txtCommand.requestFocus();
    }
}
