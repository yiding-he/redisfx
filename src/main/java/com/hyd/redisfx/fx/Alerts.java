package com.hyd.redisfx.fx;

import com.hyd.redisfx.i18n.I18n;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * (description)
 * created at 2017/8/7
 *
 * @author yidin
 */
public class Alerts {

    public static boolean confirm(String titleKey, String messageKey) {

        Alert.AlertType alertType = Alert.AlertType.WARNING;
        String title = I18n.getString(titleKey);
        String message = I18n.getString(messageKey);
        ButtonType[] buttons = {ButtonType.YES, ButtonType.NO};

        Alert alert = new Alert(alertType, message, buttons);
        alert.setTitle(title);
        alert.setHeaderText(null);

        ButtonType result = alert.showAndWait().orElse(ButtonType.CANCEL);
        return result == ButtonType.YES;
    }
}
