package com.mmdevelopment.viewHandler;

import javafx.scene.control.TextField;

public class NumberField extends TextField {

    public NumberField() {
        this.setTextFormatter(Utils.onlyInteger());
    }

    public NumberField(Integer number) {
        super(number.toString());
        this.setTextFormatter(Utils.onlyInteger());
    }

    public Integer getNumber() {
        return Integer.valueOf(this.getText());
    }

}
