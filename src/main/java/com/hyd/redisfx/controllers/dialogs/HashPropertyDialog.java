package com.hyd.redisfx.controllers.dialogs;

import com.hyd.redisfx.controllers.tabs.HashTabController;
import com.hyd.redisfx.fx.FormDialog;
import com.hyd.redisfx.fx.TextAreaFormField;
import com.hyd.redisfx.i18n.I18n;
import javafx.event.ActionEvent;

/**
 * (description)
 * created at 2017/7/12
 *
 * @author yidin
 */
public class HashPropertyDialog extends FormDialog {

    private final TextAreaFormField keyField;

    private final TextAreaFormField valueField;

    private HashTabController.HashItem hashItem;

    private Runnable onItemSubmit;

    private boolean keyEditable = true;

    public HashPropertyDialog(HashTabController.HashItem hashItem) {
        this.hashItem = hashItem;
        this.setTitle("Hash 属性");
        this.setWidth(400);

        keyField = new TextAreaFormField(I18n.getString("word_key") + ": ", hashItem.getKey(), 5, true);
        valueField = new TextAreaFormField(I18n.getString("word_value") + ": ", hashItem.getValue(), 5, true);

        this.addField(keyField);
        this.addField(valueField);

        setOnShown(event -> keyField.setEditable(keyEditable));
    }

    public void setKeyEditable(boolean keyEditable) {
        this.keyEditable = keyEditable;
    }

    public boolean isKeyEditable() {
        return keyEditable;
    }

    public void setOnItemSubmit(Runnable onItemSubmit) {
        this.onItemSubmit = onItemSubmit;
    }

    @Override
    protected void okButtonClicked(ActionEvent event) {
        this.hashItem.setKey(this.keyField.getText());
        this.hashItem.setValue(this.valueField.getText());

        if (this.onItemSubmit != null) {
            this.onItemSubmit.run();
        }

        this.close();
    }
}
