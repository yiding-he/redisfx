package com.hyd.redisfx.fx;

import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * (description)
 * created at 2017/7/12
 *
 * @author yidin
 */
public class TextAreaFormField extends FormField {

    private TextArea textArea = new TextArea();

    private boolean vGrow;

    private int rowCount;

    public TextAreaFormField(String labelName, String defaultValue, int rowCount, boolean vGrow) {
        super(labelName);
        this.rowCount = rowCount;
        this.vGrow = vGrow;
        this.textArea.setText(defaultValue);
        this.textArea.setStyle("-fx-font-family: monospace");
    }

    public String getText() {
        return this.textArea.getText();
    }

    @Override
    public void renderTo(GridPane contentPane, int rowIndex) {
        GridPane.setHgrow(textArea, Priority.ALWAYS);
        if (vGrow) {
            GridPane.setVgrow(textArea, Priority.ALWAYS);
        } else {
            this.textArea.setPrefRowCount(rowCount);
        }

        contentPane.add(getLabel(), 0, rowIndex);
        contentPane.add(textArea, 1, rowIndex);

    }
}
