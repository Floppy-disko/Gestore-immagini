package com.univr.gestoreimmagini;


import com.univr.gestoreimmagini.modello.Annotazione;
import com.univr.gestoreimmagini.modello.ImmagineAnnotata;
import javafx.beans.binding.Bindings;
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

    private Annotazione annotazione;

    private ImmagineAnnotata immagineAnnotata;

    public ResizableRectangle(Annotazione annotazione, ImmagineAnnotata immagineAnnotata){
        super();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ResizableRectangle.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        this.annotazione = annotazione;
        this.immagineAnnotata = immagineAnnotata;

        //numberLabel.textProperty().bind(Bindings.createStringBinding(()-> ))
    }
}
