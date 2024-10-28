package com.mmdevelopment.controllers;

import com.mmdevelopment.databaselogic.JPAUtil;
import com.mmdevelopment.events.CustomAlert;
import com.mmdevelopment.models.daos.ProductDao;
import com.mmdevelopment.models.entities.Product;
import com.mmdevelopment.services.ProductService;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Optional;

@Slf4j
public class ProductGeneralContentController {
    @FXML
    private Button btnCreate;

    @FXML
    private Button btnSearch;

    @FXML
    private Label lbName;

    @FXML
    private TableView<Product> tbList;

    @FXML
    private TextField tfSearch;

    private ContextMenu tableContextMenu;
    private MenuItem menuItemDelete;
    private MenuItem menuItemUpdate;

    private ObservableList<Product> data;
    private ProductService productService;



    @FXML
    public void initialize() {
        this.lbName.setText("Productos");
        this.productService = new ProductService(new ProductDao(JPAUtil.getSession()));
        this.data = FXCollections.observableArrayList(
                this.productService.getEnabled()
        );
        Views.setListOf(Views.NameOfList.PRODUCT, this.data);
        startTable(this.data);
        this.btnCreate.setOnAction(openSaveProductView());
        this.tfSearch.setOnKeyReleased(searchProduct());
    }

    private void startTable(ObservableList<Product> data) {
        initTableContextMenu();

        TableColumn<Product, String> nameColumn = new TableColumn<>("Nombre");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Product, String> codeColumn = new TableColumn<>("Código");
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));

        TableColumn<Product, String> CategoryNameColumn = new TableColumn<>("Categoría");
        CategoryNameColumn.setCellValueFactory( cellData ->
                new SimpleStringProperty(cellData.getValue().getCategory().getName())
        );

        TableColumn<Product, String> productTypeNameColumn = new TableColumn<>("Tipo");
        productTypeNameColumn.setCellValueFactory( cellData ->
            new SimpleStringProperty(cellData.getValue().getProductType().getName())
        );

        this.tbList.getColumns().remove(0, this.tbList.getColumns().size());

        this.tbList.getColumns().add(codeColumn);
        this.tbList.getColumns().add(nameColumn);
        this.tbList.getColumns().add(CategoryNameColumn);
        this.tbList.getColumns().add(productTypeNameColumn);
        this.tbList.setItems(data);

        this.tbList.setRowFactory(
                tv -> {
                    TableRow row = new TableRow<>();
                    row.setOnMouseClicked(
                        event -> {
                            if (!row.isEmpty() && event.getButton() == MouseButton.SECONDARY && row.isSelected()) {
                                if (this.tbList.getSelectionModel().getSelectedItems().size() > 1) {
                                    if (this.tableContextMenu.getItems().contains(this.menuItemUpdate)) {
                                        this.tableContextMenu.getItems().remove(this.menuItemUpdate);
                                    }
                                }else {
                                    if (!this.tableContextMenu.getItems().contains(this.menuItemUpdate)) {
                                        this.tableContextMenu.getItems().add(this.menuItemUpdate);
                                    }
                                }
                                // Show context menu only if the row is selected and right-clicked
                                this.tableContextMenu.show(row, event.getScreenX(), event.getScreenY());
                            } else {
                                this.tableContextMenu.hide(); // Hide the menu if it's not a valid row
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
                data.setAll(this.productService.getEnabled());
            }else {
                data.setAll(this.productService.getProductByMatch(name));
            }
        };
    }

    private EventHandler<ActionEvent> openSaveProductView() {
        return event -> {
            Views view = Views.getInstance();
            Stage stageOwner = view.getCurrentStage();
            try {
                ProductController controller = new ProductController();
                view.buildWindow(Views.NameOfViews.SAVE_PRODUCT, controller, "Guardar Producto");
                view.getCurrentStage().setResizable(false);
                view.getCurrentStage().setFullScreen(false);
                view.getCurrentStage().initModality(Modality.WINDOW_MODAL);
                view.getCurrentStage().initOwner(stageOwner);
                view.getCurrentStage().showAndWait();
            } catch (IOException e) {
                log.error("ERROR: {}", String.valueOf(e));
                CustomAlert.showAlert("Ocurrió un error tratando de abrir: Guardar Producto", CustomAlert.ERROR);
            }
        };
    }

    private EventHandler<ActionEvent> openUpdateProductView() {
        return event -> {
            Product product = this.tbList.getSelectionModel().getSelectedItem();
            Views view = Views.getInstance();
            try {
                ProductController controller = new ProductController();
                controller.setProduct(product);
                view.buildWindow(Views.NameOfViews.SAVE_PRODUCT, controller,"Guardar Producto");
                view.getCurrentStage().setResizable(false);
                view.getCurrentStage().setFullScreen(false);
                view.getCurrentStage().showAndWait();
            } catch (IOException e) {
                log.error("ERROR: {}", String.valueOf(e));
                CustomAlert.showAlert("Ocurrió un error tratando de abrir: Guardar Producto", CustomAlert.ERROR);
            }
        };
    }

    private  EventHandler<ActionEvent> deleteProduct() {
        return event -> {
            Product product = this.tbList.getSelectionModel().getSelectedItem();
            Optional<ButtonType> result = CustomAlert.showAndWaitAlert(
                    "Se eliminará el producto " + product.getName(),
                    CustomAlert.CONFIRMATION
            );
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (product.getStocks().isEmpty()) {
                    this.productService.delete(product);
                } else {
                    this.productService.setEnabled(product);
                }
                this.data.remove(product);
            }
        };
    }

    private void initTableContextMenu() {
        this.tableContextMenu = new ContextMenu();
        this.menuItemUpdate = new MenuItem("Editar");
        this.menuItemUpdate.setOnAction(openUpdateProductView());
        this.menuItemDelete = new MenuItem("Eliminar");
        this.menuItemDelete.setOnAction(deleteProduct());
        this.tableContextMenu.getItems().addAll(this.menuItemUpdate, this.menuItemDelete);
    }
}
