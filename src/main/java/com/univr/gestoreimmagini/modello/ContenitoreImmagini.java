package com.univr.gestoreimmagini.modello;

import com.univr.gestoreimmagini.ImagesApplication;
import javafx.scene.image.Image;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.nio.file.Paths;
import javax.imageio.ImageIO;

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
        URL path = ContenitoreImmagini.class.getResource("test.txt");  //la slash da usare è "/"
        //ImageIO.write(placedImage, "jpg", new File(path.toString()));  //Image di javafx non sappiamo come scriverla
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
