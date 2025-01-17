package com.mmdevelopment.viewHandler;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class Views {

    private static final Views singleton = new Views();
    private static final Map<String, ObservableList> LISTS = new HashMap();
    @Getter @Setter
    private Stage currentStage;
    private Stage previousStage;

    public enum NameOfViews {
        LOGIN, HOME, GENERAL_CONTENT, SAVE_PRODUCT
    }

    public enum NameOfList {
        PRODUCT
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
