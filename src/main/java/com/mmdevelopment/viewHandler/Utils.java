package com.mmdevelopment.viewHandler;

import com.mmdevelopment.Utilities;
import javafx.event.EventHandler;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;

@Slf4j
public class Utils {

    public static TextFormatter<String> onlyDouble() {
        Pattern pattern = Pattern.compile("^([1-9]\\d*(\\.)\\d*|0?(\\.|\\,)\\d*[1-9]\\d*|[1-9]\\d*)$");
        return new TextFormatter<>(change -> {
            String text = change.getControlNewText();

            if (!pattern.matcher(text).matches() || Utilities.isEmptyOrNull(text)) {
                change.setText("");
            }
            return change;
        });
    }

    public static TextFormatter<String> onlyInteger() {
        Pattern pattern = Pattern.compile("\\d*");
        return new TextFormatter<>(change -> {
            String text = change.getControlNewText();

            if (!pattern.matcher(text).matches() || Utilities.isEmptyOrNull(text)) {
                change.setText("");
            }
            return change;
        });
    }

}
