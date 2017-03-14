package com.hyd.redisfx.controllers.tabs;

import javafx.scene.control.Label;

/**
 * (description)
 * created at 17/03/14
 *
 * @author yidin
 */
@TabName("Key")
public class KeyTabController extends AbstractTabController {

    public Label txtCommand;

    @Override
    protected void onTabSelected() {
        this.txtCommand.requestFocus();  // executed, but not work
    }
}

