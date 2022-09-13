package com.univr.gestoreimmagini.modello;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import javax.imageio.ImageIO;
import org.apache.commons.io.FilenameUtils;


public class ContenitoreImmaginiAnnotate extends ContenitoreRisorse<ImmagineAnnotata> {

    protected ContenitoreImmaginiAnnotate() {
        super();
    }

    public void addRisorsa(Image immagine, String nome, String extension){  //così posso creare un Tag usando solo la stringa del nome
        addRisorsa(new ImmagineAnnotata(immagine, nome, extension));
    }

    @Override
    protected void addToMemory(ImmagineAnnotata r) {

        String imagePath = resourcesDir.getPath() + "/" + r.toString() + "." + r.getExtension();

        BufferedImage convertedImage = SwingFXUtils.fromFXImage(r.getImmagine(), null);
        try {
            ImageIO.write(convertedImage, r.getExtension(), new File(imagePath));  //Image di javafx non sappiamo come scriverla
        } catch(Exception e) {
            System.err.printf("\nError saving %s as file\n", imagePath);
        }

        updateMemory();
    }

    @Override
    protected void removeFromMemory(String nome) {

        File imageFile = new File(resourcesDir.getPath() + "/" + nome + "." + getRisorsa(nome).getExtension());

        imageFile.delete();

        updateMemory();
    }

    @Override
    protected void loadFromMemory(){

        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File f, String name) {
                return name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".JPG") || name.endsWith(".PNG");
            }
        };

        for(File imageFile: resourcesDir.listFiles(filter)){

            String nome =  FilenameUtils.getBaseName(imageFile.getPath());
            String extension = FilenameUtils.getExtension(imageFile.getPath());

            Image immagine = null;

            try(FileInputStream stream = new FileInputStream(imageFile.getPath())) {
                immagine = new Image(stream);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            getRisorse().add(new ImmagineAnnotata(immagine, nome, extension));
            getNomiRisorse().add(nome);
        }
    }

}
