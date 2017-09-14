package com.hyd.redisfx.fx;

import com.hyd.redisfx.nodes.IntegerSpinner;
import javafx.scene.layout.GridPane;

/**
 * @author yidin
 */
public class IntegerSpinnerFormField extends FormField {

    private IntegerSpinner spinner = new IntegerSpinner();

    public IntegerSpinnerFormField(String labelName, int min, int max) {
        super(labelName);
        this.spinner.setMin(min);
        this.spinner.setMax(max);
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
