package com.hyd.redisfx.fx;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * (description)
 * created at 2017/7/12
 *
 * @author yidin
 */
public abstract class FormField extends HBox {

    private Label label;

    public FormField(String labelName) {
        this.setSpacing(10);
        this.setAlignment(Pos.BASELINE_LEFT);
        this.label = new Label(labelName);
        this.getChildren().add(this.label);
    }

    public void setLabelWidth(double width) {
        this.label.setPrefWidth(width);
        this.label.setMinWidth(width);
    }

    public double getDefaultLabelWidth() {
        // https://stackoverflow.com/questions/30983584/how-to-get-the-size-of-a-label-before-it-is-laid-out
        this.label.applyCss();
        this.label.layout();
        return this.label.getWidth() + 20;
    }
}
