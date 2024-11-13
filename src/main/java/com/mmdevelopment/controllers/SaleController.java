package com.mmdevelopment.controllers;

import com.mmdevelopment.events.CustomAlert;
import com.mmdevelopment.models.entities.*;
import com.mmdevelopment.services.Auth;
import com.mmdevelopment.services.InvoceService;
import com.mmdevelopment.services.ProductService;
import com.mmdevelopment.services.SalesDetailService;
import com.mmdevelopment.utils.factories.H2ServiceFactory;
import com.mmdevelopment.viewHandler.Views;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;

@Slf4j
public class SaleController {

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnSearch;

    @FXML
    private Button btnSell;

    @FXML
    private Label lbName;

    @FXML
    private Label lbStockProduct;

    @FXML
    private Label lbTotal;

    @FXML
    private TableView<Product> tbProduct;

    @FXML
    private TableView<SalesDetail> tbSale;

    @FXML
    private TextField tfSearch;

    private ObservableList<Product> products;
    private ProductService productService;
    private ObservableList<SalesDetail> salesDetails;
    private SimpleDoubleProperty total;
    private InvoceService invoceService;
    private SalesDetailService salesDetailService;
    private static final String RETAIL_PRICE = "retailPrice";

    @FXML
    public void initialize() {
        this.lbName.setText("Ventas");
        this.total = new SimpleDoubleProperty(0);
        this.lbTotal.textProperty().bind(this.total.asString("%.2f"));
        servicesInitialize();
        listInitializer();

        startProductTable();
        this.salesDetails = Views.getListOf(Views.NameOfList.SALE_DETAILS);
        if (this.salesDetails == null) {
            this.salesDetails = FXCollections.observableArrayList();
            Views.setListOf(Views.NameOfList.SALE_DETAILS, this.salesDetails);
        }
        this.salesDetails.addListener(new ListChangeListener<SalesDetail>() {
            @Override
            public void onChanged(Change<? extends SalesDetail> c) {
                initSaleDetailList();
            }
        });
        startSalesDetailTable();

        initSaleDetailList();
        this.tfSearch.setOnKeyReleased(searchProduct());
        this.btnCancel.setOnAction(cancelSale());
        this.btnSell.setOnAction(paySale());
    }

    private void initSaleDetailList() {
        if (!salesDetails.isEmpty()) {
            total.set(
                this.salesDetailService.getTotalSaleDetails(tbSale.getItems())
            );
        } else {
            total.set(0);
        }
        btnSell.setDisable(salesDetails.isEmpty());
        btnCancel.setDisable(salesDetails.isEmpty());
    }

    private void servicesInitialize() {
        this.productService = H2ServiceFactory.getInstance().getProductService();
        this.invoceService = H2ServiceFactory.getInstance().getInvoceService();
        this.salesDetailService = H2ServiceFactory.getInstance().getSalesDetailService();
    }

    private void listInitializer() {
        this.products = FXCollections.observableArrayList(
                this.productService.getWithStock()
        );
        Views.setListOf(Views.NameOfList.PRODUCT, this.products);
    }

    private void startProductTable() {

        TableColumn<Product, String> nameColumn = new TableColumn<>("Nombre");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Product, String> codeColumn = new TableColumn<>("Código");
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));

        TableColumn<Product, String> categoryNameColumn = new TableColumn<>("Categoría");
        categoryNameColumn.setCellValueFactory( cellData ->
                new SimpleStringProperty(cellData.getValue().getCategory().getName())
        );

        this.tbProduct.getColumns().remove(0, this.tbProduct.getColumns().size());
        this.tbProduct.getColumns().add(codeColumn);
        this.tbProduct.getColumns().add(nameColumn);
        this.tbProduct.getColumns().add(categoryNameColumn);
        this.tbProduct.setItems(this.products);

        this.tbProduct.setRowFactory(
                tv -> {
                    TableRow row = new TableRow<>();
                    row.setOnMouseClicked(
                            event -> {
                                if (row.isSelected() && (event.getClickCount() == 2) && (event.getButton() == MouseButton.PRIMARY)) {
                                    Product product = (Product) row.getTableView().getSelectionModel().getSelectedItem();
                                    SelectStockController selectStockController = new SelectStockController();
                                    selectStockController.setProduct(product);
                                    Views.getInstance().showModal(Views.NameOfViews.SELECT_STOCK, "Seleccionar combinación de existencia", selectStockController);
                                    Map<String, Object> map = (Map) Views.getInstance().getCurrentStage().getUserData();
                                    if (map != null) {
                                        Stock stock = (Stock) map.get("stock");
                                        int quantity = (int) map.get("quantity");
                                        stock.setQuantityOnHand(stock.getQuantityOnHand() - quantity);
                                        SalesDetail salesDetail = new SalesDetail();
                                        salesDetail.setStock(stock);
                                        Optional<Price> optionalPrice = stock.getPrices().stream().filter(price -> price.getPriceType().getPrefix().equals(RETAIL_PRICE)).findFirst();
                                        if (optionalPrice.isEmpty()) {
                                            CustomAlert.showAlert("La existencia escogida no tiene precio de venta", CustomAlert.ERROR);
                                        } else {
                                            salesDetail.setPrice(optionalPrice.get());
                                            salesDetail.setQuantity(quantity);
                                            this.salesDetails.add(salesDetail);
                                        }
                                        Views.getInstance().getCurrentStage().setUserData(null);
                                    }
                                }
                            }
                    );
                    return row;
                }
        );

    }

    private EventHandler<KeyEvent> searchProduct() {
        return event -> {
            String name = this.tfSearch.getText();
            if (name.isEmpty()) {
                this.products.setAll(this.productService.getWithStock());
            }else {
                this.products.setAll(this.productService.getProductByMatch(name));
            }
        };
    }

    private void startSalesDetailTable() {
        
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

        this.tbSale.getColumns().setAll(
                productCodColumn,
                productNameColumn,
                productSizeColumn,
                productColorColumn,
                quantityColumn,
                priceColumn,
                totalColumn
        );
        this.tbSale.setItems(this.salesDetails);

        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItemDelete = new MenuItem("Eliminar");
        menuItemDelete.setOnAction(
                event -> {
                    SalesDetail salesDetail = this.tbSale.getSelectionModel().getSelectedItem();
                    salesDetail.getStock().setQuantityOnHand(salesDetail.getStock().getQuantityOnHand() + salesDetail.getQuantity());
                    this.salesDetails.remove(salesDetail);
                }
        );
        contextMenu.getItems().addAll(menuItemDelete);
        this.tbSale.setRowFactory(
                tv -> {
                    TableRow row = new TableRow<>();
                    row.setOnMouseClicked(
                            event -> {
                                if (!row.isEmpty() && event.getButton() == MouseButton.SECONDARY && row.isSelected()) {
                                    // Show context menu only if the row is selected and right-clicked
                                    contextMenu.show(row, event.getScreenX(), event.getScreenY());
                                } else {
                                    contextMenu.hide(); // Hide the menu if it's not a valid row
                                }
                            }
                    );
                    return row;
                }
        );
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

    private EventHandler<ActionEvent> cancelSale() {
        return event -> {
            Optional<ButtonType> result = CustomAlert.showAndWaitAlert(
                    "¿Está seguro que desea cancelar la venta?",
                    CustomAlert.CONFIRMATION
            );
            if (result.isPresent() && result.get() == ButtonType.OK) {
                for (SalesDetail salesDetail: this.tbSale.getItems()) {
                    salesDetail.getStock().setQuantityOnHand(salesDetail.getStock().getQuantityOnHand() + salesDetail.getQuantity());
                }
                this.salesDetails.removeAll(this.salesDetails);
            }
        };
    }

    private EventHandler<ActionEvent> paySale() {
        return event -> {
            Optional<ButtonType> result = CustomAlert.showAndWaitAlert(
                    "Se realizará la venta con valor total de " + this.total.getValue(),
                    CustomAlert.CONFIRMATION
            );
            if (result.isPresent() && result.get() == ButtonType.OK) {
                Invoice invoice = new Invoice();
                invoice.setUser(Auth.loggedInUser);
                this.tbSale.getItems().forEach(salesDetail -> salesDetail.setInvoice(invoice));
                invoice.setSalesDetails(this.tbSale.getItems());
                invoice.setPay(true);
                this.invoceService.save(invoice);
                CustomAlert.showAlert("La venta fue realizada exitosamente", CustomAlert.INFORMATION);
                this.salesDetails.removeAll(this.salesDetails);
            }
        };
    }

}

