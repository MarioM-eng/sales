package com.mmdevelopment.controllers;

import com.mmdevelopment.events.CustomAlert;
import com.mmdevelopment.services.StockService;
import com.mmdevelopment.utils.factories.H2ServiceFactory;
import com.mmdevelopment.viewHandler.Views;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class HomeController {

    @FXML
    private Button btnInvoices;

    @FXML
    private Button btnProducts;

    @FXML
    private Button btnSales;

    @FXML
    private Button btnStocks;

    @FXML
    private Menu mnMain;

    @FXML
    private ScrollPane spForChange;

    @FXML
    public void initialize() {
        firstView();
        this.btnProducts.setOnAction(loadProductView());
        this.btnStocks.setOnAction(loadStockListView());
        this.btnSales.setOnAction(loadSaleListView());
        this.btnInvoices.setOnAction(loadInvoiceListView());
        initMenu();
    }

    private void initMenu() {
        MenuItem sizesItem = new MenuItem("Tamaños");
        MenuItem colorsItem = new MenuItem("Colores");
        MenuItem closedItem = new MenuItem("Cerrar");

        colorsItem.setOnAction(event -> {
            Views.getInstance().showModal(Views.NameOfViews.LITTLE_OBJECTS, "Colores", new ColorController());
        });

        sizesItem.setOnAction(event -> {
            Views.getInstance().showModal(Views.NameOfViews.LITTLE_OBJECTS, "Tamaños", new SizeController());
        });

        closedItem.setOnAction(event -> {
            System.exit(0);
        });

        this.mnMain.getItems().setAll(sizesItem, colorsItem, closedItem);
        validateProductStock();
    }

    private void firstView() {
        Node node = null;
        try {
            node = Views.getParentNodeOf(Views.NameOfViews.MAKE_SALES, new SaleController());
        } catch (IOException e) {
            log.error(String.valueOf(e));
            CustomAlert.showAlert("Ocurrió un error tratando de abrir: Ventas", CustomAlert.ERROR);
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

    public EventHandler<ActionEvent> loadStockListView() {
        return event -> {
            Node node = null;
            try {
                node = Views.getParentNodeOf(Views.NameOfViews.STOCK_LIST, new StockListController());
            } catch (IOException e) {
                log.error(String.valueOf(e));
                CustomAlert.showAlert("Ocurrió un error tratando de abrir: Existencias", CustomAlert.ERROR);
            }
            this.spForChange.setContent(node);
        };
    }

    public EventHandler<ActionEvent> loadSaleListView() {
        return event -> {
            firstView();
        };
    }

    public EventHandler<ActionEvent> loadInvoiceListView() {
        return event -> {
            Node node = null;
            try {
                node = Views.getParentNodeOf(Views.NameOfViews.INVOICE_LIST, new InvoiceListController());
            } catch (IOException e) {
                log.error(String.valueOf(e));
                CustomAlert.showAlert("Ocurrió un error tratando de abrir: Facturas", CustomAlert.ERROR);
            }
            this.spForChange.setContent(node);
        };
    }

    private void validateProductStock() {
        StockService service = H2ServiceFactory.getInstance().getStockService();
        List<String> products = service.getProductsBelowMinStock()
                .stream()
                .map(stock -> stock.getProduct().getCode() + " - " + stock.getProduct().getName())
                .distinct().toList();
        if (!products.isEmpty()) {
            CustomAlert.showAndWaitAlert(
                    "Los siguientes productos tienen las mínimas existencias permitidas: \n" +
                    products.stream().collect(Collectors.joining("\n")),
                    CustomAlert.INFORMATION);
        }
    }

}
