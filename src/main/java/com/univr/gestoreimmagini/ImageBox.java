package com.univr.gestoreimmagini;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class ImageBox extends VBox {
    @FXML
    private Label nameLabel;
    @FXML
    private Button removeButton;
    @FXML
    private ImageView imageView;

    public ImageBox() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("imageBox.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

    }

    public void setNameLabelText(String text){
        nameLabel.setText(text);
    }

    public String getNameLabelText() {
        return nameLabel.getText();
    }

    public void setRemoveButtonOnAction(EventHandler<ActionEvent> handler){
        removeButton.setOnAction(handler);
    }

    public void setDisplayedImage(Image image){
        imageView.setImage(image);
    }

    public void setImageOnClick(EventHandler<MouseEvent> handler){
        imageView.setOnMouseClicked(handler);
    }
}
