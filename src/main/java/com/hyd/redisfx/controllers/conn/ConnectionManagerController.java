package com.hyd.redisfx.controllers.conn;

import javafx.event.ActionEvent;

/**
 * (description)
 * created at 17/03/20
 *
 * @author yiding_he
 */
public class ConnectionManagerController extends BaseController {

    public void closeClicked(ActionEvent actionEvent) {
        this.getStage().close();
    }
}
