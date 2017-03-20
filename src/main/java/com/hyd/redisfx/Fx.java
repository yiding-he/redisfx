package com.hyd.redisfx;

import com.hyd.redisfx.controllers.conn.BaseController;
import com.hyd.redisfx.i18n.I18n;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * (description)
 * created at 17/03/20
 *
 * @author yiding_he
 */
public class Fx {

    private static final Logger LOG = LoggerFactory.getLogger(Fx.class);

    public static void error(String message) {
        new Alert(Alert.AlertType.ERROR, message, ButtonType.OK).showAndWait();
    }

    public static FXMLLoader getFxmlLoader(String fxmlPath) {
        return new FXMLLoader(Fx.class.getResource(fxmlPath), I18n.UI_MAIN_BUNDLE);
    }

    @SuppressWarnings("unchecked")
    public static FXMLLoader loadFxml(String fxmlPath) {
        FXMLLoader fxmlLoader = getFxmlLoader(fxmlPath);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            LOG.error("", e);
            error(e.toString());
        }

        return fxmlLoader;
    }

    public static FXMLLoader showDialog(Stage owner, String title, String fxmlPath) {
        FXMLLoader fxmlLoader = loadFxml(fxmlPath);

        Stage dialogStage = new Stage();
        dialogStage.setTitle(title);

        if (owner != null) {
            dialogStage.initOwner(owner);
            dialogStage.initModality(Modality.WINDOW_MODAL);
        }

        Object controller = fxmlLoader.getController();
        if (controller instanceof BaseController) {
            ((BaseController) controller).setStage(dialogStage);
        }

        dialogStage.setScene(new Scene(fxmlLoader.getRoot()));
        dialogStage.show();
        return fxmlLoader;
    }
}
