package com.hyd.redisfx.controllers.dialogs;

import com.hyd.redisfx.jedis.JedisManager;
import com.hyd.redisfx.fx.FormDialog;
import com.hyd.redisfx.fx.IntegerSpinnerFormField;
import com.hyd.redisfx.i18n.I18n;
import javafx.event.ActionEvent;

/**
 * @author yidin
 */
public class ChangeDatabaseDialog extends FormDialog {

    private IntegerSpinnerFormField databaseField;

    public ChangeDatabaseDialog(int databases) {
        this.setTitle(I18n.getString("title_change_database"));

        int maxDatabase = databases - 1;
        String labelName = I18n.getString("word_database") + "(0~" + maxDatabase + "): ";

        databaseField = new IntegerSpinnerFormField(labelName, 0, maxDatabase);
        databaseField.setEditable(false);
        databaseField.setValue(JedisManager.getCurrentDatabase());

        this.addField(databaseField);
    }

    @Override
    protected void okButtonClicked(ActionEvent event) {
        JedisManager.setCurrentDatabase(databaseField.getValue());
        this.close();
    }
}
