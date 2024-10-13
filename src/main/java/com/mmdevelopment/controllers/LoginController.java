package com.mmdevelopment.controllers;


import com.mmdevelopment.events.CustomAlert;
import com.mmdevelopment.models.dtos.UserLoginDto;
import com.mmdevelopment.services.Auth;
import com.mmdevelopment.viewHandler.Views;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
public class LoginController implements Initializable {

    @FXML
    private PasswordField tfPassword;

    @FXML
    private TextField tfUserName;

    @FXML
    private Button btLogin;

    private Auth auth;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.auth = new Auth();
        btLogin.setOnAction(login());
    }

    public EventHandler<ActionEvent> login() {
        return event -> {
            String userName = "admin"; //this.tfUserName.getText();
            String password = "admin1234*";//this.tfPassword.getText();
            UserLoginDto userLoginDto = new UserLoginDto(userName, password);
            try{
                this.auth.login(userLoginDto);
            } catch (IllegalArgumentException err){
                CustomAlert.showAlert(err.getMessage(), CustomAlert.ERROR);
            }
            Views views = Views.getInstance();
            try {
                views.buildWindow(Views.NameOfViews.HOME, "Home");
            } catch (IOException e) {
                log.error(String.valueOf(e));
                CustomAlert.showAlert("Ocurrió un error tratando de abrir: Home", CustomAlert.ERROR);
            }
            views.getCurrentStage().show();
            views.closePreviousStage();

        };
    }
}