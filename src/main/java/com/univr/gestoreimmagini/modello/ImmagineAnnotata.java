package com.univr.gestoreimmagini.modello;

import javafx.collections.ObservableList;
import javafx.scene.image.Image;

public class ImmagineAnnotata {

    private Image immagine;
    private String nome;

    private ObservableList<Annotazione> annotazioni;

    public ImmagineAnnotata(Image immagine, String nome) {
        this.immagine = immagine;
        this.nome = nome;
    }

    public Image getImmagine(){
        return this.immagine;
    }

    public String toString(){
        return this.nome;
    }
}
