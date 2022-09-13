package com.univr.gestoreimmagini.modello;

import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import org.apache.commons.io.FilenameUtils;

public class ImmagineAnnotata {

    private Image image;
    private String name;

    private String extension;

    private ObservableList<Annotazione> annotazioni;

    public ImmagineAnnotata(Image image, String name, String extension) {
        this.image = image;
        this.name = name;
        this.extension = extension;
    }

    public Image getImage(){
        return this.image;
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
}
