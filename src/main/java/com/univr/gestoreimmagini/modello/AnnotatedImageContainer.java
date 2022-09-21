package com.univr.gestoreimmagini.modello;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import org.apache.commons.io.FilenameUtils;


public class AnnotatedImageContainer extends ResourcesContainer<ImmagineAnnotata> {

    protected AnnotatedImageContainer() {
        super();
        String path = getClass().getResource("").getPath() + "/annotations";
    }

    public void addRisorsa(Image immagine, String nome, String extension){  //così posso creare un Tag usando solo la stringa del nome
        addRisorsa(new ImmagineAnnotata(this, immagine, nome, extension));
    }

    public boolean resourceFileExists(int index){
        File resourceFile = new File(resourcesDir + "/" + getNomiRisorse().get(index));
        return resourceFile.exists();
    }

    public void restoreImage(int index){
        addToMemory(getRisorse().get(index));
    }

    @Override
    protected void addToMemory(ImmagineAnnotata r) {

        String imagePath = resourcesDir.getPath() + "/" + r.toString();

        BufferedImage convertedImage = SwingFXUtils.fromFXImage(r.getImage(), null);
        try {
            ImageIO.write(convertedImage, r.getExtension(), new File(imagePath));  //Image di javafx non sappiamo come scriverla
        } catch(Exception e) {
            System.err.printf("\nError saving %s as file\n", imagePath);
        }

        updateMemory();
    }

    @Override
    protected void removeFromMemory(String fullName) {

        File imageFile = new File(resourcesDir.getPath() + "/" + fullName);

        getRisorse().remove(fullName);  //rimuovo l'immagine dagli oggetti prima di salvare il nuovo stato

        imageFile.delete();

        updateMemory();
    }

    @Override
    protected void loadFromMemory(){

        super.loadFromMemory();

        boolean namesFileConsistent = true;

        for(String fileFullName: getNomiRisorse()){

            String name =  FilenameUtils.getBaseName(fileFullName);
            String extension = FilenameUtils.getExtension(fileFullName);

            String imagePath = resourcesDir.getPath() + "/" + fileFullName;

            Image immagine = null;

            try(FileInputStream stream = new FileInputStream(imagePath)) {
                immagine = new Image(stream);
                getRisorse().add(new ImmagineAnnotata(this, immagine, name, extension)); //devo aggiornare le liste senza chiamare addRisorsa visto che essa chiama addToMemory
            } catch (FileNotFoundException e) {
                System.err.printf("\nImage %s not found\n", fileFullName);
                namesFileConsistent = false; //Non ho trovato un file riportato in namesFile

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if(namesFileConsistent==false){
            getNomiRisorse().clear(); //Sovrascrivo la lista ausiliaria con quella principale perchè non c'è consistenza tra questa e la lista principale
            for(ImmagineAnnotata immagineAnnotata: getRisorse()){
                getNomiRisorse().add(immagineAnnotata.toString());
            }

            updateMemory();
        }

    }

    @Override
    public boolean nomeInLista(String nome) {
        for(String nomeRisorsa : getNomiRisorse()){   //cerca se c'è una risorsa con lo stesso nome
            if(nome.equals(FilenameUtils.getBaseName(nomeRisorsa)))  //Controllo se il nome è uguale togliendo l'estensione
                return true;
        }

        return false;
    }

    @Override
    protected void updateMemory(){
        super.updateMemory();
        for(ImmagineAnnotata ia: getRisorse())
            ia.updateMemory();
    }
}
