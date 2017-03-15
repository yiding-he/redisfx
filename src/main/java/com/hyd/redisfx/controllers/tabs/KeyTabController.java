package com.hyd.redisfx.controllers.tabs;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

/**
 * (description)
 * created at 17/03/14
 *
 * @author yidin
 */
@TabName("Key")
public class KeyTabController extends AbstractTabController {

    public TextField txtKeyPattern;

    public ComboBox cmbLimit;

    @Override
    protected void onTabSelected() {
        if (cmbLimit.getValue() == null) {
            cmbLimit.getSelectionModel().select(0);
        }
    }
}

