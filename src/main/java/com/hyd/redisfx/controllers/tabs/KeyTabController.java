package com.hyd.redisfx.controllers.tabs;

import com.hyd.fx.NodeUtils;
import com.hyd.fx.concurrency.BackgroundTask;
import com.hyd.redisfx.App;
import com.hyd.redisfx.Fx;
import com.hyd.redisfx.controllers.client.JedisManager;
import com.hyd.redisfx.controllers.dialogs.SetExpiryDialog;
import com.hyd.redisfx.event.EventType;
import com.hyd.redisfx.fx.BackgroundExecutor;
import com.hyd.redisfx.i18n.I18n;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

@TabName("Key")
public class KeyTabController extends AbstractTabController {

    private static final Logger LOG = LoggerFactory.getLogger(KeyTabController.class);

    private static final DateTimeFormatter DATE_TIME_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public TextField txtKeyPattern;

    public ComboBox<Integer> cmbLimit;

    public TableView<KeyItem> tblKeys;

    public TableColumn<KeyItem, String> keyColumn;

    public TableColumn<KeyItem, String> typeColumn;

    public TableColumn<KeyItem, Integer> lengthColumn;

    public TableColumn<KeyItem, String> expireColumn;

    public ContextMenu mnuConfigValues;

    public HBox searchKeyIndicator;

    public Button searchButton;

    public BorderPane root;

    private boolean searchCancelled;


    @Override
    public void initialize() {
        super.initialize();

        NodeUtils.setManaged(this.searchKeyIndicator);
        this.searchKeyIndicator.setVisible(false);

        this.keyColumn.setCellValueFactory(data -> data.getValue().keyProperty());
        this.typeColumn.setCellValueFactory(data -> data.getValue().typeProperty());
        this.lengthColumn.setCellValueFactory(data -> data.getValue().lengthProperty().asObject());
        this.expireColumn.setCellValueFactory(data -> data.getValue().expireAtProperty());

        this.cmbLimit.getSelectionModel().select(0);
        this.tblKeys.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.tblKeys.setOnMouseClicked(this::tableMouseClicked);

        this.tblKeys.setRowFactory(tv -> new TableRow<KeyItem>() {
            @Override
            protected void updateItem(KeyItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.getType() != null) {
                    return;
                }
                BackgroundExecutor.execute(() -> {
                    try (Jedis jedis = JedisManager.getJedis()) {
                        String key = item.getKey();
                        String type = jedis.type(key);
                        int length = getLength(key, type, jedis);
                        String expireAt = getExpireAt(key, jedis);
                        item.setType(type);
                        item.setLength(length);
                        item.setExpireAt(expireAt);
                    }
                });
            }
        });

        this.tblKeys.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.C && event.isControlDown()) {  // Ctrl+C
                mnuCopyKey();
            } else if (event.getCode() == KeyCode.DELETE) {
                deleteKeys();
            }
        });

        this.txtKeyPattern.setOnAction(event -> this.listKeys());

        Fx.nodeOnKeyPress(this.tblKeys, Fx.CTRL_C, this::mnuCopyKey);
        App.getEventBus().on(EventType.DatabaseChanged, event -> reset());

        // 连接到新数据库时刷新一下 key 列表，以免旧的 key 列表对用户产生误导
        App.getEventBus().on(EventType.ConnectionOpened, event -> listKeys());
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
        if (Objects.equals(type, "string")) {
            Tabs.switchTab(StringTabController.class, c -> c.showValue(selectedItem.getKey()));
        } else if (Objects.equals(type, "list")) {
            Tabs.switchTab(ListTabController.class, c -> c.showList(selectedItem.getKey()));
        } else if (Objects.equals(type, "hash")) {
            Tabs.switchTab(HashTabController.class, c -> c.showValue(selectedItem.getKey()));
        } else if (Objects.equals(type, "set")) {
            Tabs.switchTab(SetTabController.class, c -> c.showSet(selectedItem.getKey()));
        }
    }

    @Override
    protected void onTabSelected() {
        if (cmbLimit.getValue() == null) {
            cmbLimit.getSelectionModel().select(0);
        }
    }

    public void listKeys() {
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

        txtKeyPattern.selectAll();  // 方便用户继续键入

        String pattern = this.txtKeyPattern.getText();
        int limit = Integer.parseInt(String.valueOf(cmbLimit.getValue()));

        ObservableList<KeyItem> items = this.tblKeys.getItems();
        items.clear();

        if (pattern.trim().length() > 0) {
            BackgroundTask
                .runTask(() -> runSearch(pattern, limit, items))
                .whenBeforeStart(() -> {
                    NodeUtils.setDisable(true, searchButton);
                    searchKeyIndicator.setVisible(true);
                    searchCancelled = false;
                })
                .whenTaskFinish(() -> {
                    NodeUtils.setDisable(false, searchButton);
                    searchKeyIndicator.setVisible(false);
                })
                .start();
        }
    }

    private void runSearch(String pattern, int limit, ObservableList<KeyItem> items) {

        // 扫描过程中一个 key 可能返回多次，因此需要排除重复结果
        Set<String> keySet = new HashSet<>();

        try (Jedis jedis = JedisManager.getJedis()) {
            String cursor = ScanParams.SCAN_POINTER_START;
            ScanParams scanParams = new ScanParams().match(pattern).count(limit);
            ScanResult<String> result;
            do {
                if (searchCancelled) {
                    break;
                }

                result = jedis.scan(cursor, scanParams);
                cursor = result.getStringCursor();

                for (String key : result.getResult()) {
                    if (!keySet.add(key)) {
                        continue;
                    }
                    items.add(new KeyItem(key));
                    if (searchCancelled) {
                        break;
                    }
                }

            } while (!result.isCompleteIteration() && items.size() < limit);
        }
    }

    private static String getExpireAt(String key, Jedis jedis) {
        Long seconds = jedis.ttl(key);
        if (seconds <= 0) {
            return "";
        } else {
            LocalDateTime expireTime = LocalDateTime.now().plusSeconds(seconds);
            return expireTime.format(DATE_TIME_PATTERN);
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

    public void deleteKeys() {

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

        listKeys();
    }

    private void forSelectedItems(Consumer<List<KeyItem>> itemsConsumer) {
        ObservableList<KeyItem> selectedItems = this.tblKeys.getSelectionModel().getSelectedItems();
        if (!selectedItems.isEmpty()) {
            itemsConsumer.accept(selectedItems);
        }
    }

    public void mnuCopyKey() {
        forSelectedItems(
            items -> Fx.copyText(
                items.stream().map(KeyItem::getKey).collect(Collectors.joining("\n"))
            )
        );
    }

    public void mnuSetExpiry() {
        forSelectedItems(
            items -> new SetExpiryDialog(items).show()
        );
    }

    public void mnuOpenEntry() {
        tableItemDoubleClicked();
    }

    public void cancelSearch() {
        this.searchCancelled = true;
    }

    //////////////////////////////////////////////////////////////

    public static class KeyItem {

        private StringProperty key = new SimpleStringProperty();

        private StringProperty type = new SimpleStringProperty();

        private IntegerProperty length = new SimpleIntegerProperty();

        private StringProperty expireAt = new SimpleStringProperty();

        public KeyItem() {
        }

        public KeyItem(String key) {
            setKey(key);
        }

        public KeyItem(String key, String type, int length, String expireAt) {
            setKey(key);
            setType(type);
            setLength(length);
            setExpireAt(expireAt);
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

        public String getExpireAt() {
            return expireAt.get();
        }

        public StringProperty expireAtProperty() {
            return expireAt;
        }

        public void setExpireAt(String expireAt) {
            this.expireAt.set(expireAt);
        }

        public void refreshExpiry() {
            JedisManager.withJedis(jedis ->
                setExpireAt(KeyTabController.getExpireAt(getKey(), jedis)));
        }
    }
}

