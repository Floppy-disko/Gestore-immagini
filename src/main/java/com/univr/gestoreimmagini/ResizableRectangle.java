package com.univr.gestoreimmagini;


import com.univr.gestoreimmagini.modello.Annotazione;
import com.univr.gestoreimmagini.modello.ImmagineAnnotata;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
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
    private double ratio;

    public ResizableRectangle(Annotazione annotazione, double ratio){
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

        this.ratio=ratio;

        numberLabel.textProperty().bind(Bindings.createStringBinding(()-> String.valueOf(annotazione.getIndexInList())));

        centerCircle.layoutXProperty().bind(annotazione.xProperty().multiply(ratio));
        centerCircle.layoutYProperty().bind(annotazione.yProperty().multiply(ratio));
        cornerCircle.layoutXProperty().bind(Bindings.createDoubleBinding(()->{  //mantengo il cerchio in basso a sinistra
            return (annotazione.getX()- annotazione.getWidth()/2)*ratio;
        }, annotazione.xProperty(), annotazione.widthProperty()));
        cornerCircle.layoutYProperty().bind(Bindings.createDoubleBinding(()->{
            return (annotazione.getY()- annotazione.getHeight()/2)*ratio;
        }, annotazione.yProperty(), annotazione.heightProperty()));

        rectangle.layoutXProperty().bind(cornerCircle.layoutXProperty());
        rectangle.layoutYProperty().bind(cornerCircle.layoutYProperty());

        rectangle.widthProperty().bind(annotazione.widthProperty().multiply(ratio));
        rectangle.heightProperty().bind(annotazione.heightProperty().multiply(ratio));

        numberLabel.layoutXProperty().bind(rectangle.layoutXProperty().add(rectangle.widthProperty()));
        numberLabel.layoutYProperty().bind(rectangle.layoutYProperty());

        centerCircle.setOnMouseDragged(this::modifyCenter);
        cornerCircle.setOnMouseDragged(this::modifySize);
    }
    private void modifyCenter(MouseEvent mouseEvent){
        double newX = annotazione.getX() + mouseEvent.getX()*ratio;
        double newY = annotazione.getY() + mouseEvent.getY()*ratio;
        double maxX = newX+annotazione.getWidth()/2;
        double minX = newX-annotazione.getWidth()/2;
        double maxY = newY+annotazione.getHeight()/2;
        double minY = newY-annotazione.getHeight()/2;

        if(maxX < annotazione.getImage().getWidth() && minX>0)  //Controllo di non anadare oltre ai limiti dell'immagine
            annotazione.setX(newX);
        if(maxY < annotazione.getImage().getHeight() && minY>0)
            annotazione.setY(newY);
    }

    private void modifySize(MouseEvent mouseEvent){

        double newWidth = annotazione.getWidth() - mouseEvent.getX()*ratio;
        double newHeight = annotazione.getHeight() - mouseEvent.getY()*ratio;

        double maxX = annotazione.getX()+newWidth/2;
        double minX = annotazione.getX()-newWidth/2;
        double maxY = annotazione.getY()+newHeight/2;
        double minY = annotazione.getY()-newHeight/2;

        if(newWidth>0 && maxX < annotazione.getImage().getWidth() && minX>0)  //controllo di non sbordare e di non restringerlo fino a 0
            annotazione.setWidth(newWidth);
        if(newHeight>0 && maxY < annotazione.getImage().getWidth() && minY>0)
            annotazione.setHeight(newHeight);
    }
}
