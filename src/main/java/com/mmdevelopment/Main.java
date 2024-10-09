package com.mmdevelopment;

import com.mmdevelopment.viewHandler.Views;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.mmdevelopment.databaselogic.JPAUtil;
import com.mmdevelopment.databaselogic.seeder.Seeder;
import lombok.extern.slf4j.Slf4j;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
@Slf4j
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Establecer la escena en la ventana principal (Stage)
        primaryStage.setScene(Views.getScene(Views.NameOfViews.LOGIN));
        primaryStage.setTitle("Iniciar sesión");
        primaryStage.show();
    }

    public static void main(String[] args) {
        log.info("{}", Config.runSeeders());
        if (Config.runSeeders()){
            Seeder.seed();
        }
        launch(args);
    }

    @Override
    public void stop() throws Exception {
        JPAUtil.shutdown();
        super.stop();
    }
}