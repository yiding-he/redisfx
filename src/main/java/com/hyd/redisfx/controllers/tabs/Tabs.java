package com.hyd.redisfx.controllers.tabs;

import java.util.HashMap;
import java.util.Map;

/**
 * (description)
 * created at 17/03/14
 *
 * @author yidin
 */
public class Tabs {

    private static Map<String, AbstractTabController> tabControllerMap = new HashMap<>();

    public static void register(AbstractTabController controller) {
        if (!controller.getClass().isAnnotationPresent(TabName.class)) {
            throw new IllegalArgumentException("Controller class must have a @TabName annotation");
        }

        String tabName = controller.getClass().getAnnotation(TabName.class).value();
        tabControllerMap.put(tabName, controller);
    }

    public static AbstractTabController getTabController(String tabName) {
        if (tabName == null) {
            return null;
        }
        return tabControllerMap.get(tabName);
    }
}
