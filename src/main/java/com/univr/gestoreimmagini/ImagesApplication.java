package com.univr.gestoreimmagini;

import com.univr.gestoreimmagini.modello.Model;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ImagesApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ImagesApplication.class.getResource("ImageManagerView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        stage.setTitle("Image Manager");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop(){
        Model.getModel().updateMemory();
    }

    public static void main(String[] args) {
        launch();
    }
}