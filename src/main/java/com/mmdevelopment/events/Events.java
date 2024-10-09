package com.mmdevelopment.events;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class Events {

    private Events(){

    }

    public static EventHandler<ActionEvent> goToSchools(){
        return event ->  {
            // Código para manejar el evento aquí
            log.info("Botón clicado");
            log.info("Todo bien");
        };
    }

    public static EventHandler<ActionEvent> gotToHome(){
        return event ->  {
            FXMLLoader loader = new FXMLLoader(Events.class.getResource("/views/home.fxml"));
            Parent root = null;
            try {
                root = loader.load();
            } catch (IOException e) {
                log.error("Ocurrió un error tratando de iniciar contenido de ventana");
            }

            Scene scene = new Scene(root);

            Stage stage = new Stage();

            stage.setResizable(false);
            stage.setScene(scene);
            stage.setTitle("");
            stage.show();
            //Cerramos el stage anterior
            Events.closeNodeWindow(((Node) event.getSource()));
        };
    }

    public static void closeNodeWindow(Node node) {
        ((Stage) node.getScene().getWindow()).close();
    }

}
