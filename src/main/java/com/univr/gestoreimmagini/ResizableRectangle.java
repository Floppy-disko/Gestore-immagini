package com.univr.gestoreimmagini;


import com.univr.gestoreimmagini.modello.Annotation;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.awt.dnd.DragSourceListener;
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

    private Annotation annotation;
    private double ratio;

    public ResizableRectangle(Annotation annotation, double ratio){
        super();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ResizableRectangle.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        this.annotation = annotation;

        this.ratio=ratio;

        numberLabel.textProperty().bind(Bindings.createStringBinding(()-> String.valueOf(annotation.getNumber()), annotation.numberProperty()));

        centerCircle.layoutXProperty().bind(annotation.xProperty().multiply(ratio));
        centerCircle.layoutYProperty().bind(annotation.yProperty().multiply(ratio));
        cornerCircle.layoutXProperty().bind(Bindings.createDoubleBinding(()->{  //mantengo il cerchio in basso a sinistra
            return (annotation.getX()- annotation.getWidth()/2)*ratio;
        }, annotation.xProperty(), annotation.widthProperty()));
        cornerCircle.layoutYProperty().bind(Bindings.createDoubleBinding(()->{
            return (annotation.getY()- annotation.getHeight()/2)*ratio;
        }, annotation.yProperty(), annotation.heightProperty()));

        rectangle.layoutXProperty().bind(cornerCircle.layoutXProperty());
        rectangle.layoutYProperty().bind(cornerCircle.layoutYProperty());

        rectangle.widthProperty().bind(annotation.widthProperty().multiply(ratio));
        rectangle.heightProperty().bind(annotation.heightProperty().multiply(ratio));

        numberLabel.layoutXProperty().bind(rectangle.layoutXProperty().add(rectangle.widthProperty()));
        numberLabel.layoutYProperty().bind(rectangle.layoutYProperty());

        centerCircle.setOnMouseDragged(this::modifyCenter);
        cornerCircle.setOnMouseDragged(this::modifySize);
    }
    private void modifyCenter(MouseEvent mouseEvent){
        double newX = annotation.getX() + mouseEvent.getX()*ratio;
        double newY = annotation.getY() + mouseEvent.getY()*ratio;
        double maxX = newX+ annotation.getWidth()/2;
        double minX = newX- annotation.getWidth()/2;
        double maxY = newY+ annotation.getHeight()/2;
        double minY = newY- annotation.getHeight()/2;

        if(maxX < annotation.getImage().getWidth() && minX>0)  //Controllo di non anadare oltre ai limiti dell'immagine
            annotation.setX(newX);
        if(maxY < annotation.getImage().getHeight() && minY>0)
            annotation.setY(newY);

    }

    private void modifySize(MouseEvent mouseEvent){

        double newWidth = annotation.getWidth() - mouseEvent.getX()*ratio;
        double newHeight = annotation.getHeight() - mouseEvent.getY()*ratio;

        double maxX = annotation.getX()+newWidth/2;
        double minX = annotation.getX()-newWidth/2;
        double maxY = annotation.getY()+newHeight/2;
        double minY = annotation.getY()-newHeight/2;

        if(newWidth>0 && maxX < annotation.getImage().getWidth() && minX>0)  //controllo di non sbordare e di non restringerlo fino a 0
            annotation.setWidth(newWidth);
        if(newHeight>0 && maxY < annotation.getImage().getHeight() && minY>0)
            annotation.setHeight(newHeight);
    }
}
