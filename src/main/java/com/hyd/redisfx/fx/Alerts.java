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
        Alert alert = new Alert(Alert.AlertType.WARNING, I18n.getString(messageKey), ButtonType.YES, ButtonType.NO);
        alert.setTitle(I18n.getString(titleKey));
        alert.setHeaderText(null);
        ButtonType buttonType = alert.showAndWait().orElse(ButtonType.CANCEL);
        return buttonType == ButtonType.YES;
    }
}
