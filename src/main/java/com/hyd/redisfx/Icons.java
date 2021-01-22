package com.hyd.redisfx;

import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * (description)
 * created at 2017/7/10
 *
 * @author yidin
 */
public enum Icons {

    Logo("/icons/logo.png"),

    //////////////////////////////////////////////////////////////

    ;

    private Image image;

    Icons(String path) {
        this.image = new Image(Icons.class.getResourceAsStream(path));
    }

    public void setToStage(Stage stage) {
        ObservableList<Image> icons = stage.getIcons();
        icons.clear();
        icons.add(this.image);
    }
}
