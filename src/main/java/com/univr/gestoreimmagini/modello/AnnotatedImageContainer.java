package com.univr.gestoreimmagini.modello;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import org.apache.commons.io.FilenameUtils;


public class AnnotatedImageContainer extends ResourcesContainer<AnnotatedImage> {

    private String annotationsDir = getClass().getResource("").getPath() + "/annotations";
    protected AnnotatedImageContainer() {
        super();
    }

    public void addRisorsa(Image immagine, String nome, String extension){      // Così è possibile creare un Tag usando solo la stringa del nome
        addRisorsa(new AnnotatedImage(immagine, nome, extension));
    }

    public boolean resourceFileExists(int index){
        File resourceFile = new File(resourcesDir + "/" + getNomiRisorse().get(index));
        return resourceFile.exists();
    }

    public void restoreImage(int index){
        addToMemory(getRisorse().get(index));
    }

    @Override
    protected void addToMemory(AnnotatedImage r) {

        String imagePath = resourcesDir.getPath() + "/" + r.toString();

        BufferedImage convertedImage = SwingFXUtils.fromFXImage(r.getImage(), null);
        try {
            ImageIO.write(convertedImage, r.getExtension(), new File(imagePath));       // Image di JavaFX non sappiamo come scriverla
        } catch(Exception e) {
            System.err.printf("\nError saving %s as file\n", imagePath);
        }

        updateMemory();
    }

    @Override
    protected void removeFromMemory(String fullName) {

        File imageFile = new File(resourcesDir.getPath() + "/" + fullName);
        File annotationsFile = new File(annotationsDir + "/" + FilenameUtils.getBaseName(fullName) + ".dat");

        getRisorse().remove(fullName);      // Rimuovo l'immagine dagli oggetti prima di salvare il nuovo stato

        imageFile.delete();
        annotationsFile.delete();

        updateMemory();
    }

    @Override
    protected void loadFromMemory(){

        super.loadFromMemory();

        boolean namesFileConsistent = true;

        // Per ogni nome letto
        for(String fileFullName: getNomiRisorse()){

            String name =  FilenameUtils.getBaseName(fileFullName);             // Preleva il nome
            String extension = FilenameUtils.getExtension(fileFullName);        // Preleva l'estensione

            String imagePath = resourcesDir.getPath() + "/" + fileFullName;     // Costruisce il path

            Image immagine;

            try(FileInputStream stream = new FileInputStream(imagePath)) {
                immagine = new Image(stream);
                getRisorse().add(new AnnotatedImage(immagine, name, extension));        // Devo aggiornare le liste senza chiamare addRisorsa visto che essa chiama addToMemory
            } catch (FileNotFoundException e) {
                System.err.printf("\nImage %s not found\n", fileFullName);
                namesFileConsistent = false;                                            // Non si è trovato un file riportato in namesFile

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if(namesFileConsistent == false){
            getNomiRisorse().clear();       // Si sovrascrive la lista ausiliaria con quella principale perchè non c'è consistenza tra questa e la lista principale
            for(AnnotatedImage annotatedImage : getRisorse()){
                getNomiRisorse().add(annotatedImage.toString());
            }

            updateMemory();
        }

    }

    @Override
    public boolean nomeInLista(String nome) {
        for(String nomeRisorsa: getNomiRisorse()){                      // Cerca se c'è una risorsa con lo stesso nome
            if(nome.equals(FilenameUtils.getBaseName(nomeRisorsa)))     // Controllo se il nome è uguale togliendo l'estensione
                return true;
        }

        return false;
    }

    @Override
    protected void updateMemory(){
        super.updateMemory();
        for(AnnotatedImage ia: getRisorse())
            ia.updateMemory();
    }
}
