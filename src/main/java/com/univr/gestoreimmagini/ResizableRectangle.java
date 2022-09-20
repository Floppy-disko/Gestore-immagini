package com.univr.gestoreimmagini;


import com.univr.gestoreimmagini.modello.Annotazione;
import com.univr.gestoreimmagini.modello.ImmagineAnnotata;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
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

    public ResizableRectangle(Annotazione annotazione){
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

        numberLabel.textProperty().bind(Bindings.createStringBinding(()-> String.valueOf(annotazione.getIndexInList())));

        centerCircle.centerXProperty().bind(annotazione.xProperty());
        centerCircle.centerYProperty().bind(annotazione.yProperty());
        cornerCircle.centerXProperty().bind(Bindings.createDoubleBinding(()->{  //mantengo il cerchio in basso a sinistra
            return annotazione.getX()- annotazione.getWidth()/2;
        }));
        cornerCircle.centerYProperty().bind(Bindings.createDoubleBinding(()->{
            return annotazione.getY()- annotazione.getHeight()/2;
        }));
    }
}
