package com.mmdevelopment.controllers;

import com.mmdevelopment.events.CustomAlert;
import com.mmdevelopment.models.entities.Invoice;
import com.mmdevelopment.models.entities.SalesDetail;
import com.mmdevelopment.services.InvoceService;
import com.mmdevelopment.services.SalesDetailService;
import com.mmdevelopment.utils.factories.H2ServiceFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;

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
    private TableView<Invoice> tbInvoices;

    @FXML
    private TableView<SalesDetail> tbSalesDetails;

    private ObservableList<Invoice> invoices;
    private InvoceService invoceService;
    private ObservableList<SalesDetail> salesDetails;
    private SalesDetailService salesDetailService;

    @FXML
    public void initialize() {

        this.lbName.setText("Registro de ventas");
        this.invoices = FXCollections.observableArrayList();
        this.invoceService = H2ServiceFactory.getInstance().getInvoceService();
        this.salesDetailService = H2ServiceFactory.getInstance().getSalesDetailService();

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

        startInvoiceTable();

        this.invoices.addListener(
                new ListChangeListener<Invoice>() {
                    @Override
                    public void onChanged(Change<? extends Invoice> change) {
                        lbTotal1.setText(String.valueOf(invoceService.getTotalByInvoices(tbInvoices.getItems())));
                    }
                }
        );

    }

    private void startInvoiceTable() {
        TableColumn<Invoice, String> idColumn = new TableColumn<>("Identificador");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Invoice, String> createdAtColumn = new TableColumn<>("Fecha de creación");
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        TableColumn<Invoice, String> totalPayColumn = new TableColumn<>("Total pagado");
        totalPayColumn.setCellValueFactory( cellData ->
                new SimpleStringProperty(
                        String.valueOf(this.invoceService.getTotalByInvoice(cellData.getValue()))
                )
        );

        TableColumn<Invoice, String> payColumn = new TableColumn<>("Estado");
        payColumn.setCellValueFactory( cellData -> {
            SimpleStringProperty result = new SimpleStringProperty();
            if (cellData.getValue().isPay()) {
                result.set("Pagado");
            } else {
                result.set("Pendiente de pago");
            }
            return  result;
        });
        this.tbInvoices.getColumns().setAll(idColumn, createdAtColumn, totalPayColumn, payColumn);
        this.tbInvoices.setItems(this.invoices);
    }

}
