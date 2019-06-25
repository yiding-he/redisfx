package com.hyd.redisfx;

import com.hyd.jfapps.appbase.AppCategory;
import com.hyd.jfapps.appbase.AppInfo;
import com.hyd.jfapps.appbase.JfappsApp;
import com.hyd.redisfx.controllers.MainController;
import com.hyd.redisfx.controllers.client.JedisManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

@AppInfo(
    name = "Redis 客户端",
    author = "yiding-he",
    url = "https://github.com/yiding-he/redisfx",
    category = AppCategory.DATABASE
)
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

    @Override
    public void onCloseRequest() {
        JedisManager.shutdown();
    }
}
