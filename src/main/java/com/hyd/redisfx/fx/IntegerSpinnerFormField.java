package com.hyd.redisfx.fx;

import com.hyd.redisfx.nodes.IntegerSpinner;

/**
 * @author yidin
 */
public class IntegerSpinnerFormField extends FormField {

    private IntegerSpinner spinner = new IntegerSpinner();

    public IntegerSpinnerFormField(String labelName, int min, int max) {
        super(labelName);
        this.spinner.setMin(min);
        this.spinner.setMax(max);
        this.getChildren().add(spinner);
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
}
