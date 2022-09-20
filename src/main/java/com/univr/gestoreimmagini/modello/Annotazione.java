package com.univr.gestoreimmagini.modello;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

public class Annotazione {

    private Model modello = Model.getModel();

    private Tag tag;
    private SimpleStringProperty valore = new SimpleStringProperty();

    private SimpleDoubleProperty X = new SimpleDoubleProperty();

    private SimpleDoubleProperty Y = new SimpleDoubleProperty();

    private SimpleDoubleProperty width = new SimpleDoubleProperty();

    private SimpleDoubleProperty height = new SimpleDoubleProperty();

    private ImmagineAnnotata immagineAnnotata;

    public Annotazione(ImmagineAnnotata immagineAnnotata, double X, double Y, double width, double height){
        this.immagineAnnotata = immagineAnnotata;   //mantengo un riferimento a chi annoto
        this.X.set(X);
        this.Y.set(Y);
        this.width.set(width);
        this.height.set(height);
    }

    public int getIndexInList(){
        return immagineAnnotata.getAnnotazioni().indexOf(this);
    }

    public String getValore() {
        return valore.get();
    }

    public SimpleStringProperty valoreProperty() {
        return valore;
    }

    public void setValore(String valore) {
        this.valore.set(valore);
    }

    public double getX() {
        return X.get();
    }

    public SimpleDoubleProperty xProperty() {
        return X;
    }

    public void setX(double x) {
        this.X.set(x);
    }

    public double getY() {
        return Y.get();
    }

    public SimpleDoubleProperty yProperty() {
        return Y;
    }

    public void setY(double y) {
        this.Y.set(y);
    }

    public double getWidth() {
        return width.get();
    }

    public SimpleDoubleProperty widthProperty() {
        return width;
    }

    public void setWidth(double width) {
        this.width.set(width);
    }

    public double getHeight() {
        return height.get();
    }

    public SimpleDoubleProperty heightProperty() {
        return height;
    }

    public void setHeight(double height) {
        this.height.set(height);
    }
}
