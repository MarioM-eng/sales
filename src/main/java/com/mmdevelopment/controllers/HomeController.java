package com.mmdevelopment.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

import static com.mmdevelopment.events.Events.goToSchools;

public class HomeController {

    @FXML
    private Button btnSchools;

    @FXML
    public void initialize() {
        // Asignar un evento al botón justo cuando inicia la aplicación
        btnSchools.setOnAction(goToSchools());
    }

}
