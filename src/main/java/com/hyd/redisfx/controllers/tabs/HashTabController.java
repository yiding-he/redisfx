package com.hyd.redisfx.controllers.tabs;

import com.hyd.fx.NodeUtils;
import com.hyd.fx.concurrency.BackgroundTask;
import com.hyd.fx.utils.Str;
import com.hyd.redisfx.Fx;
import com.hyd.redisfx.controllers.dialogs.HashPropertyDialog;
import com.hyd.redisfx.fx.Alerts;
import com.hyd.redisfx.i18n.I18n;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;

import java.util.Map;
import java.util.Optional;

import static com.hyd.redisfx.jedis.JedisManager.withJedis;

@TabName("Hash")
public class HashTabController extends AbstractTabController {

    public TextField txtKey;

    public TableView<HashItem> tblHashValues;

    public TableColumn<HashItem, String> colHashKey;

    public TableColumn<HashItem, String> colHashValue;

    public ContextMenu mnuHashValues;

    public Label lblHashSize;

    public TextField txtHashFieldPattern;

    public Button searchButton;

    private String currentKey;

    public void initialize() {
        super.initialize();

        colHashKey.setCellValueFactory(item -> item.getValue().keyProperty());
        colHashValue.setCellValueFactory(item -> item.getValue().valueProperty());

        Fx.nodeOnKeyPress(txtKey, Fx.ENTER, this::showValue);
        Fx.nodeOnKeyPress(txtHashFieldPattern, Fx.ENTER, this::searchFieldPattern);

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
        if (selectedItem == null) {
            return;
        }

        HashPropertyDialog hashPropertyDialog = new HashPropertyDialog(selectedItem);
        hashPropertyDialog.setKeyEditable(false);
        hashPropertyDialog.setOnItemSubmit(() -> submitValue(selectedItem));
        hashPropertyDialog.show();
    }

    private void submitValue(HashItem hashItem) {
        withJedis(jedis ->
            jedis.hset(this.currentKey, hashItem.getKey(), hashItem.getValue()));
    }

    public void showValue() {
        if (Str.isBlank(txtKey.getText())) {
            return;
        }

        showValue(txtKey.getText());
    }

    public void showValue(String key) {
        txtKey.setText(key);
        txtKey.selectAll();
        this.currentKey = key;

        withJedis(jedis -> {
            tblHashValues.getItems().clear();
            jedis.hgetAll(key).forEach((k, v) -> tblHashValues.getItems().add(new HashItem(k, v)));
        });

        showKeySize(txtKey.getText());
    }

    private void showKeySize(String key) {
        withJedis(jedis -> lblHashSize.setText(I18n.getString("hash_lbl_size") + jedis.hlen(key)));
    }

    public void showValueWithPattern(String fieldPattern) {
        if (Str.isBlank(txtKey.getText())) {
            return;
        }

        showValueWithPattern(txtKey.getText(), fieldPattern);
    }

    public void showValueWithPattern(String key, String fieldPattern) {
        txtKey.setText(key);
        txtKey.selectAll();
        this.currentKey = key;

        BackgroundTask
            .runTask(() -> runSearch(key, fieldPattern, tblHashValues.getItems()))
            .whenBeforeStart(() -> NodeUtils.setDisable(true, searchButton))
            .whenTaskFinish(() -> NodeUtils.setDisable(false, searchButton))
            .start();
    }

    private void runSearch(String key, String fieldPattern, ObservableList<HashItem> items) {
        withJedis(jedis -> {

            lblHashSize.setText(I18n.getString("hash_lbl_size") + jedis.hlen(key));
            items.clear();

            ScanParams scanParams = new ScanParams().match(fieldPattern).count(1000);
            ScanResult<Map.Entry<String, String>> scanResult;
            String cursor = ScanParams.SCAN_POINTER_START;

            do {
                scanResult = jedis.hscan(key, cursor, scanParams);
                scanResult.getResult().forEach(
                    entry -> items.add(new HashItem(entry.getKey(), entry.getValue()))
                );
                cursor = scanResult.getCursor();
            } while (!scanResult.isCompleteIteration());
        });
    }

    public void addValue() {

        if (Str.isBlank(this.currentKey)) {
            showValue();
        }

        if (Str.isBlank(this.currentKey)) {
            Alerts.error("title_op_error", "hash_msg_nokey");
            return;
        }

        HashItem hashItem = new HashItem();
        HashPropertyDialog hashPropertyDialog = new HashPropertyDialog(hashItem);
        hashPropertyDialog.setOnItemSubmit(() -> {
            submitValue(hashItem);
            showValue();
        });
        hashPropertyDialog.show();
    }

    public void mnuEditHashValue() {
        editHashValue(tblHashValues.getSelectionModel().getSelectedItem());
    }

    public void mnuDeleteHashValue() {
        if (Alerts.confirm("word_delete_confirm", "hash_msg_confirm_delete")) {
            HashItem selectedItem = tblHashValues.getSelectionModel().getSelectedItem();
            withJedis(jedis -> jedis.hdel(this.currentKey, selectedItem.getKey()));
            tblHashValues.getItems().remove(selectedItem);
            showKeySize(this.currentKey);
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

    public void searchFieldPattern() {
        txtHashFieldPattern.selectAll();
        String fieldPattern = txtHashFieldPattern.getText();

        if (Str.isBlank(fieldPattern)) {
            showValue();
        } else {
            showValueWithPattern(fieldPattern);
        }
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
