package com.hyd.redisfx.controllers.tabs;

import javafx.scene.control.TabPane;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * (description)
 * created at 17/03/14
 *
 * @author yidin
 */
public class Tabs {

    private static Map<String, AbstractTabController> tabControllerMap = new HashMap<>();

    private static TabPane tabs;

    public static void setTabs(TabPane tabs) {
        Tabs.tabs = tabs;
    }

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

    @SuppressWarnings("unchecked")
    public static <T extends AbstractTabController> void switchTab(Class<T> type, Consumer<T> afterSwitch) {
        tabControllerMap.values().stream()
                .filter(controller -> controller.getClass().equals(type))
                .findFirst()
                .ifPresent(tabController -> {
                    tabs.getSelectionModel().select(tabController.getTab());
                    if (afterSwitch != null) {
                        afterSwitch.accept((T)tabController);
                    }
                });
    }
}
