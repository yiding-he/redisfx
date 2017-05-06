package com.hyd.redisfx.nodes;

import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory.DoubleSpinnerValueFactory;
import javafx.util.StringConverter;

/**
 * 可以写的
 *
 * @author yidin
 */
public class DoubleSpinner extends Spinner<Double> {

    public DoubleSpinner() {
        super(-Double.MAX_VALUE, Double.MAX_VALUE, 0);
        setEditable(true);
        initSpinner();
    }

    private void initSpinner() {
        getEditor().focusedProperty().addListener((_ob, _old, _new) -> {
            if (!_new) {
                checkEditorText();
            }
        });

        DoubleSpinnerValueFactory valueFactory = (DoubleSpinnerValueFactory) getValueFactory();

        valueFactory.setConverter(new StringConverter<Double>() {

            @Override
            public String toString(Double value) {
                // If the specified value is null, return a zero-length String
                if (value == null) {
                    return "";
                }

                return String.valueOf(value);
            }

            @Override
            public Double fromString(String value) {
                try {
                    // If the specified value is null or zero-length, return null
                    if (value == null) {
                        return null;
                    }

                    value = value.trim();

                    if (value.length() < 1) {
                        return null;
                    }

                    // Perform the requested parsing
                    return Double.parseDouble(value);
                } catch (NumberFormatException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

    }

    private void checkEditorText() {
        DoubleSpinnerValueFactory valueFactory = (DoubleSpinnerValueFactory) getValueFactory();

        try {
            double value = Double.parseDouble(getEditor().getText());
            double min = valueFactory.getMin();
            double max = valueFactory.getMax();

            value = Math.min(Math.max(min, value), max);

            getEditor().setText(String.valueOf(value));
            valueFactory.setValue(value);
        } catch (NumberFormatException e) {
            getEditor().setText(String.valueOf(valueFactory.getValue()));
        }
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
