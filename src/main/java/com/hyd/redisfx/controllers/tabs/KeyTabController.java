package com.hyd.redisfx.controllers.tabs;

import com.hyd.redisfx.Fx;
import com.hyd.redisfx.controllers.client.JedisManager;
import com.hyd.redisfx.i18n.I18n;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * (description)
 * created at 17/03/14
 *
 * @author yidin
 */
@TabName("Key")
public class KeyTabController extends AbstractTabController {

    private static final Logger LOG = LoggerFactory.getLogger(KeyTabController.class);

    public TextField txtKeyPattern;

    public ComboBox<Integer> cmbLimit;

    public TableView<KeyItem> tblKeys;

    public TableColumn<KeyItem, String> keyColumn;

    public TableColumn<KeyItem, String> typeColumn;

    @Override
    public void initialize() {
        super.initialize();

        this.keyColumn.setCellValueFactory(data -> data.getValue().keyProperty());
        this.typeColumn.setCellValueFactory(data -> data.getValue().typeProperty());
        this.tblKeys.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.tblKeys.setOnMouseClicked(this::tableMouseClicked);
    }

    private void tableMouseClicked(MouseEvent event) {
        if (event.getClickCount() == 2) {
            tableItemDoubleClicked();
        }
    }

    private void tableItemDoubleClicked() {
        KeyItem selectedItem = tblKeys.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            return;
        }

        String type = selectedItem.getType();
        if (type.equals("string")) {
            Tabs.switchTab(StringTabController.class, c -> c.showValue(selectedItem.getKey()));
        } else if (type.equals("list")) {
            Tabs.switchTab(ListTabController.class, c -> c.showList(selectedItem.getKey()));
        } else if (type.equals("hash")) {
            Tabs.switchTab(HashTabController.class, c -> c.showValue(selectedItem.getKey()));
        }
    }

    @Override
    protected void onTabSelected() {
        if (cmbLimit.getValue() == null) {
            cmbLimit.getSelectionModel().select(0);
        }
    }

    public void listKeys(ActionEvent actionEvent) {
        try {
            listKeys0();
        } catch (Exception e) {
            LOG.error("", e);
            Fx.error(I18n.getString("title_op_fail"), e.toString());
        }
    }

    private void listKeys0() {
        if (StringUtils.isBlank(this.txtKeyPattern.getText())) {
            this.txtKeyPattern.setText("*");
        }

        String pattern = this.txtKeyPattern.getText();
        int limit = Integer.parseInt(String.valueOf(cmbLimit.getValue()));
        AtomicInteger counter = new AtomicInteger();

        ObservableList<KeyItem> items = this.tblKeys.getItems();
        items.clear();

        if (pattern.trim().length() > 0) {

            ScanResult<String> result = null;
            String cursor = "0";
            ScanParams scanParams = new ScanParams();
            scanParams.match(pattern);

            try (Jedis jedis = JedisManager.getJedis()) {
                while (result == null || counter.get() < limit) {

                    if (result == null) {
                        result = jedis.scan(cursor, scanParams);
                    } else {
                        result = jedis.scan(cursor);
                    }

                    for (String key : result.getResult()) {
                        if (counter.incrementAndGet() <= limit) {
                            String type = jedis.type(key);
                            items.add(new KeyItem(key, type));
                        } else {
                            break;
                        }
                    }

                    cursor = result.getStringCursor();
                    if (cursor.equals("0")) {
                        break;
                    }
                }
            }
        }
    }

    public void deleteKeys(ActionEvent actionEvent) {

        ObservableList<KeyItem> selectedItems = this.tblKeys.getSelectionModel().getSelectedItems();
        if (selectedItems.isEmpty()) {
            return;
        }

        String message = I18n.getString("msg_confirm_delete_key");
        new Alert(Alert.AlertType.WARNING, message, ButtonType.YES, ButtonType.NO)
                .showAndWait().ifPresent(result -> {
            if (result == ButtonType.YES) {
                JedisManager.withJedis(jedis ->
                        selectedItems.forEach(item ->
                                jedis.del(item.getKey())));
            }
        });

        listKeys(actionEvent);
    }

    //////////////////////////////////////////////////////////////

    public static class KeyItem {

        private StringProperty key = new SimpleStringProperty();

        private StringProperty type = new SimpleStringProperty();

        public KeyItem() {
        }

        public KeyItem(String key) {
            setKey(key);
        }

        public KeyItem(String key, String type) {
            setKey(key);
            setType(type);
        }

        public String getKey() {
            return key.get();
        }

        public void setKey(String key) {
            this.key.set(key);
        }

        public String getType() {
            return type.get();
        }

        public void setType(String type) {
            this.type.set(type);
        }

        public StringProperty keyProperty() {
            return this.key;
        }

        public StringProperty typeProperty() {
            return this.type;
        }
    }
}

