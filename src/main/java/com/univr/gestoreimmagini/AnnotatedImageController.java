package com.univr.gestoreimmagini;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class AnnotatedImageController extends StackPane {

    @FXML
    private ImageView zoomImage;

    public AnnotatedImageController(){
        super();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AnnotatedImageView.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
