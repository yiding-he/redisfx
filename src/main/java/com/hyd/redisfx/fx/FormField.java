package com.hyd.redisfx.fx;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import static javafx.scene.layout.Region.USE_PREF_SIZE;

/**
 * (description)
 * created at 2017/7/12
 *
 * @author yidin
 */
public abstract class FormField {

    private Label label;

    public FormField(String labelName) {
        this.label = new Label(labelName);
        this.label.setMinWidth(USE_PREF_SIZE);
    }

    protected Label getLabel() {
        return label;
    }

    public abstract void renderTo(GridPane contentPane, int rowIndex);
}
