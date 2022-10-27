package com.hyd.redisfx.fx;

import com.hyd.fx.components.IntegerSpinner;
import javafx.scene.layout.GridPane;

/**
 * @author yidin
 */
public class IntegerSpinnerFormField extends FormField {

    private IntegerSpinner spinner;

    public IntegerSpinnerFormField(String labelName, int min, int max) {
        super(labelName);
        this.spinner = new IntegerSpinner(min, max, min, 1);
    }

    public void setEditable(boolean editable) {
        this.spinner.setEditable(editable);
    }

    public int getValue() {
        return this.spinner.getValue();
    }

    public void setValue(int value) {
        this.spinner.getValueFactory().setValue(value);
    }

    @Override
    public void renderTo(GridPane contentPane, int rowIndex) {
        contentPane.add(getLabel(), 0, rowIndex);
        contentPane.add(spinner, 1, rowIndex);
    }
}
