package com.hyd.redisfx.controllers.dialogs;

import com.hyd.redisfx.controllers.tabs.HashTabController;
import com.hyd.redisfx.fx.FormDialog;

/**
 * (description)
 * created at 2017/7/12
 *
 * @author yidin
 */
public class HashPropertyDialog extends FormDialog {

    private HashTabController.HashItem hashItem;

    public HashPropertyDialog(HashTabController.HashItem hashItem) {
        this.hashItem = hashItem;
        this.setTitle("Hash 属性");
    }

    @Override
    protected void okButtonClicked() {

    }

    @Override
    protected void cancelButtonClicked() {

    }
}
