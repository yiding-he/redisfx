package com.hyd.redisfx.controllers.tabs;

import com.hyd.redisfx.controllers.client.JedisManager;
import com.hyd.redisfx.i18n.I18n;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;

@TabName("String")
public class StringTabController extends AbstractTabController {

    public TextField txtKey;

    public TextArea txtValue;

    public Label lblMessage;

    public Label lblLength;

    public Spinner<Integer> spnIncrement;

    public Spinner<Integer> spnDecrement;

    @Override
    public void initialize() {
        super.initialize();

        this.txtKey.textProperty().addListener((_ob, _old, _new) -> showValue(_new));
        this.txtValue.textProperty().addListener((_ob, _old, _new) -> updateLength(_new));

        this.spnIncrement.setValueFactory(new IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, 1));
        this.spnDecrement.setValueFactory(new IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, 1));
    }

    private void updateLength(String value) {
        this.lblLength.setText("Length: " + value.length());
    }

    private void showValue(String key) {

        if (key.length() == 0) {
            this.lblMessage.setText("Please type a key.");
            return;
        }

        String value = JedisManager.getJedis().get(key);

        if (value == null) {
            this.lblMessage.setText("Value is null.");
            this.lblLength.setText("");
            this.txtValue.setText("");
        } else {
            this.lblMessage.setText("Value:");
            this.lblLength.setText("Length: " + value.length());
            this.txtValue.setText(value);
        }
    }

    public void deleteKey(ActionEvent actionEvent) {
        String key = this.txtKey.getText();

        if (key.length() <= 0) {
            return;
        }

        String message = I18n.getString("confirm_delete_key");
        new Alert(Alert.AlertType.WARNING, message, ButtonType.YES, ButtonType.NO)
                .showAndWait().ifPresent(result -> {
            if (result == ButtonType.YES) {
                JedisManager.getJedis().del(key);
                showValue(key);
            }
        });
    }

    public void saveKey(ActionEvent actionEvent) {
        String key = this.txtKey.getText();

        if (key.length() <= 0) {
            return;
        }

        String value = this.txtValue.getText();
        JedisManager.getJedis().set(key, value);
        this.lblMessage.setText("Value saved.");
    }

    public void increment(ActionEvent actionEvent) {
        String key = txtKey.getText();
        Integer incr = this.spnIncrement.getValue();

        try {
            JedisManager.getJedis().incrBy(key, incr);
            showValue(key);
        } catch (Exception e) {
            this.lblMessage.setText(e.getMessage());
        }
    }

    public void decrement(ActionEvent actionEvent) {
        String key = txtKey.getText();
        Integer decr = this.spnDecrement.getValue();

        try {
            JedisManager.getJedis().decrBy(key, decr);
            showValue(key);
        } catch (Exception e) {
            this.lblMessage.setText(e.getMessage());
        }
    }
}
