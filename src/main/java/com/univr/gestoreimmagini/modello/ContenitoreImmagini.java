package com.univr.gestoreimmagini.modello;

import com.univr.gestoreimmagini.ImagesApplication;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.imageio.ImageIO;
import org.apache.commons.io.FilenameUtils;


public class ContenitoreImmagini extends ContenitoreRisorse<Immagine> {

    private Image placedImage;
    private File cartellaImmagini;

    protected ContenitoreImmagini() {
        super();
        URL url = ContenitoreImmagini.class.getResource("");  //la slash da usare è "/"
        String path = url.getPath() + "/immagini";
        cartellaImmagini = new File(path);

        if(!cartellaImmagini.exists()){
            cartellaImmagini.mkdir();
        }

        loadFromMemory();
    }

    public void addRisorsa(Image immagine, String nome){  //così posso creare un Tag usando solo la stringa del nome
        addRisorsa(new Immagine(immagine, nome));
    }

    @Override
    protected void addToMemory(String nome) {

        String imagePath = cartellaImmagini.getPath() + "/" + nome + ".jpg";

        BufferedImage convertedImage = SwingFXUtils.fromFXImage(placedImage, null);
        try {
            ImageIO.write(convertedImage, "jpg", new File(imagePath));  //Image di javafx non sappiamo come scriverla
        } catch(Exception e) {
            System.err.println("Error saving placedImage as file");
        }
    }

    @Override
    protected void removeFromMemory(String nome) {

        File imageFile = new File(cartellaImmagini.getPath() + "/" + nome + ".jpg");

        imageFile.delete();
    }

    @Override
    protected void loadFromMemory(){

        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File f, String name) {
                return name.endsWith(".jpg");
            }
        };

        for(File imageFile: cartellaImmagini.listFiles(filter)){

            Image immagine = null;
            String nome =  FilenameUtils.getBaseName(imageFile.getPath());

            try {
                immagine = new Image(new FileInputStream(imageFile));
            } catch (FileNotFoundException e) {
                System.err.printf("\nCouldn't read from %s\n", imageFile.getPath());
                throw new RuntimeException();
            }

            getRisorse().add(new Immagine(immagine, nome));
        }
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
