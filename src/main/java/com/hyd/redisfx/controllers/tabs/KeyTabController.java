package com.hyd.redisfx.controllers.tabs;

import com.hyd.redisfx.controllers.client.JedisManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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
    }

    @Override
    protected void onTabSelected() {
        if (cmbLimit.getValue() == null) {
            cmbLimit.getSelectionModel().select(0);
        }
    }

    public void listKeys(ActionEvent actionEvent) {
        String pattern = this.txtKeyPattern.getText();
        int limit = Integer.parseInt(String.valueOf(cmbLimit.getValue()));

        ObservableList<KeyItem> items = this.tblKeys.getItems();
        items.clear();
        AtomicInteger counter = new AtomicInteger();

        if (pattern.trim().length() > 0) {

            ScanResult<String> result = null;
            String cursor = "0";
            ScanParams scanParams = new ScanParams();
            scanParams.match(pattern);
            Jedis jedis = JedisManager.getJedis();

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

