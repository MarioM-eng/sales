package com.mmdevelopment.controllers;

import com.mmdevelopment.events.CustomAlert;
import com.mmdevelopment.models.entities.Price;
import com.mmdevelopment.models.entities.Product;
import com.mmdevelopment.models.entities.Stock;
import com.mmdevelopment.services.ProductService;
import com.mmdevelopment.services.StockService;
import com.mmdevelopment.utils.factories.H2ServiceFactory;
import com.mmdevelopment.viewHandler.Views;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Slf4j
public class StockListController {

    @FXML
    private Button btnCreate;

    @FXML
    private Button btnSearch;

    @FXML
    private Label lbName;

    @FXML
    private Label lbStockProduct;

    @FXML
    private TableView<Product> tbProduct;

    @FXML
    private TableView<Stock> tbStock;

    @FXML
    private TextField tfSearch;

    private ObservableList<Stock> stocks;
    private StockService stockService;
    private ObservableList<Product> products;
    private ProductService productService;


    @FXML
    public void initialize() {
        this.lbName.setText("Existencias");
        servicesInitialize();
        listInitializer();

        startProductTable();
        startStockTable();

        this.btnCreate.setDisable(true);
        this.btnCreate.setOnAction(openCreateStockView());
        this.tfSearch.setOnKeyReleased(searchProduct());
    }

    private void servicesInitialize() {
        this.productService = H2ServiceFactory.getInstance().getProductService();
        this.stockService = H2ServiceFactory.getInstance().getStockService();
    }

    private void listInitializer() {
        this.products = FXCollections.observableArrayList(
                this.productService.getEnabled()
        );
        Views.setListOf(Views.NameOfList.PRODUCT, this.products);
        this.stocks = FXCollections.observableArrayList();
        Views.setListOf(Views.NameOfList.STOCK, this.stocks);
    }

    private void startStockTable() {

        TableColumn<Stock, String> quantityColumn = new TableColumn<>("Cantidad");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantityOnHand"));

        TableColumn<Stock, String> quantityMinColumn = new TableColumn<>("Cant. mínima");
        quantityMinColumn.setCellValueFactory(new PropertyValueFactory<>("reorderPoint"));
        quantityMinColumn.setPrefWidth(100);

        TableColumn<Stock, String> sizeColumn = new TableColumn<>("Tamaño");
        sizeColumn.setCellValueFactory( cellData ->
                new SimpleStringProperty(cellData.getValue().getSize().getName())
        );

        TableColumn<Stock, String> colorColumn = new TableColumn<>("Color");
        colorColumn.setCellValueFactory( cellData ->
                new SimpleStringProperty(cellData.getValue().getColor().getName())
        );

        TableColumn<Stock, String> priceColumn = getStockPriceTableColumn();

        this.tbStock.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.tbStock.getColumns().remove(0, this.tbStock.getColumns().size());
        this.tbStock.getColumns().add(priceColumn);
        this.tbStock.getColumns().add(sizeColumn);
        this.tbStock.getColumns().add(colorColumn);
        this.tbStock.getColumns().add(quantityColumn);
        this.tbStock.getColumns().add(quantityMinColumn);
        this.tbStock.setItems(this.stocks);

        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItemUpdate = new MenuItem("Editar");
        menuItemUpdate.setOnAction(openUpdateStockView());
        MenuItem menuItemDelete = new MenuItem("Eliminar");
        menuItemDelete.setOnAction(disableStock());
        contextMenu.getItems().addAll(menuItemUpdate, menuItemDelete);

        this.tbStock.setRowFactory(
                tv -> {
                    TableRow row = new TableRow<>();
                    row.setOnMouseClicked(
                            event -> {
                                if (!row.isEmpty() && event.getButton() == MouseButton.SECONDARY && row.isSelected()) {
                                    if (this.tbStock.getSelectionModel().getSelectedItems().size() > 1) {
                                        if (contextMenu.getItems().contains(menuItemUpdate)) {
                                            contextMenu.getItems().remove(menuItemUpdate);
                                        }
                                    }else {
                                        if (!contextMenu.getItems().contains(menuItemUpdate)) {
                                            contextMenu.getItems().add(menuItemUpdate);
                                        }
                                    }
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

    private static TableColumn<Stock, String> getStockPriceTableColumn() {
        TableColumn<Stock, String> priceColumn = new TableColumn<>("Precio");
        priceColumn.setCellValueFactory( cellData -> {

            Optional<Price> priceOptional = cellData.getValue()
                    .getPrices()
                    .stream()
                    .sorted(Comparator.comparing(Price::getCreatedAt).reversed())
                    .filter(price -> price.getPriceType().getPrefix().equals("retailPrice"))
                    .findFirst();

            return new SimpleStringProperty(
                    !priceOptional.isEmpty() ? priceOptional.get().getValue().toString() : "0"
            );
        });
        return priceColumn;
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

        this.tbProduct.setOnMouseClicked(whenMouseClickedProduct());

        ContextMenu contextMenu = initProductTableContextMenu();
        this.tbProduct.setRowFactory(
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

    private ContextMenu initProductTableContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItemCreate = new MenuItem("Crear stock");
        menuItemCreate.setOnAction(openCreateStockView());
        contextMenu.getItems().addAll(menuItemCreate);
        return contextMenu;
    }

    private EventHandler<MouseEvent> whenMouseClickedProduct() {
        return event -> {
            setStockButtonEnabled();
            setStockTableData();
        };
    }

    private void setStockTableData() {
        Product product = this.tbProduct.getSelectionModel().getSelectedItem();
        this.stocks.clear();
        List<Stock> stockList = product.getStocks();
        if (stockList != null && !stockList.isEmpty()) {
            this.stocks.setAll(product.getStocks().stream().filter(Stock::isEnabled).toList());
            this.lbStockProduct.setText(product.getName());
        }
    }

    private void setStockButtonEnabled() {
        Product product = this.tbProduct.getSelectionModel().getSelectedItem();
        this.btnCreate.setDisable(product == null);
    }

    private EventHandler<ActionEvent> openUpdateStockView() {
        return event -> {
            Stock stock = this.tbStock.getSelectionModel().getSelectedItem();
            StockController controller = new StockController();
            controller.setStock(stock);
            Views view = Views.getInstance();
            view.showModal(Views.NameOfViews.SAVE_STOCK, "Guardar existencias", controller);
        };
    }

    private EventHandler<ActionEvent> openCreateStockView() {
        return event -> {
            Views view = Views.getInstance();
            Stock stock = new Stock();
            stock.setProduct(this.tbProduct.getSelectionModel().getSelectedItem());
            StockController controller = new StockController();
            controller.setStock(stock);
            view.showModal(Views.NameOfViews.SAVE_STOCK, "Guardar existencias", controller);
        };
    }

    private EventHandler<ActionEvent> disableStock() {
        return event -> {
            List<Stock> stocksSelectionModel = this.tbStock.getSelectionModel().getSelectedItems();

            String selectMessage = (stocksSelectionModel.size() > 1) ? "las existencias" : "la existencia";
            Optional<ButtonType> result = CustomAlert.showAndWaitAlert(
                    "¿Está seguro que desea eliminar "+ selectMessage + "?",
                    CustomAlert.CONFIRMATION
            );
            if (result.isPresent() && result.get() == ButtonType.OK) {
                this.stockService.setEnabled(stocksSelectionModel.toArray(new Stock[0]));
                this.stocks.removeAll(stocksSelectionModel);
            }
        };
    }

    private EventHandler<KeyEvent> searchProduct() {
        return event -> {
            String name = this.tfSearch.getText();
            this.products.remove(0, this.products.size());
            if (name.isEmpty()) {
                this.products.addAll(this.productService.findAll());
            }else {
                this.products.addAll(this.productService.getProductByMatch(name));
            }
        };
    }

}
