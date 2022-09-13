package com.univr.gestoreimmagini.modello;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;

public class ContenitoreTag extends ContenitoreRisorse<Tag> {

    protected ContenitoreTag() {
        super();
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

    @Override
    protected void loadFromMemory(){

        try{

            FileInputStream fis = new FileInputStream(namesFile);

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
                new PrintWriter(namesFile).close(); //Se c'è un eccezzione che non è IO o ClassNotFound prbabimente il file è illeggibile quindi pulisci il file

            } catch(FileNotFoundException e2) {
                System.err.printf("File %s non trovato", namesFile);
            }
        }

    }
}
