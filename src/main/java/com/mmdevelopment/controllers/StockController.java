package com.mmdevelopment.controllers;

import com.mmdevelopment.Utilities;
import com.mmdevelopment.events.CustomAlert;
import com.mmdevelopment.models.entities.*;
import com.mmdevelopment.services.*;
import com.mmdevelopment.utils.factories.H2ServiceFactory;
import com.mmdevelopment.viewHandler.Utils;
import com.mmdevelopment.viewHandler.Views;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.util.Duration;
import javafx.util.StringConverter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public class StockController {

    @FXML
    private Button btnSave;

    @FXML
    private ComboBox<Color> cbColor;

    @FXML
    private ComboBox<Size> cbSize;

    @FXML
    private GridPane gridPrice;

    @FXML
    private RowConstraints gridRow;

    @FXML
    private TextField tfMin;

    @FXML
    private TextField tfQuntity;

    @FXML
    private Tooltip tlpPrice;

    @Setter
    private Stock stock;
    private ObservableList<Size> sizes;
    private ObservableList<Color> colors;
    private ObservableList<PriceType> priceTypes;
    private SizeService sizeService;
    private ColorService colorService;
    private PriceTypeService priceTypeService;
    private StockService stockService;
    private List<Price> prices;

    @FXML
    public void initialize() {
        this.sizeService = H2ServiceFactory.getInstance().getSizeService();
        this.colorService = H2ServiceFactory.getInstance().getColorService();
        this.priceTypeService = H2ServiceFactory.getInstance().getPriceTypeService();
        this.stockService = H2ServiceFactory.getInstance().getStockService();
        fillCombos();
        this.priceTypes = FXCollections.observableArrayList(this.priceTypeService.findAll());
        this.stock = Objects.requireNonNullElse(this.stock, new Stock());
        this.prices = new ArrayList<>();
        this.tfQuntity.setTextFormatter(Utils.onlyInteger());
        this.tfMin.setTextFormatter(Utils.onlyInteger());
        chargePricesView();
        initFields();
        this.btnSave.setOnAction(save());
    }

    private void initFields() {
        if (this.stock.getId() != 0) {
            this.tfQuntity.setText(this.stock.getQuantityOnHand().toString());
            this.tfMin.setText(this.stock.getReorderPoint().toString());
            this.cbColor.setValue(this.stock.getColor());
            this.cbSize.setValue(this.stock.getSize());
            try {
                this.gridPrice.getChildren().stream().forEach(
                        node -> {
                            this.stock.getPrices().stream().forEach(
                                    price -> {
                                        if (node.getId() != null && node.getId().equals(price.getPriceType().getPrefix())) {
                                            ((TextField) node).setText(price.getValue().toString());
                                        }
                                    }
                            );
                        }
                );
            } catch (Exception e) {
                log.error("ERROR {}", e);
            }

        }
    }

    private void fillCombos() {
        this.sizes = FXCollections.observableArrayList(this.sizeService.findAll());
        this.cbSize.setItems(this.sizes);
        this.cbSize.setConverter(new StringConverter<Size>() {
            @Override
            public String toString(Size object) {
                return object != null ? object.getName() : "";
            }

            @Override
            public Size fromString(String string) {
                return null;
            }
        });

        this.colors = FXCollections.observableArrayList(this.colorService.findAll());
        this.cbColor.setItems(this.colors);
        this.cbColor.setConverter(new StringConverter<Color>() {
            @Override
            public String toString(Color object) {
                return object != null ? object.getName() : "";
            }

            @Override
            public Color fromString(String string) {
                return null;
            }
        });
    }

    private void chargePricesView() {
        for (int i = 0; i < this.priceTypes.size(); i++) {
            PriceType priceType = this.priceTypes.get(i);

            Price price = new Price();
            price.setStock(this.stock);
            price.setPriceType(priceType);

            Label label = new Label();
            label.setText(priceType.getName());
            this.gridPrice.add(label, 0, i);


            TextField textField = new TextField();
            textField.setId(priceType.getPrefix());
            textField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!Utilities.isEmptyOrNull(newValue)) {
                    price.setValue(Double.valueOf(newValue));
                }
            });
            textField.setTextFormatter(Utils.onlyDouble());
            this.gridPrice.add(textField, 1, i);

            Button buttonHelp = new Button();
            buttonHelp.setText("?");
            buttonHelp.getStyleClass().add("btn-help");
            Tooltip tooltip = new Tooltip(priceType.getDescription());
            tooltip.setShowDuration(Duration.INDEFINITE);
            tooltip.setShowDelay(Duration.millis(500));
            buttonHelp.tooltipProperty().set(tooltip);
            this.gridPrice.add(buttonHelp, 2, i);
            this.prices.add(price);
        }
    }

    private EventHandler<ActionEvent> save() {
        return event -> {
            Integer quantity = Utilities.isEmptyOrNull(this.tfQuntity.getText()) ? null : Integer.valueOf(this.tfQuntity.getText());
            Integer quantityMin = Utilities.isEmptyOrNull(this.tfMin.getText()) ? null : Integer.valueOf(this.tfMin.getText());
            Size size = this.cbSize.getValue();
            Color color = this.cbColor.getValue();

            this.stock.setQuantityOnHand(quantity);
            this.stock.setReorderPoint(quantityMin);
            this.stock.setColor(color);
            this.stock.setSize(size);
            this.stock.setUser(Auth.loggedInUser);
            this.stock.setPrices(this.prices);

            try{
                this.stockService.validate(this.stock);
                this.stockService.areValidatedThePrices(this.stock);
                this.stock = this.stockService.save(this.stock);
                ObservableList<Stock> list = Views.getListOf(Views.NameOfList.STOCK);
                if (list != null) {
                    if (list.contains(this.stock)){
                        list.set(list.indexOf(this.stock), this.stock);
                    }else{
                        list.add(this.stock);
                    }
                }
                CustomAlert.showAlert("Stock guardado exitosamente", CustomAlert.INFORMATION);
            } catch (IllegalArgumentException err) {
                CustomAlert.showAlert(err.getMessage(), CustomAlert.ERROR);
            }
        };
    }

    private void setFieldsEmpty() {
        this.tfQuntity.setText("");
        this.tfMin.setText("");
        this.cbSize.setValue(null);
        this.cbColor.setValue(null);
    }

}
