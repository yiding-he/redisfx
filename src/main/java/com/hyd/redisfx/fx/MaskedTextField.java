package com.hyd.redisfx.fx;

import com.hyd.fx.utils.Str;
import javafx.scene.control.TextField;

public class MaskedTextField extends TextField {

    private String mask;

    public void setMask(String mask) {
        this.mask = mask;
    }

    public String getMask() {
        return mask;
    }

    public MaskedTextField() {
        init();
    }

    private void init() {
        textProperty().addListener((_ob, _old, _new) -> {
            if (Str.isBlank(mask)) {
                return;
            }

            if (!_new.matches(mask)) {
                setText(_old);
            }
        });
    }
}
