package com.mmdevelopment.viewHandler;

import com.mmdevelopment.events.Events;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class Views {

    public static enum NameOfViews {

        LOGIN, HOME

    }

    public static Scene getScene(Views.NameOfViews nameOfViews) {
        FXMLLoader loader = new FXMLLoader(Events.class.getResource(getNameOfView(nameOfViews)));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            log.error("Ocurrió un error tratando de iniciar contenido de ventana");
        }

        return new Scene(root);
    }

    public static Stage getStage(Scene scene, String title) {
        Stage stage = new Stage();

        stage.setScene(scene);
        stage.setTitle(title);
        return  stage;
    }

    public static void closeNodeWindow(Node node) {
        ((Stage) node.getScene().getWindow()).close();
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
        }
        return result;
    }

}
