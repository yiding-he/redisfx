package com.hyd.redisfx.controllers.dialogs;

import com.hyd.redisfx.App;
import com.hyd.redisfx.controllers.client.JedisManager;
import com.hyd.redisfx.event.EventType;
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
        this.setPrefLabelWidth(100);
    }

    @Override
    protected void okButtonClicked(ActionEvent event) {

        JedisManager.setCurrentDatabase(databaseField.getValue());
        App.getEventBus().post(EventType.DatabaseChanged);

        this.close();
    }
}
