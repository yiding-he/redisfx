package com.hyd.redisfx.controllers.tabs;

import com.hyd.redisfx.controllers.client.JedisManager;
import com.hyd.redisfx.i18n.I18n;
import com.hyd.redisfx.nodes.DoubleSpinner;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

@TabName("String")
public class StringTabController extends AbstractTabController {

    public TextField txtKey;

    public TextArea txtValue;

    public Label lblMessage;

    public Label lblLength;

    public DoubleSpinner spnIncrement;

    public DoubleSpinner spnDecrement;

    @Override
    public void initialize() {
        super.initialize();

        this.txtKey.textProperty().addListener((_ob, _old, _new) -> showValue(_new));
        this.txtValue.textProperty().addListener((_ob, _old, _new) -> updateLength(_new));
    }

    private void updateLength(String value) {
        this.lblLength.setText("Length: " + value.length());
    }

    public void showValue(String key) {

        if (key.length() == 0) {
            this.lblMessage.setText("Please type a key.");
            return;
        }

        this.txtKey.setText(key);       // 在界面上回填 key

        JedisManager.withJedis(jedis -> {
            String value = jedis.get(key);

            if (value == null) {
                this.lblMessage.setText("Value is null.");
                this.lblLength.setText("");
                this.txtValue.setText("");
            } else {
                this.lblMessage.setText("Value:");
                this.lblLength.setText("Length: " + value.length());
                this.txtValue.setText(value);
            }
        });
    }

    public void deleteKey() {
        String key = this.txtKey.getText();

        if (key.length() <= 0) {
            return;
        }

        String message = I18n.getString("confirm_delete_key");
        new Alert(Alert.AlertType.WARNING, message, ButtonType.YES, ButtonType.NO)
                .showAndWait().ifPresent(result -> {
            if (result == ButtonType.YES) {
                JedisManager.withJedis(jedis -> jedis.del(key));
                showValue(key);
            }
        });
    }

    public void saveKey() {
        String key = this.txtKey.getText();

        if (key.length() <= 0) {
            return;
        }

        String value = this.txtValue.getText();
        JedisManager.withJedis(jedis -> jedis.set(key, value));
        this.lblMessage.setText("Value saved.");
    }

    public void increment() {
        String key = txtKey.getText();
        double incr = this.spnIncrement.getValue();

        try {
            JedisManager.withJedis(jedis -> jedis.incrByFloat(key, incr));
            showValue(key);
        } catch (Exception e) {
            this.lblMessage.setText(e.getMessage());
        }
    }

    public void decrement() {
        String key = txtKey.getText();
        double incr = this.spnDecrement.getValue();

        try {
            JedisManager.withJedis(jedis -> jedis.incrByFloat(key, -incr));
            showValue(key);
        } catch (Exception e) {
            this.lblMessage.setText(e.getMessage());
        }
    }
}
