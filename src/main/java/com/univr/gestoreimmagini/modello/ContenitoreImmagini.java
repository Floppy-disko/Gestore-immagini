package com.univr.gestoreimmagini.modello;

import javafx.scene.image.Image;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;

public class ContenitoreImmagini extends ContenitoreRisorse<Immagine> {

    Image placedImage;

    protected ContenitoreImmagini() {
        loadFromMemory();
    }

    public void addRisorsa(Image immagine, String nome){  //così posso creare un Tag usando solo la stringa del nome
        addRisorsa(new Immagine(immagine, nome));
    }
    @Override
    protected void updateMemory() {
        System.out.println(Paths.get(this.getClass().getResource("/").getPath()));
    }

    @Override
    protected void loadFromMemory(){

    }

    public void setPlacedImage(Image image) {
        placedImage = image;
    }

    public void addPlacedImage(String nome) {
        addRisorsa(placedImage, nome);
    }
}
