package com.hyd.redisfx;

import com.hyd.jfapps.appbase.JfappsApp;
import com.hyd.redisfx.controllers.MainController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

public class RedisFxApp extends JfappsApp {

    @Override
    public Parent getRoot() throws Exception {
        Fx.setFxmlLoaderSupplier(this::fxmlLoader);
        FXMLLoader fxmlLoader = Fx.getFxmlLoader("/fxml/Main.fxml");
        BorderPane mainPane = fxmlLoader.load();
        MainController mainController = fxmlLoader.getController();
        mainController.setPrimaryStage(globalContext.get("primaryStage"));
        return mainPane;
    }
}
