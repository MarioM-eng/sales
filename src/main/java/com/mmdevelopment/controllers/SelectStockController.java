package com.mmdevelopment.controllers;

import com.mmdevelopment.events.CustomAlert;
import com.mmdevelopment.models.entities.Product;
import com.mmdevelopment.models.entities.Stock;
import com.mmdevelopment.viewHandler.Utils;
import com.mmdevelopment.viewHandler.Views;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectStockController {

    @FXML
    private Button btnAccept;

    @FXML
    private TableView<Stock> tbStock;

    @FXML
    private TextField tfQuantity;

    @Setter
    private Product product;
    private ObservableList<Stock> stocks;

    @FXML
    public void initialize() {
        if (this.product == null) {
            CustomAlert.showAlert("Debe escoger un producto", CustomAlert.ERROR);
            Views.getInstance().getCurrentStage().close();
        }

        List<Stock> stockList = this.product.getStocks().stream().filter(Stock::isEnabled).toList();

        if (stockList.isEmpty()) {
            CustomAlert.showAlert("El producto no tiene existencias activas", CustomAlert.ERROR);
            Views.getInstance().getCurrentStage().close();
        }

        this.stocks = FXCollections.observableArrayList(stockList);

        startTable();

        this.tfQuantity.setTextFormatter(Utils.onlyInteger());
        this.btnAccept.setOnAction(saveData());
    }

    public void startTable() {
        TableColumn<Stock, String> sizeColumn = new TableColumn<>("Tamaño");
        sizeColumn.setCellValueFactory( cellData ->
                new SimpleStringProperty(cellData.getValue().getSize().getName())
        );

        TableColumn<Stock, String> colorColumn = new TableColumn<>("Color");
        colorColumn.setCellValueFactory( cellData ->
                new SimpleStringProperty(cellData.getValue().getColor().getName())
        );

        TableColumn<Stock, String> quantityOnHandsColumn = new TableColumn<>("Existencias");
        quantityOnHandsColumn.setCellValueFactory( cellData ->
                new SimpleStringProperty(cellData.getValue().getQuantityOnHand().toString())
        );

        this.tbStock.getColumns().add(sizeColumn);
        this.tbStock.getColumns().add(colorColumn);
        this.tbStock.getColumns().add(quantityOnHandsColumn);
        this.tbStock.setItems(this.stocks);

    }

    public EventHandler<ActionEvent> saveData() {
        return event -> {
            Stock stock = (Stock) this.tbStock.getSelectionModel().getSelectedItem();
            String quantity = this.tfQuantity.getText();

            if (stock == null) {
                CustomAlert.showAlert("Debe elegir una combinación entre tamaño y color", CustomAlert.ERROR);
            } else if (quantity.isBlank() || Integer.valueOf(quantity) < 1){
                CustomAlert.showAlert("Debe ingresar la cantidad a vender del producto", CustomAlert.ERROR);
            } else if (Integer.valueOf(quantity) > stock.getQuantityOnHand()) {
                CustomAlert.showAlert("La cantidad ingresada supera las existencias del producto", CustomAlert.ERROR);
            } else {
                Map<String, Object> map = new HashMap<>();
                map.put("stock", stock);
                map.put("quantity", Integer.valueOf(quantity));
                Views.getInstance().getPreviousStage().setUserData(map);
                Views.getInstance().getCurrentStage().close();
            }
        };
    }

}
