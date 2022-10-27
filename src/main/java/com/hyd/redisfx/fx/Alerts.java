package com.hyd.redisfx.fx;

import com.hyd.fx.dialog.AlertDialog;
import com.hyd.redisfx.i18n.I18n;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

import static javafx.scene.control.Alert.AlertType.*;

/**
 * (description)
 * created at 2017/8/7
 *
 * @author yidin
 */
public class Alerts {

    public static boolean confirm(String titleKey, String messageKey) {
        Alert alert = createAlert(titleKey, messageKey, WARNING);
        ButtonType result = alert.showAndWait().orElse(ButtonType.CANCEL);
        return result == ButtonType.YES;
    }

    public static void error(String titleKey, String messageKey) {
        String title = I18n.getString(titleKey);
        String message = I18n.getString(messageKey);
        AlertDialog.error(title, message);
    }

    public static void error(String titleKey, Exception e) {
        String title = I18n.getString(titleKey);
        AlertDialog.error(title, e);
    }

    private static Alert createAlert(String titleKey, String messageKey, AlertType alertType) {
        String title = I18n.getString(titleKey);
        String message = I18n.getString(messageKey);

        ButtonType[] buttons = alertType == CONFIRMATION || alertType == WARNING ?
                new ButtonType[]{ButtonType.YES, ButtonType.NO} : new ButtonType[]{ButtonType.OK};

        Alert alert = new Alert(alertType, message, buttons);
        alert.setTitle(title);
        alert.setHeaderText(null);
        return alert;
    }
}
