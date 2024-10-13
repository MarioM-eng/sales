package com.mmdevelopment.controllers;

import com.mmdevelopment.events.CustomAlert;
import com.mmdevelopment.viewHandler.Views;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class HomeController {

    @FXML
    private Button btnProducts;

    @FXML
    private Button btnSales;

    @FXML
    private Button btnStocks;

    @FXML
    private ScrollPane spForChange;

    @FXML
    public void initialize() {
        firstView();
        btnProducts.setOnAction(loadProductView());
    }

    private void firstView() {
        Node node = null;
        try {
            node = Views.getParentNodeOf(Views.NameOfViews.GENERAL_CONTENT);
        } catch (IOException e) {
            log.error(String.valueOf(e));
            CustomAlert.showAlert("Ocurrió un error tratando de abrir: Contenido general", CustomAlert.ERROR);
        }
        this.spForChange.setContent(node);
    }

    public EventHandler<ActionEvent> loadProductView() {
        return event -> {
            Node node = null;
            try {
                node = Views.getParentNodeOf(Views.NameOfViews.GENERAL_CONTENT, new ProductGeneralContentController());
            } catch (IOException e) {
                log.error(String.valueOf(e));
                CustomAlert.showAlert("Ocurrió un error tratando de abrir: Contenido general", CustomAlert.ERROR);
            }
            this.spForChange.setContent(node);
        };
    }

}
