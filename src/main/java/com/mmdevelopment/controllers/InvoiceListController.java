package com.mmdevelopment.controllers;

import com.mmdevelopment.Utilities;
import com.mmdevelopment.events.CustomAlert;
import com.mmdevelopment.models.entities.*;
import com.mmdevelopment.services.InvoceService;
import com.mmdevelopment.services.SalesDetailService;
import com.mmdevelopment.utils.factories.H2ServiceFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import lombok.extern.slf4j.Slf4j;

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
    private Label lbTotalPerDetails;

    @FXML
    private Label lbTotalPerInvoices;

    @FXML
    private TableView<Invoice> tbInvoices;

    @FXML
    private TableView<SalesDetail> tbSalesDetails;

    private ObservableList<Invoice> invoices;
    private InvoceService invoceService;
    private ObservableList<SalesDetail> salesDetails;
    private SalesDetailService salesDetailService;
    private static final String RETAIL_PRICE = "retailPrice";

    @FXML
    public void initialize() {

        this.lbName.setText("Registro de ventas");
        this.invoices = FXCollections.observableArrayList();
        this.invoceService = H2ServiceFactory.getInstance().getInvoceService();
        this.salesDetailService = H2ServiceFactory.getInstance().getSalesDetailService();
        this.salesDetails = FXCollections.observableArrayList();

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

        this.dpFrom.valueProperty().addListener((observable, oldDate, newDate) -> this.dpTo.setValue(null));

        startInvoiceTable();
        startSaleDetailsTable();

        this.invoices.addListener(
                new ListChangeListener<Invoice>() {
                    @Override
                    public void onChanged(Change<? extends Invoice> change) {
                        lbTotalPerInvoices.setText(
                                Utilities.getCurrencyFormat(
                                    invoceService.getTotalByInvoices(tbInvoices.getItems())
                                )
                        );
                    }
                }
        );

        this.salesDetails.addListener(
                new ListChangeListener<SalesDetail>() {
                    @Override
                    public void onChanged(Change<? extends SalesDetail> change) {
                        lbTotalPerDetails.setText(
                                Utilities.getCurrencyFormat(
                                    salesDetailService.getTotalSaleDetails(tbSalesDetails.getItems())
                                )
                        );
                    }
                }
        );

    }

    private void startInvoiceTable() {
        TableColumn<Invoice, String> idColumn = new TableColumn<>("Id");
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

        this.tbInvoices.setRowFactory(
                tv -> {
                    TableRow row = new TableRow<>();
                    row.setOnMouseClicked(
                            event -> {
                                if (row.isSelected() && (event.getButton() == MouseButton.PRIMARY)) {
                                    Invoice invoice = (Invoice) row.getTableView().getSelectionModel().getSelectedItem();
                                    this.salesDetails.setAll(invoice.getSalesDetails());
                                    this.lbInvoiceDetail.setText("Detalle de factura N° " + invoice.getId());
                                }
                            }
                    );
                    return row;
                }
        );
    }

    private void startSaleDetailsTable() {
        TableColumn<SalesDetail, String> quantityColumn = new TableColumn<>("Cantidad");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<SalesDetail, String> productCodColumn = new TableColumn<>("Cod. producto");
        productCodColumn.setCellValueFactory( cellData ->
                new SimpleStringProperty(cellData.getValue().getStock().getProduct().getCode())
        );

        TableColumn<SalesDetail, String> productNameColumn = new TableColumn<>("Nombre");
        productNameColumn.setCellValueFactory( cellData ->
                new SimpleStringProperty(cellData.getValue().getStock().getProduct().getName())
        );

        TableColumn<SalesDetail, String> productSizeColumn = new TableColumn<>("Tamaño");
        productSizeColumn.setCellValueFactory( cellData ->
                new SimpleStringProperty(cellData.getValue().getStock().getSize().getName())
        );

        TableColumn<SalesDetail, String> productColorColumn = new TableColumn<>("Color");
        productColorColumn.setCellValueFactory( cellData ->
                new SimpleStringProperty(cellData.getValue().getStock().getColor().getName())
        );

        TableColumn<SalesDetail, String> priceColumn = getPriceColumn();

        TableColumn<SalesDetail, String> totalColumn = getTotalColumn();

        this.tbSalesDetails.getColumns().setAll(
                productCodColumn,
                productNameColumn,
                productSizeColumn,
                productColorColumn,
                quantityColumn,
                priceColumn,
                totalColumn
        );
        this.tbSalesDetails.setItems(this.salesDetails);
    }

    private static TableColumn<SalesDetail, String> getTotalColumn() {
        TableColumn<SalesDetail, String> totalColumn = new TableColumn<>("Valor total");
        totalColumn.setCellValueFactory( cellData ->
                new SimpleStringProperty(
                        String.valueOf(
                                cellData.getValue().getStock().getPrices()
                                        .stream()
                                        .filter(price -> price.getPriceType().getPrefix().equals(RETAIL_PRICE))
                                        .findFirst()
                                        .get().getValue() * cellData.getValue().getQuantity()
                        )
                )
        );
        return totalColumn;
    }

    private static TableColumn<SalesDetail, String> getPriceColumn() {
        TableColumn<SalesDetail, String> priceColumn = new TableColumn<>("Valor unitario");
        priceColumn.setCellValueFactory( cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getStock().getPrices()
                                .stream()
                                .filter(price -> price.getPriceType().getPrefix().equals(RETAIL_PRICE))
                                .findFirst()
                                .get().getValue().toString()
                )
        );
        return priceColumn;
    }

}
