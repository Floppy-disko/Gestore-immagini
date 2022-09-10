package com.univr.gestoreimmagini.modello;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import javax.imageio.ImageIO;
import org.apache.commons.io.FilenameUtils;


public class ContenitoreImmaginiAnnotate extends ContenitoreRisorse<ImmagineAnnotata> {

    private File cartellaImmagini;

    protected ContenitoreImmaginiAnnotate() {
        super();
        URL url = ContenitoreImmaginiAnnotate.class.getResource("");  //la slash da usare è "/"
        String path = url.getPath() + "/immagini";
        cartellaImmagini = new File(path);

        if(!cartellaImmagini.exists()){
            cartellaImmagini.mkdir();
        }

        loadFromMemory();
    }

    public void addRisorsa(Image immagine, String nome){  //così posso creare un Tag usando solo la stringa del nome
        addRisorsa(new ImmagineAnnotata(immagine, nome));
    }

    @Override
    protected void addToMemory(ImmagineAnnotata r) {

        String imagePath = cartellaImmagini.getPath() + "/" + r.toString() + ".jpg";

        BufferedImage convertedImage = SwingFXUtils.fromFXImage(r.getImmagine(), null);
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

            try(FileInputStream stream = new FileInputStream(imageFile)) {
                immagine = new Image(stream);
            } catch (IOException e) {
                System.err.printf("\nCouldn't read from %s\n", imageFile.getPath());
                throw new RuntimeException();
            }

            getNomiRisorse().add(nome);
            getRisorse().add(new ImmagineAnnotata(immagine, nome));
        }
    }

    public void addImage(Image immagine, String nome) {
        addRisorsa(immagine, nome);
    }
}
