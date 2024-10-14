package com.mmdevelopment.controllers;

import com.mmdevelopment.databaselogic.JPAUtil;
import com.mmdevelopment.events.CustomAlert;
import com.mmdevelopment.models.daos.CategoryDao;
import com.mmdevelopment.models.daos.ProductDao;
import com.mmdevelopment.models.daos.ProductTypeDao;
import com.mmdevelopment.models.entities.Category;
import com.mmdevelopment.models.entities.Product;
import com.mmdevelopment.models.entities.ProductType;
import com.mmdevelopment.services.Auth;
import com.mmdevelopment.services.CategoryService;
import com.mmdevelopment.services.ProductService;
import com.mmdevelopment.services.ProductTypeService;
import com.mmdevelopment.viewHandler.Views;
import jakarta.persistence.EntityManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);
    @FXML
    private Button btnSave;

    @FXML
    private ComboBox<Category> cbCategory;

    @FXML
    private TextField tfCode;

    @FXML
    private TextField tfName;

    @FXML
    private ComboBox<ProductType> cbType;

    EntityManager entityManager;
    CategoryService categoryService;
    CategoryDao categoryDao;
    ObservableList<Category> categories;

    ProductTypeService productTypeService;
    ProductTypeDao productTypeDao;
    ObservableList<ProductType> productTypes;

    ProductService productService;
    ProductDao productDao;

    @Setter
    public Product product;

    @FXML
    public void initialize() {
        this.entityManager = JPAUtil.getSession();

        this.categoryDao = new CategoryDao(this.entityManager);
        this.categoryService = new CategoryService(this.categoryDao);

        this.productTypeDao = new ProductTypeDao(this.entityManager);
        this.productTypeService = new ProductTypeService(this.productTypeDao);

        this.productDao = new ProductDao(this.entityManager);
        this.productService = new ProductService(this.productDao);

        this.product = Objects.requireNonNullElse(this.product, new Product());
        fillComboCategory();
        fillComboProductType();
        initFields();

        this.btnSave.setOnAction(saveProduct());

    }

    private void initFields() {
        if (this.product.getId() != 0) {
            this.tfCode.setText(this.product.getCode());
            this.tfName.setText(this.product.getName());
            this.cbType.getSelectionModel().select(this.product.getProductType());
            this.cbCategory.getSelectionModel().select(this.product.getCategory());
        }
    }

    private void fillComboCategory() {
        this.categories = FXCollections.observableArrayList(
                this.categoryService.findAll()
        );
        this.cbCategory.setItems(categories);
        this.cbCategory.setConverter(new StringConverter<Category>() {
            @Override
            public String toString(Category object) {
                return object != null ? object.getName() : "";
            }

            @Override
            public Category fromString(String string) {
                return null;
            }
        });
    }

    private void fillComboProductType() {
        this.productTypes = FXCollections.observableArrayList(
                this.productTypeService.findAll()
        );
        this.cbType.setItems(this.productTypes);
        this.cbType.setConverter(new StringConverter<ProductType>() {
            @Override
            public String toString(ProductType object) {
                return object != null ? object.getName() : "";
            }

            @Override
            public ProductType fromString(String string) {
                return null;
            }
        });
    }

    private EventHandler<ActionEvent> saveProduct() {
        return event -> {
            String name = this.tfName.getText();
            String code = this.tfCode.getText();
            ProductType productType = this.cbType.getValue();
            Category category = this.cbCategory.getValue();

            this.product.setName(name);
            this.product.setCode(code);
            this.product.setProductType(productType);
            this.product.setCategory(category);
            this.product.setUser(Auth.loggedInUser);

            try{
                this.productService.validate(this.product);
                this.product = this.productService.save(this.product);
                ObservableList<Product> list = Views.getListOf(Views.NameOfList.PRODUCT);
                if (list != null) {
                    if (list.contains(this.product)){
                        list.set(list.indexOf(this.product), this.product);
                    }else{
                        list.add(this.product);
                        this.product = new Product();
                        setFieldsEmpty();
                    }
                }
                CustomAlert.showAlert("Producto guardado exitosamente", CustomAlert.INFORMATION);
            } catch (IllegalArgumentException err) {
                CustomAlert.showAlert(err.getMessage(), CustomAlert.ERROR);
            }
        };
    }

    private void setFieldsEmpty() {
        this.tfName.setText("");
        this.tfCode.setText("");
        this.cbType.setValue(null);
        this.cbCategory.setValue(null);
    }

}

