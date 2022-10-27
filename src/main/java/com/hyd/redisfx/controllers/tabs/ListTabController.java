package com.hyd.redisfx.controllers.tabs;

import com.hyd.fx.components.IntegerSpinner;
import com.hyd.fx.utils.Str;
import com.hyd.redisfx.Fx;
import com.hyd.redisfx.controllers.dialogs.EditStringValueDialog;
import com.hyd.redisfx.i18n.I18n;
import com.hyd.redisfx.jedis.JedisManager;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.args.ListPosition;

import java.util.List;
import java.util.Objects;

@TabName("List")
public class ListTabController extends AbstractTabController {

    public TextField txtKey;

    public IntegerSpinner spnFromIndex;

    public IntegerSpinner spnToIndex;

    public ListView<String> lstValues;

    public Label lblMessage;

    public IntegerSpinner spnTrimFrom;

    public IntegerSpinner spnTrimTo;

    private String currentKey;           // key of currently displayed list

    private int currentFrom, currentTo;  // index range of currently displayed items

    public void initialize() {
        super.initialize();

        Fx.nodeOnKeyPress(txtKey, Fx.ENTER, this::listValues);

        //////////////////////////////////////////////////////////////

        // List key events
        lstValues.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.C && event.isControlDown()) {    // Ctrl+C
                listItemCopyClicked();
            } else if (event.getCode() == KeyCode.DELETE) {                 // Delete
                deleteItem();
            }
        });

        // List mouse events
        lstValues.setOnMouseClicked(event -> {
            String value = lstValues.getSelectionModel().getSelectedItem();
            if (event.getClickCount() == 2 && value != null) {
                editListItem();
            }
        });
    }

    public void editListItem() {
        if (lstValues.getSelectionModel().getSelectedIndex() == -1) {
            return;
        }

        String currentValue = lstValues.getSelectionModel().getSelectedItem();
        String newValue = getStringByDialog(currentValue);

        if (newValue != null) {
            if (newValue.equals("")) {
                deleteItem0();
            } else {
                replaceItem0(newValue);
            }
        }
    }

    private String getStringByDialog(String currentValue) {
        EditStringValueDialog dialog = new EditStringValueDialog(currentValue);
        dialog.showAndWait();

        String newValue = null;
        if (dialog.isOk()) {
            newValue = dialog.getValue();
        }
        return newValue;
    }

    public void listValues() {
        String key = txtKey.getText();
        showList(key);
    }

    void showList(String key) {
        if (Str.isBlank(key)) {
            return;
        }

        int from = spnFromIndex.getValue();
        int to = spnToIndex.getValue();

        showList(key, from, to);
    }

    private void showList(String key, int from, int to) {
        int selectedIndex = lstValues.getSelectionModel().getSelectedIndex();

        currentFrom = from;
        currentTo = to;
        currentKey = key;

        JedisManager.withJedis(jedis -> {

            if (!Objects.equals("list", jedis.type(key))) {
                lstValues.getItems().clear();
                lblMessage.setText(I18n.getString("list_msg_type_error"));
                return;
            }

            Long length = jedis.llen(key);
            lblMessage.setText(
                    I18n.getString("list_lbl_length") + length +
                            I18n.getString("list_lbl_display") + from +
                            I18n.getString("lbl_to") + to);

            List<String> values = jedis.lrange(key, from, to);
            lstValues.getItems().clear();
            lstValues.getItems().addAll(values);

            // restore selection
            if (selectedIndex != -1) {
                lstValues.getSelectionModel().select(selectedIndex);
            }

            // 在界面上回填 key
            this.txtKey.setText(key);
        });

        this.txtKey.selectAll();
    }

    // return true if a list is displaying
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean prepareList() {
        if (currentKey != null) {
            return true;
        } else if (Str.isNotBlank(txtKey.getText())) {
            showList(txtKey.getText());
            return true;
        } else {
            return false;
        }
    }

    public void deleteItem() {
        if (!prepareList()) {
            return;
        }

        int selectedIndex = lstValues.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            return;
        }

        if (!Fx.confirmYesNo(I18n.getString("op_delete"), I18n.getString("msg_confirm_delete_value"))) {
            return;
        }

        deleteItem0();
    }

    private void deleteItem0() {
        int selectedIndex = lstValues.getSelectionModel().getSelectedIndex();

        if (selectedIndex < 0) {
            return;
        }

        int listIndex = selectedIndex + currentFrom;

        try {
            JedisManager.withJedis(jedis -> {
                String toBeDeletedValue = "__TO_BE_DELETED/" + System.currentTimeMillis();
                jedis.watch(currentKey);
                Transaction transaction = jedis.multi();
                transaction.lset(currentKey, listIndex, toBeDeletedValue);
                transaction.lrem(currentKey, 1, toBeDeletedValue);
                transaction.exec();
            });
        } catch (Exception e) {
            Fx.error(I18n.getString("title_op_fail"), I18n.getString("list_msg_op_failed"));
        }

        refreshList();
    }

    private void refreshList() {
        showList(currentKey, currentFrom, currentTo);
    }

    private void replaceItem0(String value) {
        int selectedIndex = lstValues.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            return;
        }

        int listIndex = selectedIndex + currentFrom;
        JedisManager.withJedis(jedis -> jedis.lset(currentKey, listIndex, value));
        refreshList();
    }

    private void insertItem(ListPosition position) {
        if (!prepareList()) {
            return;
        }

        if (!lstValues.getItems().isEmpty() &&
            lstValues.getSelectionModel().getSelectedIndex() == -1) {
            return;
        }

        // append if list is empty
        long[] length = {0};
        JedisManager.withJedis(jedis -> length[0] = jedis.llen(currentKey));
        if (length[0] == 0) {
            appendItem();
            return;
        }

        // do insert

        int selectedIndex = lstValues.getSelectionModel().getSelectedIndex();
        final ListPosition finalPosition = selectedIndex < 0 ? ListPosition.AFTER : position;

        String value = getStringByDialog("");
        if (Str.isBlank(value)) {
            return;
        }

        int listIndex = selectedIndex + currentFrom;
        int restoreIndex = finalPosition == ListPosition.BEFORE ? listIndex + 1 : listIndex;
        int restoreSelectionIndex = finalPosition == ListPosition.BEFORE ? selectedIndex + 1 : selectedIndex;

        try {
            JedisManager.withJedis(jedis -> {
                String originalValue = jedis.lrange(currentKey, listIndex, listIndex).get(0);
                String tempValue = "__TEMP_VALUE/" + System.currentTimeMillis();

                jedis.watch(currentKey);
                Transaction transaction = jedis.multi();
                transaction.lset(currentKey, listIndex, tempValue);
                transaction.linsert(currentKey, finalPosition, tempValue, value);
                transaction.lset(currentKey, restoreIndex, originalValue);
                transaction.exec();
            });
        } catch (Exception e) {
            Fx.error(I18n.getString("title_op_fail"), I18n.getString("list_msg_op_failed"));
        }

        refreshList();
        if (restoreSelectionIndex < lstValues.getItems().size()) {
            lstValues.getSelectionModel().select(restoreSelectionIndex);
        } else {
            lstValues.getSelectionModel().clearSelection();
        }
    }

    private void appendItem() {
        String value = getStringByDialog("");
        if (Str.isBlank(value)) {
            return;
        }

        JedisManager.withJedis(jedis -> jedis.lpush(currentKey, value));
        refreshList();
    }

    public void listItemCopyClicked() {
        String value = lstValues.getSelectionModel().getSelectedItem();
        if (value != null) {
            Fx.copyText(value);
        }
    }

    public void insertItemBefore() {
        insertItem(ListPosition.BEFORE);
    }

    public void insertItemAfter() {
        insertItem(ListPosition.AFTER);
    }
}
