package com.hyd.redisfx.controllers.tabs;

import com.hyd.redisfx.Fx;
import com.hyd.redisfx.controllers.client.JedisManager;
import com.hyd.redisfx.i18n.I18n;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.input.KeyCode;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@TabName("List")
public class ListTabController extends AbstractTabController {

    public TextField txtKey;

    public Spinner<Integer> spnFromIndex;

    public Spinner<Integer> spnToIndex;

    public ListView<String> lstValues;

    public Label lblMessage;

    public TextField txtNewItemValue;

    public Button btnDelete;

    public Button btnAppend;

    public Button btnReplace;

    public Button btnInsert;

    public ComboBox<String> cmbInsertType;

    private String currentKey;           // key of currently displayed list

    private int currentFrom, currentTo;  // index range of currently displayed items

    public void initialize() {
        super.initialize();

        //////////////////////////////////////////////////////////////

        cmbInsertType.getItems().addAll(
                I18n.getString("list_lbl_before_selected"),
                I18n.getString("list_lbl_after_selected")
        );
        cmbInsertType.getSelectionModel().select(0);

        //////////////////////////////////////////////////////////////

        spnFromIndex.setValueFactory(new IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 0));
        spnToIndex.setValueFactory(new IntegerSpinnerValueFactory(Integer.MIN_VALUE, Integer.MAX_VALUE, 100));

        Fx.fixIntegerSpinners(spnFromIndex, spnToIndex);

        //////////////////////////////////////////////////////////////

        txtKey.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                listValues(null);
            }
        });
    }

    public void listValues(ActionEvent actionEvent) {
        String key = txtKey.getText();
        if (StringUtils.isBlank(key)) {
            return;
        }

        int from = spnFromIndex.getValue();
        int to = spnToIndex.getValue();

        showList(key, from, to);
    }

    private void showList(String key, int from, int to) {
        JedisManager.withJedis(jedis -> {
            Long length = jedis.llen(key);
            lblMessage.setText(
                    I18n.getString("list_lbl_length") + length +
                            I18n.getString("list_lbl_display") + from +
                            I18n.getString("lbl_to") + to);

            List<String> values = jedis.lrange(key, from, to);
            lstValues.getItems().clear();
            lstValues.getItems().addAll(values);

            currentFrom = from;
            currentTo = to;
            currentKey = key;
        });
    }

    public void deleteItem(ActionEvent actionEvent) {
        if (currentKey == null) {
            return;
        }

        int selectedIndex = lstValues.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            return;
        }

        if (!Fx.confirmYesNo(I18n.getString("op_delete"), I18n.getString("msg_confirm_delete_value"))) {
            return;
        }

        int listIndex = selectedIndex + currentFrom;
        JedisManager.withJedis(jedis -> {
            String toBeDeletedValue = "__TO_BE_DELETED/" + System.currentTimeMillis();
            jedis.lset(currentKey, listIndex, toBeDeletedValue);
            jedis.lrem(currentKey, 1, toBeDeletedValue);
        });

        refreshList();
    }

    private void refreshList() {
        showList(currentKey, currentFrom, currentTo);
    }

    public void appendItem(ActionEvent actionEvent) {
        if (currentKey == null) {
            return;
        }

        String value = txtNewItemValue.getText();
        if (StringUtils.isBlank(value)) {
            return;
        }

        JedisManager.withJedis(jedis -> jedis.rpush(currentKey, value));
        refreshList();
    }

    public void replaceItem(ActionEvent actionEvent) {

    }

    public void insertItem(ActionEvent actionEvent) {

    }
}
