package com.hyd.redisfx.fx;

import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * (description)
 * created at 2017/7/12
 *
 * @author yidin
 */
public class TextAreaFormField extends FormField {

    private TextArea textArea = new TextArea();

    public TextAreaFormField(String labelName, String defaultValue, int rowCount, boolean vGrow) {
        super(labelName);
        this.textArea.setText(defaultValue);
        this.getChildren().add(this.textArea);
        HBox.setHgrow(this.textArea, Priority.ALWAYS);

        if (vGrow) {
            VBox.setVgrow(this, Priority.ALWAYS);
            this.textArea.prefHeightProperty().bind(this.heightProperty());
        } else {
            this.textArea.setPrefRowCount(rowCount);
        }
    }

    public String getText() {
        return this.textArea.getText();
    }
}
