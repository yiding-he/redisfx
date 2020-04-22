package com.hyd.redisfx;

import com.hyd.fx.Fxml;
import com.hyd.fx.app.AppLogo;
import com.hyd.fx.app.AppPrimaryStage;
import com.hyd.redisfx.controllers.MainController;
import com.hyd.redisfx.i18n.I18n;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class RedisFxApp extends Application {

    public static void main(String[] args) {
        launch(RedisFxApp.class);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        AppPrimaryStage.setPrimaryStage(primaryStage);
        AppLogo.setStageLogo(primaryStage);

        primaryStage.setTitle("RedisFX");
        primaryStage.setScene(new Scene(getRoot(primaryStage)));
        primaryStage.show();
    }

    public Parent getRoot(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = Fxml.load("/fxml/Main.fxml", I18n.UI_MAIN_BUNDLE);
        BorderPane mainPane = fxmlLoader.getRoot();

        MainController mainController = fxmlLoader.getController();
        mainController.setPrimaryStage(primaryStage);

        return mainPane;
    }

}
