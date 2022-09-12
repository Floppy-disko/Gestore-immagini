package com.univr.gestoreimmagini.modello;

import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import org.apache.commons.io.FilenameUtils;

public class ImmagineAnnotata {

    private Image immagine;
    private String nome;

    private String extension;

    private ObservableList<Annotazione> annotazioni;

    public ImmagineAnnotata(Image immagine, String nome, String extension) {
        this.extension = extension;
        this.immagine = immagine;
        this.nome = nome;
    }

    public Image getImmagine(){
        return this.immagine;
    }

    public String toString(){
        return this.nome;
    }

    public String getExtension() {
        return extension;
    }
}
