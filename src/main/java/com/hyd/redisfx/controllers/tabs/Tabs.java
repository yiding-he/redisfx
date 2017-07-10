package com.hyd.redisfx.controllers.tabs;

import javafx.scene.control.TabPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * (description)
 * created at 17/03/14
 *
 * @author yidin
 */
public class Tabs {

    private static final Logger LOG = LoggerFactory.getLogger(Tabs.class);

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

        Optional<AbstractTabController> tabController = tabControllerMap.values().stream()
                .filter(controller -> controller.getClass().equals(type))
                .findFirst();


        tabController.ifPresent(c -> {
            tabs.getSelectionModel().select(c.getTab());
            if (afterSwitch != null) {
                afterSwitch.accept((T) c);
            }
        });

        if (!tabController.isPresent()) {
            LOG.error("Tab controller not found for type " + type);
        }
    }
}
