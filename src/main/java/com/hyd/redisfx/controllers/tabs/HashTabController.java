package com.hyd.redisfx.controllers.tabs;

import com.hyd.redisfx.Fx;
import com.hyd.redisfx.controllers.client.JedisManager;
import com.hyd.redisfx.controllers.dialogs.HashPropertyDialog;
import com.hyd.redisfx.fx.Alerts;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

@TabName("Hash")
public class HashTabController extends AbstractTabController {

    public TextField txtKey;

    public TableView<HashItem> tblHashValues;

    public TableColumn<HashItem, String> colHashKey;

    public TableColumn<HashItem, String> colHashValue;

    public ContextMenu mnuHashValues;

    private String currentKey;

    public void initialize() {
        super.initialize();

        colHashKey.setCellValueFactory(item -> item.getValue().keyProperty());
        colHashValue.setCellValueFactory(item -> item.getValue().valueProperty());

        txtKey.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                showValue();
            }
        });

        tblHashValues.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && tblHashValues.getSelectionModel().getSelectedIndex() != -1) {
                editHashValue(tblHashValues.getSelectionModel().getSelectedItem());
            }
        });

        mnuHashValues.getItems().forEach(menuItem ->
                menuItem.disableProperty().bind(
                        tblHashValues.getSelectionModel().selectedIndexProperty().isEqualTo(-1)));
    }

    private void editHashValue(HashItem selectedItem) {
        HashPropertyDialog hashPropertyDialog = new HashPropertyDialog(selectedItem);
        hashPropertyDialog.setOnItemSubmit(() -> submitValue(selectedItem));
        hashPropertyDialog.show();
    }

    private void submitValue(HashItem hashItem) {
        JedisManager.withJedis(jedis ->
                jedis.hset(this.currentKey, hashItem.getKey(), hashItem.getValue()));
    }

    public void showValue() {
        if (StringUtils.isBlank(txtKey.getText())) {
            return;
        }

        showValue(txtKey.getText());
    }

    public void showValue(String key) {
        txtKey.setText(key);
        this.currentKey = key;

        JedisManager.withJedis(jedis -> {
            tblHashValues.getItems().clear();
            jedis.hgetAll(key).forEach((k, v) -> tblHashValues.getItems().add(new HashItem(k, v)));
        });
    }

    public void addValue() {

        if (StringUtils.isBlank(this.currentKey)) {
            showValue();
        }

        if (StringUtils.isBlank(this.currentKey)) {
            Alerts.error("title_op_error", "hash_msg_nokey");
            return;
        }

        HashItem hashItem = new HashItem();
        HashPropertyDialog hashPropertyDialog = new HashPropertyDialog(hashItem);
        hashPropertyDialog.setOnItemSubmit(() -> {
            submitValue(hashItem);
            tblHashValues.getItems().add(hashItem);
        });
        hashPropertyDialog.show();
    }

    public void mnuEditHashValue() {
        editHashValue(tblHashValues.getSelectionModel().getSelectedItem());
    }

    public void mnuDeleteHashValue() {
        if (Alerts.confirm("word_delete_confirm", "hash_msg_confirm_delete")) {
            HashItem selectedItem = tblHashValues.getSelectionModel().getSelectedItem();
            JedisManager.withJedis(jedis -> jedis.hdel(this.currentKey, selectedItem.getKey()));
            tblHashValues.getItems().remove(selectedItem);
        }
    }

    public void mnuCopyHashKey() {
        Optional.ofNullable(tblHashValues.getSelectionModel().getSelectedItem())
                .ifPresent(hashItem -> Fx.copyText(hashItem.getKey()));
    }

    public void mnuCopyHashValue() {
        Optional.ofNullable(tblHashValues.getSelectionModel().getSelectedItem())
                .ifPresent(hashItem -> Fx.copyText(hashItem.getValue()));
    }

    //////////////////////////////////////////////////////////////

    public static class HashItem {

        private StringProperty key = new SimpleStringProperty();

        private StringProperty value = new SimpleStringProperty();

        public HashItem() {
        }

        public HashItem(String key) {
            setKey(key);
        }

        public HashItem(String key, String value) {
            setKey(key);
            setValue(value);
        }

        public String getKey() {
            return key.get();
        }

        public void setKey(String key) {
            this.key.set(key);
        }

        public String getValue() {
            return value.get();
        }

        public void setValue(String value) {
            this.value.set(value);
        }

        public StringProperty keyProperty() {
            return this.key;
        }

        public StringProperty valueProperty() {
            return this.value;
        }
    }
}
