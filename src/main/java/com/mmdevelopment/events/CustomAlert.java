package com.mmdevelopment.events;

import javafx.scene.control.Alert;

public class CustomAlert {

    private CustomAlert(){}

    public static final Alert.AlertType ERROR = Alert.AlertType.ERROR;
    public static final Alert.AlertType CONFIRMATION = Alert.AlertType.CONFIRMATION;
    public static final Alert.AlertType NONE = Alert.AlertType.NONE;
    public static final Alert.AlertType WARNING = Alert.AlertType.WARNING;
    public static final Alert.AlertType INFORMATION = Alert.AlertType.INFORMATION;

    public static Alert showAlert(String message, Alert.AlertType alertType){
        Alert alert = new Alert(alertType);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
        return alert;
    }



}
