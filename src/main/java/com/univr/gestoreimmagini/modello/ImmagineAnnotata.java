package com.univr.gestoreimmagini.modello;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import org.apache.commons.io.FilenameUtils;

public class ImmagineAnnotata {

    private SimpleObjectProperty<Image> image = new SimpleObjectProperty<>(this, "image");
    private String name;

    private String extension;

    //private ObservableList<Annotazione> annotazioni;
    private ObservableList<Annotazione> annotazioni = FXCollections.observableArrayList();

    public ImmagineAnnotata(Image image, String name, String extension) {
        setImage(image);
        this.name = name;
        this.extension = extension;
    }

    public SimpleObjectProperty<Image> imageProperty() {
        return image;
    }

    public void setImage(Image image) {
        this.image.set(image);
    }

    public Image getImage(){
        return this.image.get();
    }

    public String toString(){
        return this.name + "." + this.extension;
    }

    public String getName(){
        return this.name;
    }

    public String getExtension() {
        return extension;
    }

    public ObservableList<Annotazione> getAnnotazioni() {
        return annotazioni;
    }

    public void setAnnotazioni(ObservableList<Annotazione> annotazioni) {
        this.annotazioni = annotazioni;
    }
}
