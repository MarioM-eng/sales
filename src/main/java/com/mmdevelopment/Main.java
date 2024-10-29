package com.mmdevelopment;

import com.mmdevelopment.events.CustomAlert;
import com.mmdevelopment.viewHandler.Views;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import com.mmdevelopment.databaselogic.JPAUtil;
import com.mmdevelopment.databaselogic.seeder.Seeder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
@Slf4j
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Establecer la escena en la ventana principal (Stage)
        Views views = Views.getInstance();
        views.setCurrentStage(primaryStage);
        try {
            views.buildWindow(Views.NameOfViews.LOGIN, "Inicio de sesión");
            views.getCurrentStage().setResizable(false);
            views.getCurrentStage().setFullScreen(false);
        } catch (IOException e) {
            log.error(String.valueOf(e));
            CustomAlert.showAlert("Ocurrió un error tratando de abrir: Inicio de sesión", CustomAlert.ERROR);
        }
        views.getCurrentStage().show();
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