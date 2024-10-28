package com.mmdevelopment.controllers;
import com.mmdevelopment.events.CustomAlert;
import com.mmdevelopment.models.entities.Color;
import com.mmdevelopment.services.ColorService;
import com.mmdevelopment.utils.factories.H2ServiceFactory;
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

import java.util.Optional;

@Slf4j
public class ColorController {

    @FXML
    private Button btnCreate;

    @FXML
    private Label lbName;

    @FXML
    private TableView<Color> tbElements;

    @FXML
    private TableColumn<Color, String> tcColumn;

    @FXML
    private TextField tfSearch;

    private ObservableList<Color> colors;
    private ColorService colorService = H2ServiceFactory.getInstance().getColorService();

    @FXML
    public void initialize() {
        this.lbName.setText("Colores");
        this.colors = FXCollections.observableArrayList(
                colorService.getEnabled()
        );
        startColorTable();
        this.tfSearch.setOnKeyReleased(search());
        this.btnCreate.setOnAction(save());
    }

    private void startColorTable() {
        this.tcColumn.setText("Colores");
        this.tcColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        this.tbElements.setItems(this.colors);
        menuInRows();
    }

    private EventHandler<KeyEvent> search() {
        return event -> {
            String name = this.tfSearch.getText();
            this.colors.remove(0, this.colors.size());
            if (name.isEmpty()) {
                this.colors.addAll(this.colorService.getEnabled());
            }else {
                this.colors.addAll(this.colorService.getColorByMatch(name));
            }
        };
    }

    private void menuInRows() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItemCreate = new MenuItem("Eliminar");
        menuItemCreate.setOnAction(delete());
        contextMenu.getItems().addAll(menuItemCreate);
        this.tbElements.setRowFactory(
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

    private EventHandler<ActionEvent> save() {
        return event -> {
            try {
                Color color = new Color(this.tfSearch.getText());
                Optional<ButtonType> result = CustomAlert.showAndWaitAlert(
                        "Se agregará el color: " + color.getName(),
                        CustomAlert.CONFIRMATION
                );
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    this.colorService.save(color);
                    this.colors.add(color);
                    CustomAlert.showAlert("Color guardado exitosamente", CustomAlert.INFORMATION);
                }
            } catch (IllegalArgumentException err) {
                CustomAlert.showAlert(err.getMessage(), CustomAlert.ERROR);
            }
        };
    }

    private EventHandler<ActionEvent> delete() {
        return event -> {
            Color color = this.tbElements.getSelectionModel().getSelectedItem();
            if (color.getStocks().isEmpty()) {
                this.colorService.delete(color);
            } else {
                this.colorService.setEnabled(color);
            }
            this.colors.remove(color);
            CustomAlert.showAlert("Color eliminado exitosamente", CustomAlert.INFORMATION);
        };
    }

}
