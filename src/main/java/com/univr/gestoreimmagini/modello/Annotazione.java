package com.univr.gestoreimmagini.modello;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class Annotazione {

    private Model modello = Model.getModel();

    private Tag tag;
    private SimpleStringProperty valore = new SimpleStringProperty();

    private SimpleDoubleProperty center = new SimpleDoubleProperty();

    private SimpleDoubleProperty width = new SimpleDoubleProperty();

    private SimpleDoubleProperty height = new SimpleDoubleProperty();

    public String getValore() {
        return valore.get();
    }

    public SimpleStringProperty valoreProperty() {
        return valore;
    }

    public void setValore(String valore) {
        this.valore.set(valore);
    }

    public double getCenter() {
        return center.get();
    }

    public SimpleDoubleProperty centerProperty() {
        return center;
    }

    public void setCenter(double center) {
        this.center.set(center);
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
