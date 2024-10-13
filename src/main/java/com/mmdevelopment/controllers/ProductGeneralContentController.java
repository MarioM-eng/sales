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
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

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

    ObservableList<Product> data;
    ProductService productService;



    @FXML
    public void initialize() {
        this.lbName.setText("Productos");
        this.productService = new ProductService(new ProductDao(JPAUtil.getSession()));
        this.data = FXCollections.observableArrayList(
                this.productService.findAll()
        );
        Views.setListOf(Views.NameOfList.PRODUCT, this.data);
        startTable(this.data);
        btnCreate.setOnAction(openSaveProductView());
        tfSearch.setOnKeyReleased(searchProduct());
    }

    private void startTable(ObservableList<Product> data) {
        ContextMenu contextMenu = generateContextMenu();

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

    private EventHandler<KeyEvent> searchProduct() {
        return event -> {
            String name = this.tfSearch.getText();
            data.remove(0, data.size());
            if (name.equals("")) {
                data.addAll(this.productService.findAll());
            }else {
                data.addAll(this.productService.getProductByMatch(name));
            }
        };
    }

    private EventHandler<ActionEvent> openSaveProductView() {
        return event -> {
            Views view = Views.getInstance();
            try {
                ProductController controller = new ProductController();
                view.buildWindow(Views.NameOfViews.SAVE_PRODUCT, controller, "Guardar Producto");
                view.getCurrentStage().setResizable(false);
                view.getCurrentStage().setFullScreen(false);
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
            this.productService.delete(product);
            this.data.remove(product);
        };
    }

    private ContextMenu generateContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItem1 = new MenuItem("Editar");
        menuItem1.setOnAction(openUpdateProductView());
        MenuItem menuItem2 = new MenuItem("Eliminar");
        menuItem2.setOnAction(deleteProduct());
        contextMenu.getItems().addAll(menuItem1, menuItem2);
        return contextMenu;
    }
}
