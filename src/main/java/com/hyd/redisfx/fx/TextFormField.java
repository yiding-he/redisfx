package com.hyd.redisfx.fx;

import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * (description)
 * created at 2017/7/12
 *
 * @author yidin
 */
public class TextFormField extends FormField {

    private final TextField textField = new TextField();

    public TextFormField(String labelName, String defaultValue) {
        super(labelName);
        this.textField.setText(defaultValue);
    }

    public TextField getTextField() {
        return textField;
    }

    public String getText() {
        return this.textField.getText();
    }

    @Override
    public void renderTo(GridPane contentPane, int rowIndex) {
        GridPane.setHgrow(this.textField, Priority.ALWAYS);
        contentPane.add(getLabel(), 0, rowIndex);
        contentPane.add(textField, 1, rowIndex);
    }
}
