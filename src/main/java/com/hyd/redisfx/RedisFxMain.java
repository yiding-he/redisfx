package com.hyd.redisfx;

import com.hyd.redisfx.i18n.I18n;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
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
        BorderPane mainPane = FXMLLoader.load(
                RedisFxMain.class.getResource("/fxml/Main.fxml"),
                I18n.UI_MAIN_BUNDLE);

        primaryStage.setTitle(I18n.getString("app_title"));
        primaryStage.setScene(new Scene(mainPane));
        primaryStage.setWidth(950);
        primaryStage.setHeight(700);
        primaryStage.show();
    }
}
