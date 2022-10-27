package com.hyd.redisfx;

import com.hyd.fx.app.AppLogo;
import com.hyd.redisfx.controllers.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class RedisFxApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = Fx.getFxmlLoader("/fxml/Main.fxml");
        BorderPane mainPane = fxmlLoader.load();

        MainController mainController = fxmlLoader.getController();
        mainController.setPrimaryStage(primaryStage);

        primaryStage.setTitle("RedisFX");
        primaryStage.setScene(new Scene(mainPane));

        AppLogo.setPath("/icons/logo.png");
        AppLogo.setStageLogo(primaryStage);
        primaryStage.show();
    }
}
