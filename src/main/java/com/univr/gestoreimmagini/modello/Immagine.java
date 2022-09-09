package com.univr.gestoreimmagini.modello;

import javafx.scene.image.Image;

public class Immagine {

    private Image immagine;
    private String nome;

    public Immagine(Image immagine, String nome) {
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
