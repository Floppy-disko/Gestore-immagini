package com.univr.gestoreimmagini;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Translate;

import java.io.IOException;

public class ResizableRectangle extends Group {

    @FXML
    private Rectangle rectangle;
    @FXML
    private Circle centerCircle;
    @FXML
    private Circle cornerCircle;
    @FXML
    private Label numberLabel;

    public ResizableRectangle(){
        super();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ResizableRectangle.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

    }
}
