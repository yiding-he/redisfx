package com.hyd.redisfx.controllers.dialogs;

import com.hyd.redisfx.jedis.JedisManager;
import com.hyd.redisfx.controllers.tabs.KeyTabController;
import com.hyd.redisfx.fx.FormDialog;
import com.hyd.redisfx.fx.IntegerSpinnerFormField;
import com.hyd.redisfx.i18n.I18n;
import javafx.event.ActionEvent;

import java.util.List;

import static com.hyd.redisfx.jedis.JedisManager.withJedis;

public class SetExpiryDialog extends FormDialog {

    private List<KeyTabController.KeyItem> keyItems;

    private IntegerSpinnerFormField expiryField;

    public SetExpiryDialog(List<KeyTabController.KeyItem> keyItems) {
        this.setTitle(I18n.getString("key_set_expiry_title"));

        this.keyItems = keyItems;
        this.expiryField = new IntegerSpinnerFormField(
            I18n.getString("key_lbl_expiry"), 0, Integer.MAX_VALUE);

        if (keyItems.size() == 1) {
            long ttl = JedisManager.usingJedis(jedis -> jedis.ttl(keyItems.get(0).getKey()));
            expiryField.setValue((int) ttl);
        }

        this.addField(expiryField);
    }

    @Override
    protected void okButtonClicked(ActionEvent event) {
        withJedis(jedis -> keyItems.forEach(keyItem -> {
            int ttl = expiryField.getValue();
            if (ttl > 0) {
                jedis.expire(keyItem.getKey(), ttl);
            } else {
                jedis.persist(keyItem.getKey());
            }
            keyItem.refreshExpiry();
        }));

        this.close();
    }
}
