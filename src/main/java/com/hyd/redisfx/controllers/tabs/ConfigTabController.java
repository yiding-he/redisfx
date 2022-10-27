package com.hyd.redisfx.controllers.tabs;

import com.hyd.redisfx.App;
import com.hyd.redisfx.Fx;
import com.hyd.redisfx.jedis.JedisManager;
import com.hyd.redisfx.controllers.dialogs.HashPropertyDialog;
import com.hyd.redisfx.event.EventType;
import com.hyd.redisfx.fx.Alerts;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * (description)
 * created at 2017/10/27
 *
 * @author yidin
 */
@TabName("Config")
public class ConfigTabController extends AbstractTabController {

    public TableView<ConfigItem> tblConfigs;

    public TableColumn<ConfigItem, String> colConfigKey;

    public TableColumn<ConfigItem, String> colConfigValue;

    public ContextMenu mnuConfigValues;

    @Override
    public void initialize() {
        super.initialize();

        colConfigKey.setCellValueFactory(item -> item.getValue().keyProperty());
        colConfigValue.setCellValueFactory(item -> item.getValue().valueProperty());

        App.getEventBus().on(EventType.ConnectionOpened, event -> {
            tblConfigs.getItems().clear();

            JedisManager.withJedis(jedis -> {
                List<String> strings = jedis.configGet("*");
                int counter = 0;
                int size = strings.size();
                while (counter < size - 1) {
                    tblConfigs.getItems().add(new ConfigItem(strings.get(counter), strings.get(counter + 1)));
                    counter += 2;
                }
            });

            tblConfigs.getItems().sort(Comparator.comparing(ConfigItem::getKey));
        });

        Fx.nodeOnKeyPress(this.tblConfigs, Fx.CTRL_C, this::mnuCopyConfigKey);
        Fx.nodeOnKeyPress(this.tblConfigs, Fx.CTRL_B, this::mnuCopyConfigValue);
    }

    public void mnuEditConfigValue() {
        editHashValue(tblConfigs.getSelectionModel().getSelectedItem());
    }

    private void editHashValue(ConfigItem selectedItem) {
        if (selectedItem == null) {
            return;
        }

        ConfigItem duplicate = new ConfigItem(selectedItem.getKey(), selectedItem.getValue());

        HashPropertyDialog hashPropertyDialog = new HashPropertyDialog(duplicate);
        hashPropertyDialog.setKeyEditable(false);
        hashPropertyDialog.setOnItemSubmit(() -> {
            try {
                submitValue(duplicate);
                selectedItem.setValue(duplicate.getValue());
            } catch (Exception e) {
                Alerts.error("word_error", e.toString());
            }
        });
        hashPropertyDialog.show();
    }

    private void submitValue(ConfigItem configItem) {
        JedisManager.withJedis(jedis ->
                jedis.configSet(configItem.getKey(), configItem.getValue()));
    }

    public void mnuCopyConfigKey() {
        Optional.ofNullable(tblConfigs.getSelectionModel().getSelectedItem())
                .ifPresent(hashItem -> Fx.copyText(hashItem.getKey()));
    }

    public void mnuCopyConfigValue() {
        Optional.ofNullable(tblConfigs.getSelectionModel().getSelectedItem())
                .ifPresent(hashItem -> Fx.copyText(hashItem.getValue()));
    }

    public static class ConfigItem extends HashTabController.HashItem {

        public ConfigItem() {
        }

        public ConfigItem(String key) {
            super(key);
        }

        public ConfigItem(String key, String value) {
            super(key, value);
        }
    }
}
