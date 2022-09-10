package com.univr.gestoreimmagini.modello;

import com.univr.gestoreimmagini.modello.ContenitoreRisorse;
import com.univr.gestoreimmagini.modello.Tag;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;

public class ContenitoreTag extends ContenitoreRisorse<Tag> {

    private File tagFile;

    protected ContenitoreTag() {
        super();

        URL url = ContenitoreTag.class.getResource("");
        String path = url.getPath() + "/tags";  //path della cartella tagss
        File tagDir = new File(path);

        if(!tagDir.exists()) { //se la cartella non esiste lo creo
                tagDir.mkdir();
        }

        path = path + "/tags.txt";  //path del file su cui salvare il tag
        tagFile = new File(path);

        if(!tagFile.exists()) { //se il file non esiste lo creo
            try {
                tagFile.createNewFile();
            } catch (IOException e) {
                System.err.println("Impossibile creare file tag");
            }

        }

        if(tagFile.length()>0)  //controllo che il file non sia vuoto e se non lo è provo a caricare la lista di tag
            loadFromMemory();

    }

    public void addRisorsa(String nome){  //così posso creare un Tag usando solo la stringa del nome
        addRisorsa(new Tag(nome));
    }

    @Override
    protected void addToMemory(Tag r) {
        updateMemory();
    }

    @Override
    protected void removeFromMemory(String nome) {
        updateMemory(); //aggiungere e cancellare dalla memoria significa sovrascrivere tags.txt
    }

    /**
     * Sovrascrive tags.txt con la lista ausiliaria contenente i nomi dei tags
     */
    protected void updateMemory(){
        try{
            FileOutputStream fos = new FileOutputStream(tagFile);

            ObjectOutputStream oos = new ObjectOutputStream(fos);

            try{
                oos.writeObject(getNomiRisorse());  //salvo questo oggetto in memoria con tutta la lista di tag
            } finally {
                oos.flush();
                oos.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void loadFromMemory(){

        try{

            FileInputStream fis = new FileInputStream(tagFile);

            ObjectInputStream ois = new ObjectInputStream(fis);

            try {
                getNomiRisorse().addAll((ArrayList<String>) ois.readObject()); //aggiungo gli elementi copiati alla lista ausiliaria
            } finally {
                ois.close();
            }

            for(String nome: getNomiRisorse())  //creo un tag nella lista principale per ogni nome letto da memoria
                getRisorse().add(new Tag(nome));


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            try {
                new PrintWriter(tagFile).close(); //Se c'è un eccezzione che non è IO o ClassNotFound prbabimente il file è illeggibile quindi pulisci il file

            } catch(FileNotFoundException e2) {
                System.err.printf("File %s non trovato", tagFile);
            }
        }

    }
}
