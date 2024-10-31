package com.mmdevelopment.controllers;

import com.mmdevelopment.events.CustomAlert;
import com.mmdevelopment.models.entities.Invoice;
import com.mmdevelopment.services.InvoceService;
import com.mmdevelopment.utils.factories.H2ServiceFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

@Slf4j
public class InvoiceListController {

    @FXML
    private DatePicker dpFrom;

    @FXML
    private DatePicker dpTo;

    @FXML
    private Label lbInvoiceDetail;

    @FXML
    private Label lbName;

    @FXML
    private Label lbTotal;

    @FXML
    private Label lbTotal1;

    @FXML
    private TableView<?> tbInvoices;

    @FXML
    private TableView<?> tbSalesDetails;

    private ObservableList<Invoice> invoices;
    private InvoceService invoceService;

    @FXML
    public void initialize() {

        this.invoices = FXCollections.observableArrayList();
        this.invoceService = H2ServiceFactory.getInstance().getInvoceService();

        this.dpTo.valueProperty().addListener((observable, oldDate, newDate) -> {
            if (newDate != null) {
                // Llama a la función que quieres ejecutar con la nueva fecha seleccionada
                if (this.dpFrom.getValue() == null) {
                    CustomAlert.showAlert("Debe seleccionar una fecha de inicio", CustomAlert.WARNING);
                    this.dpTo.setValue(null);
                } else {
                    this.invoices.setAll(
                            this.invoceService.findAll().stream().filter(invoice -> {
                                int resultComparationFrom = invoice.getCreatedAt().toLocalDate().compareTo(this.dpFrom.getValue());
                                int resultComparationTo = invoice.getCreatedAt().toLocalDate().compareTo(this.dpTo.getValue());
                                return resultComparationFrom >= 0 && resultComparationTo <= 0;
                            }).toList()
                    );
                }
            }
        });

        this.dpFrom.valueProperty().addListener((observable, oldDate, newDate) -> {
            this.dpTo.setValue(null);
        });

    }

    private void startInvoiceTable() {
        TableColumn<Invoice, String> idColumn = new TableColumn<>("Identificador");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Invoice, String> createdAtColumn = new TableColumn<>("Fecha de creación");
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        TableColumn<Invoice, String> payColumn = new TableColumn<>("Total pagado");
        payColumn.setCellValueFactory( cellData ->
                new SimpleStringProperty(
                        String.valueOf(totalByInvoice(cellData.getValue()))
                )
        );
    }

    private double totalByInvoice(Invoice invoice) {
        return invoice.getSalesDetails()
                .stream()
                .map(salesDetail -> salesDetail.getPrice().getValue())
                .mapToDouble(Double::doubleValue).sum();
    }

}
