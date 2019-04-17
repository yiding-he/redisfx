package com.hyd.redisfx.controllers.tabs;

import com.hyd.redisfx.Fx;
import com.hyd.redisfx.controllers.client.JedisManager;
import com.hyd.redisfx.controllers.dialogs.EditStringValueDialog;
import com.hyd.redisfx.i18n.I18n;
import java.util.Objects;
import java.util.Set;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@TabName("Set")
public class SetTabController extends AbstractTabController {

    private static final Logger LOG = LoggerFactory.getLogger(SetTabController.class);

    public TextField txtKey;

    public ListView<String> lstValues;

    public Label lblMessage;

    private String currentKey;           // key of currently displayed list

    public void initialize() {
        super.initialize();

        Fx.nodeOnKeyPress(txtKey, Fx.ENTER, this::listValues);

        //////////////////////////////////////////////////////////////

        // List key events
        Fx.nodeOnKeyPress(lstValues, Fx.CTRL_C, this::listItemCopyClicked);
        Fx.nodeOnKeyPress(lstValues, new KeyCodeCombination(KeyCode.DELETE), this::deleteItem);
    }

    private String getStringByDialog() {
        EditStringValueDialog dialog = new EditStringValueDialog("");
        dialog.showAndWait();

        String newValue = null;
        if (dialog.isOk()) {
            newValue = dialog.getValue();
        }
        return newValue;
    }

    public void listValues() {
        String key = txtKey.getText();
        showSet(key);
    }

    void showSet(String key) {
        if (StringUtils.isBlank(key)) {
            return;
        }

        int selectedIndex = lstValues.getSelectionModel().getSelectedIndex();
        currentKey = key;

        JedisManager.withJedis(jedis -> {

            if (!Objects.equals("set", jedis.type(key))) {
                lstValues.getItems().clear();
                lblMessage.setText(I18n.getString("list_msg_type_error"));
                return;
            }

            Long length = jedis.scard(key);
            lblMessage.setText(I18n.getString("list_lbl_length") + length);

            Set<String> values = jedis.smembers(key);
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
    private boolean prepareList() {
        if (currentKey != null) {
            return true;
        } else if (StringUtils.isNotBlank(txtKey.getText())) {
            showSet(txtKey.getText());
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

        String selectedItem = lstValues.getSelectionModel().getSelectedItem();
        JedisManager.withJedis(jedis -> jedis.srem(currentKey, selectedItem));
        refreshList();
    }

    private void refreshList() {
        showSet(currentKey);
    }

    public void insertItem() {
        if (!prepareList()) {
            return;
        }

        String value = getStringByDialog();
        if (StringUtils.isBlank(value)) {
            return;
        }

        try {
            JedisManager.withJedis(jedis -> jedis.sadd(currentKey, value));
        } catch (Exception e) {
            LOG.error("insert failed", e);
            Fx.error(I18n.getString("title_op_fail"), I18n.getString("list_msg_op_failed"));
        }

        refreshList();
    }

    public void listItemCopyClicked() {
        String value = lstValues.getSelectionModel().getSelectedItem();
        if (value != null) {
            Fx.copyText(value);
        }
    }
}
