package com.hyd.redisfx.i18n;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * (description)
 * created at 2017/3/14
 *
 * @author yidin
 */
public class I18n {

    public static final ResourceBundle UI_MAIN_BUNDLE = ResourceBundle.getBundle(
            "i18n.uiMain", new XMLResourceBundleControl());

    public static String getString(String key) {
        try {
            return UI_MAIN_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return key;
        }
    }

    public static String[] getStringArray(String key) {
        return UI_MAIN_BUNDLE.getStringArray(key);
    }
}
