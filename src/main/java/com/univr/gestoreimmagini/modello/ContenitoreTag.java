package com.univr.gestoreimmagini.modello;

import com.univr.gestoreimmagini.modello.ContenitoreRisorse;
import com.univr.gestoreimmagini.modello.Tag;

import java.io.*;
import java.util.ArrayList;

public class ContenitoreTag extends ContenitoreRisorse<Tag> {

    private File tagFile = new File("tags.txt");

    protected ContenitoreTag() {
        super();
        if(!tagFile.exists()) { //se il file non esiste lo creo
            try {
                tagFile.createNewFile();
            } catch (IOException e) {
                System.err.println("Impossibile creare file tag");
            }

        }

        if(tagFile.length()>0)  //controllo che il file non sia vuoto e se non lo è provo a caricare la lista di tag
            try{
                loadFromMemory();
            } catch(FileNotFoundException e) {
                System.err.println("File di tag non trovato");
            }
    }

    public void addRisorsa(String nome){  //così posso creare un Tag usando solo la stringa del nome
        addRisorsa(new Tag(nome));
    }

    protected void updateMemory(){
        try{
            FileOutputStream fos = new FileOutputStream(tagFile);

            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(getNomiRisorse());  //salvo questo oggetto in memoria con tutta la lista di tag

            oos.flush();
            oos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void loadFromMemory() throws FileNotFoundException {

        try{

            FileInputStream fis = new FileInputStream(tagFile);

            ObjectInputStream ois = new ObjectInputStream(fis);

            getNomiRisorse().addAll((ArrayList<String>) ois.readObject()); //aggiungo gli elementi copiati alla lista ausiliaria

            ois.close();

            for(String nome: getNomiRisorse())  //creo un tag nella lista principale per ogni nome letto da memoria
                getRisorse().add(new Tag(nome));


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            new PrintWriter(tagFile).close(); //Se c'è un eccezzione che non è IO o ClassNotFound prbabimente il file è illeggibile quindi pulisci il file
        }

    }
}
