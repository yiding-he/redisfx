package com.hyd.redisfx;

import com.hyd.redisfx.controllers.MainController;
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

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader fxmlLoader = Fx.getFxmlLoader("/fxml/Main.fxml");

        BorderPane mainPane = fxmlLoader.load();
        MainController mainController = fxmlLoader.getController();
        mainController.setPrimaryStage(primaryStage);

        Icons.Logo.setToStage(primaryStage);

        primaryStage.setTitle(I18n.getString("app_title"));
        primaryStage.setScene(new Scene(mainPane));
        primaryStage.setWidth(950);
        primaryStage.setHeight(700);
        primaryStage.show();
    }
}
