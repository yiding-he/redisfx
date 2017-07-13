package com.hyd.redisfx.nodes;

import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;

/**
 * 可以写的
 *
 * @author yidin
 */
public class IntegerSpinner extends Spinner<Integer> {

    public IntegerSpinner() {
        super(Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
        setEditable(true);
        initEditor();
    }

    private void initEditor() {
        getEditor().focusedProperty().addListener((_ob, _old, _new) -> {
            if (!_new) {
                checkEditorText();
            }
        });
    }

    public void setMin(int min) {
        getVF().setMin(min);
    }

    public void setMax(int max) {
        getVF().setMax(max);
    }

    private void checkEditorText() {
        IntegerSpinnerValueFactory valueFactory = getVF();

        try {
            int value = Integer.parseInt(getEditor().getText());
            int min = valueFactory.getMin();
            int max = valueFactory.getMax();

            value = Math.min(Math.max(min, value), max);

            getEditor().setText(String.valueOf(value));
            valueFactory.setValue(value);

        } catch (NumberFormatException e) {
            getEditor().setText(String.valueOf(valueFactory.getValue()));
        }
    }

    private IntegerSpinnerValueFactory getVF() {
        return (IntegerSpinnerValueFactory) getValueFactory();
    }

    @Override
    public void increment(int steps) {
        checkEditorText();
        super.increment(steps);
    }

    @Override
    public void decrement(int steps) {
        checkEditorText();
        super.decrement(steps);
    }
}
