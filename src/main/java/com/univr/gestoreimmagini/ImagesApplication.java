package com.univr.gestoreimmagini;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ImagesApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ImagesApplication.class.getResource("ImageManagerView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1020, 730);
        stage.setTitle("Image Manager");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}