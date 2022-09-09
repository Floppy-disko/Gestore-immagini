package com.univr.gestoreimmagini.modello;

import com.univr.gestoreimmagini.ImagesApplication;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import javax.imageio.ImageIO;


public class ContenitoreImmagini extends ContenitoreRisorse<Immagine> {

    private Image placedImage = null;

    protected ContenitoreImmagini() {
        loadFromMemory();
    }

    public void addRisorsa(Image immagine, String nome){  //così posso creare un Tag usando solo la stringa del nome
        addRisorsa(new Immagine(immagine, nome));
    }

    @Override
    protected void addToMemory(String nome) {
        URL url = ContenitoreImmagini.class.getResource("");  //la slash da usare è "/"
        String path = url.getPath() + "/immagini";

        File folder = new File(path);
        if(!folder.exists()){
            folder.mkdir();
        }

        path = path + "/" + nome + ".jpg";

        BufferedImage convertedImage = SwingFXUtils.fromFXImage(placedImage, null);
        try {
            ImageIO.write(convertedImage, "jpg", new File(path));  //Image di javafx non sappiamo come scriverla
        } catch(Exception e) {
            System.err.println("Error saving placedImage as file");
        }
    }

    @Override
    protected void removeFromMemory() {

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

    public boolean isPlacedImageSet() {
        return placedImage!=null;
    }
}
