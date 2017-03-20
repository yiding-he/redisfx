package com.hyd.redisfx.controllers.conn;

import javafx.stage.Stage;

/**
 * (description)
 * created at 17/03/20
 *
 * @author yiding_he
 */
public abstract class BaseController {

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Stage getStage() {
        return stage;
    }
}
