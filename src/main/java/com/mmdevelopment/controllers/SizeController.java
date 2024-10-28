package com.mmdevelopment.controllers;
import com.mmdevelopment.events.CustomAlert;
import com.mmdevelopment.models.entities.Size;
import com.mmdevelopment.services.SizeService;
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
public class SizeController {

    @FXML
    private Button btnCreate;

    @FXML
    private Label lbName;

    @FXML
    private TableView<Size> tbElements;

    @FXML
    private TableColumn<Size, String> tcColumn;

    @FXML
    private TextField tfSearch;

    private ObservableList<Size> sizes;
    private SizeService sizeService = H2ServiceFactory.getInstance().getSizeService();

    @FXML
    public void initialize() {
        this.lbName.setText("Colores");
        this.sizes = FXCollections.observableArrayList(
                sizeService.getEnabled()
        );
        startColorTable();
        this.tfSearch.setOnKeyReleased(search());
        this.btnCreate.setOnAction(save());
    }

    private void startColorTable() {
        this.tcColumn.setText("Tamaños");
        this.tcColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        this.tbElements.setItems(this.sizes);
        menuInRows();
    }

    private EventHandler<KeyEvent> search() {
        return event -> {
            String name = this.tfSearch.getText();
            this.sizes.remove(0, this.sizes.size());
            if (name.isEmpty()) {
                this.sizes.addAll(this.sizeService.getEnabled());
            }else {
                this.sizes.addAll(this.sizeService.getSizeByMatch(name));
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
                Size size = new Size(this.tfSearch.getText());
                Optional<ButtonType> result = CustomAlert.showAndWaitAlert(
                        "Se agregará el tamaño: " + size.getName(),
                        CustomAlert.CONFIRMATION
                );
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    this.sizeService.save(size);
                    this.sizes.add(size);
                    CustomAlert.showAlert("Tamaño guardado exitosamente", CustomAlert.INFORMATION);
                }
            } catch (IllegalArgumentException err) {
                CustomAlert.showAlert(err.getMessage(), CustomAlert.ERROR);
            }
        };
    }

    private EventHandler<ActionEvent> delete() {
        return event -> {
            Size size = this.tbElements.getSelectionModel().getSelectedItem();
            if (size.getStocks().isEmpty()) {
                this.sizeService.delete(size);
            } else {
                this.sizeService.setEnabled(size);
            }
            this.sizes.remove(size);
            CustomAlert.showAlert("Tamaño eliminado exitosamente", CustomAlert.INFORMATION);
        };
    }

}
