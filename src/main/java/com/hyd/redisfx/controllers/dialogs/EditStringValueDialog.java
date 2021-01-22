package com.hyd.redisfx.controllers.dialogs;

import com.hyd.redisfx.fx.Alerts;
import com.hyd.redisfx.fx.FormDialog;
import com.hyd.redisfx.fx.TextAreaFormField;
import javafx.event.ActionEvent;

/**
 * (description)
 * created at 2017/8/7
 *
 * @author yidin
 */
public class EditStringValueDialog extends FormDialog {

    private final TextAreaFormField textValueField;

    private String value;

    public EditStringValueDialog(String value) {
        this.value = value;
        this.setTitle("编辑值");
        this.setWidth(400);

        textValueField = new TextAreaFormField("值: ", value, 4, true);
        this.addField(textValueField);
    }

    public String getValue() {
        return value;
    }

    @Override
    protected void okButtonClicked(ActionEvent event) {
        String text = textValueField.getText();

        if (text.equals("")) {
            if (!Alerts.confirm("list_op_delete", "list_msg_delete_confirm")) {
                return;
            }
        }

        this.value = text;
        this.closeOK();
    }
}
