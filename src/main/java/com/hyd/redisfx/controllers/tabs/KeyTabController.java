package com.hyd.redisfx.controllers.tabs;

import com.hyd.redisfx.App;
import com.hyd.redisfx.Fx;
import com.hyd.redisfx.controllers.client.JedisManager;
import com.hyd.redisfx.event.EventType;
import com.hyd.redisfx.i18n.I18n;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

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

    public TableColumn<KeyItem, Integer> lengthColumn;

    @Override
    public void initialize() {
        super.initialize();

        this.keyColumn.setCellValueFactory(data -> data.getValue().keyProperty());
        this.typeColumn.setCellValueFactory(data -> data.getValue().typeProperty());
        this.lengthColumn.setCellValueFactory(data -> data.getValue().lengthProperty().asObject());

        this.cmbLimit.getSelectionModel().select(0);
        this.tblKeys.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.tblKeys.setOnMouseClicked(this::tableMouseClicked);

        this.tblKeys.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.C && event.isControlDown()) {  // Ctrl+C
                KeyItem selectedItem = this.tblKeys.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    Fx.copyText(selectedItem.getKey());
                }
            }
        });

        this.txtKeyPattern.setOnAction(this::listKeys);

        App.getEventBus().on(EventType.DatabaseChanged, event -> reset());
    }

    private void reset() {
        this.txtKeyPattern.setText("*");
        this.cmbLimit.getSelectionModel().select(0);
        this.tblKeys.getItems().clear();
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

        ObservableList<KeyItem> items = this.tblKeys.getItems();
        items.clear();

        if (pattern.trim().length() > 0) {
            try (Jedis jedis = JedisManager.getJedis()) {
                ScanParams scanParams = new ScanParams().match(pattern).count(limit);
                ScanResult<String> result = jedis.scan(ScanParams.SCAN_POINTER_START, scanParams);

                result.getResult().forEach(key -> {
                    String type = jedis.type(key);
                    int length = getLength(key, type, jedis);
                    items.add(new KeyItem(key, type, length));
                });
            }
        }
    }

    private int getLength(String key, String type, Jedis jedis) {
        switch (type) {
            case "string":
                return jedis.strlen(key).intValue();
            case "list":
                return jedis.llen(key).intValue();
            case "hash":
                return jedis.hlen(key).intValue();
            case "set":
                return jedis.scard(key).intValue();
            case "zset":
                return jedis.zcard(key).intValue();
            default:
                return 0;
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

        private IntegerProperty length = new SimpleIntegerProperty();

        public KeyItem() {
        }

        public KeyItem(String key) {
            setKey(key);
        }

        public KeyItem(String key, String type) {
            setKey(key);
            setType(type);
        }

        public KeyItem(String key, String type, int length) {
            setKey(key);
            setType(type);
            setLength(length);
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

        public int getLength() {
            return length.get();
        }

        public IntegerProperty lengthProperty() {
            return length;
        }

        public void setLength(int length) {
            this.length.set(length);
        }

        public StringProperty keyProperty() {
            return this.key;
        }

        public StringProperty typeProperty() {
            return this.type;
        }
    }
}

