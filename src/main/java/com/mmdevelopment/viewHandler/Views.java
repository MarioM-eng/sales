package com.mmdevelopment.viewHandler;

import com.mmdevelopment.events.CustomAlert;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class Views {

    private static final Views singleton = new Views();
    private static final Map<String, ObservableList> LISTS = new HashMap();
    @Getter @Setter
    private Stage currentStage;
    private Stage previousStage;

    public enum NameOfViews {
        LOGIN, HOME, GENERAL_CONTENT, SAVE_PRODUCT, STOCK_LIST, SAVE_STOCK, LITTLE_OBJECTS
    }

    public enum NameOfList {
        PRODUCT, STOCK
    }

    private Views() {}

    public static Views getInstance() {
        return singleton;
    }

    public void buildWindow(Views.NameOfViews nameOfViews,String title) throws IOException {
        Parent root = getParentNodeOf(nameOfViews);
        Scene scene = new Scene(root);
        this.previousStage = this.currentStage;
        this.currentStage = new Stage();
        this.currentStage.setScene(scene);
        this.currentStage.setTitle(title);
    }

    public void buildWindow(Views.NameOfViews nameOfViews, Object controller,String title) throws IOException {
        Parent root = getParentNodeOf(nameOfViews, controller);
        Scene scene = new Scene(root);
        this.previousStage = this.currentStage;
        this.currentStage = new Stage();
        this.currentStage.setScene(scene);
        this.currentStage.setTitle(title);
    }

    public void showModal(NameOfViews nameOfViews, String title) {
        Stage stagePrevious = getInstance().getCurrentStage();
        try {
            buildWindow(nameOfViews, title);
            getCurrentStage().setResizable(false);
            getCurrentStage().setFullScreen(false);
            getCurrentStage().initModality(Modality.WINDOW_MODAL);
            getCurrentStage().initOwner(stagePrevious);
            getCurrentStage().showAndWait();
            this.previousStage = null;
            this.currentStage = stagePrevious;

        } catch (IOException e) {
            log.error("ERROR: {}", String.valueOf(e));
            CustomAlert.showAlert("Ocurrió un error tratando de abrir: " + title, CustomAlert.ERROR);
        }
    }

    public void showModal(NameOfViews nameOfViews, String title, Object controller) {
        Stage stagePrevious = getInstance().getCurrentStage();
        try {
            buildWindow(nameOfViews, controller, title);
            getCurrentStage().setResizable(false);
            getCurrentStage().setFullScreen(false);
            getCurrentStage().initModality(Modality.WINDOW_MODAL);
            getCurrentStage().initOwner(stagePrevious);
            getCurrentStage().showAndWait();
            this.previousStage = null;
            this.currentStage = stagePrevious;

        } catch (IOException e) {
            log.error("ERROR: {}", String.valueOf(e));
            CustomAlert.showAlert("Ocurrió un error tratando de abrir: " + title, CustomAlert.ERROR);
        }
    }

    public void closePreviousStage() {
        if (this.previousStage != this.currentStage && this.previousStage != null) {
            this.previousStage.close();
        }
        this.previousStage = null;
    }

    public static Parent getParentNodeOf(Views.NameOfViews nameOfViews, Object controller) throws IOException {
        FXMLLoader loader = new FXMLLoader(Views.class.getResource(getNameOfView(nameOfViews)));
        loader.setController(controller);
        Parent root = null;
        root = loader.load();
        return root;
    }

    public static Parent getParentNodeOf(Views.NameOfViews nameOfViews) throws IOException {
        FXMLLoader loader = new FXMLLoader(Views.class.getResource(getNameOfView(nameOfViews)));
        Parent root = null;
        root = loader.load();
        return root;
    }

    private static String getNameOfView(Views.NameOfViews nameOfViews) {
        String result = "";
        switch (nameOfViews) {
            case HOME: {
                result = "/views/home.fxml";
                break;
            }
            case LOGIN: {
                result = "/views/login.fxml";
                break;
            }
            case GENERAL_CONTENT: {
                result = "/views/general_content.fxml";
                break;
            }
            case SAVE_PRODUCT: {
                result = "/views/product_save.fxml";
                break;
            }
            case STOCK_LIST: {
                result = "/views/stock_list.fxml";
                break;
            }
            case SAVE_STOCK: {
                result = "/views/stock_save.fxml";
                break;
            }
            case LITTLE_OBJECTS: {
                result = "/views/little_general_objects.fxml";
                break;
            }
        }
        return result;
    }

    private static String getNameOflist(Views.NameOfList nameOfList) {
        String result = "";
        switch (nameOfList) {
            case PRODUCT: {
                result = "product";
                break;
            }
            case STOCK: {
                result = "stock";
                break;
            }
        }
        return result;
    }

    public static ObservableList getListOf(Views.NameOfList nameOfList) {
        String nameList = getNameOflist(nameOfList);
        return LISTS.get(nameList);
    }

    public static void setListOf(Views.NameOfList nameOfList, ObservableList observableList) {
        String nameList = getNameOflist(nameOfList);
        LISTS.put(nameList, observableList);
    }

}
