package com.hyd.redisfx.controllers.dialogs;

import com.hyd.redisfx.controllers.tabs.KeyTabController;
import com.hyd.redisfx.fx.FormDialog;
import com.hyd.redisfx.fx.IntegerSpinnerFormField;
import com.hyd.redisfx.i18n.I18n;
import javafx.event.ActionEvent;

import static com.hyd.redisfx.controllers.client.JedisManager.withJedis;

public class SetExpiryDialog extends FormDialog {

    private KeyTabController.KeyItem keyItem;

    private IntegerSpinnerFormField expiryField;

    public SetExpiryDialog(KeyTabController.KeyItem keyItem, int expiry) {
        this.setTitle(I18n.getString("key_set_expiry_title"));

        this.keyItem = keyItem;
        this.expiryField = new IntegerSpinnerFormField(
                I18n.getString("key_lbl_expiry"), 0, Integer.MAX_VALUE);

        if (expiry > 0) {
            expiryField.setValue(expiry);
        }

        this.addField(expiryField);
    }

    @Override
    protected void okButtonClicked(ActionEvent event) {
        withJedis(jedis -> {
            int ttl = expiryField.getValue();
            if (ttl > 0) {
                jedis.expire(keyItem.getKey(), ttl);
            } else {
                jedis.persist(keyItem.getKey());
            }
        });
        keyItem.refreshExpiry();
        this.close();
    }
}
