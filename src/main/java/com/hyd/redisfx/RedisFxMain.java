package com.hyd.redisfx;

import com.hyd.redisfx.i18n.I18n;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * (description)
 * created at 2017/3/14
 *
 * @author yidin
 */
public class RedisFxMain extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle(I18n.getString("app_title"));
        primaryStage.setScene(new Scene(new BorderPane()));
        primaryStage.show();
    }
}
