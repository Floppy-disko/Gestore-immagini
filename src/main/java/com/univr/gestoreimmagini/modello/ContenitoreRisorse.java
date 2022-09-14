package com.univr.gestoreimmagini.modello;

import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.io.*;
import java.net.URL;
import java.util.*;

public abstract class ContenitoreRisorse<T> implements Serializable {

    private final ObservableList<T> risorse = FXCollections.observableArrayList();

    //lista ausiliaria che tiene solo i nomi, utile perchè serializzabile e più rapida per la ricerca di duplicati tra i tag
    private final ArrayList<String> nomiRisorse = new ArrayList<>();  //ATTENZIONE: ogni volta in cui modifichi una delle due liste devi modificare anche l'altra per evitare inconsistenze

    protected File namesFile;
    protected File resourcesDir;

    protected ContenitoreRisorse(){

    }

    public T getRisorsa(int index){
        return risorse.get(index);
    }

    /**
     * Cerca la risorsa per nome
     * @param nome
     * @return La risorsa se la trova se no ritorna null
     */
    public T getRisorsa(String nome){
        for(T r: risorse){
            if(r.toString().equals(nome)){
                return r;
            }
        }

        return null;
    }

    public int getSize(){
        return nomiRisorse.size();
    }

    public void addRisorsa(T r){

        nomiRisorse.add(r.toString()); //aggiungo nome a lista ausiliaria
        risorse.add(r); //aggiungo tag con quel nome alla lista principale
        addToMemory(r);
    }

    /**
     * Cerca per nome se una risorsa è contenuta e se la trova la elimina
     * @param nome
     * @return 1 se trova la risorsa con quel nome, 0 se non la trova
     */
    public int removeRisorsa(String nome){

        T r = getRisorsa(nome);
        if(r!=null){
            removeRisorsa(r);
            return 1;
        }

        return 0;
    }
    public void removeRisorsa(T r){
        nomiRisorse.remove(r.toString());  //rimuovi l'entry allo stesso index di r
        risorse.remove(r);
        removeFromMemory(r.toString());
    }

    public ObservableList<T> getRisorse(){
        return risorse;
    }
    public ArrayList<String> getNomiRisorse() {
        return nomiRisorse;
    }

    public int getIndex(String nome){
        return nomiRisorse.indexOf(nome);
    }

    public boolean nomeInLista(String nome){
        for(String nomeRisorsa : nomiRisorse){   //cerca se c'è una risorsa con lo stesso nome
            if(nomeRisorsa.equals(nome))
                return true;
        }

        return false;
    }

    public void populateList(String fileName){
        URL url = ContenitoreTag.class.getResource("");
        String path = url.getPath() + fileName;  //path della cartella tagss
        resourcesDir = new File(path);

        if(!resourcesDir.exists()) { //se la cartella non esiste lo creo
            resourcesDir.mkdir();
        }

        path = path + "/" + fileName + ".dat";  //path del file su cui salvare il tag
        namesFile = new File(path);

        if(!namesFile.exists()) { //se il file non esiste lo creo
            try {
                namesFile.createNewFile();
            } catch (IOException e) {
                System.err.println("Impossibile creare file tag");
            }

        }

        getNomiRisorse().clear();
        getRisorse().clear();

        if(namesFile.length()>0)  //controllo che il file non sia vuoto e se non lo è provo a caricare la lista di tag e che la lista di tag non si agià stata inizializzata
            loadFromMemory();
    }

    protected void updateMemory(){
        try{
            FileOutputStream fos = new FileOutputStream(namesFile);

            ObjectOutputStream oos = new ObjectOutputStream(fos);

            try{
                oos.writeObject(getNomiRisorse());  //salvo in memoria la lista ausiliaria
            } finally {
                oos.flush();
                oos.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected abstract void addToMemory(T r);

    protected abstract void removeFromMemory(String nome);

    protected void loadFromMemory(){
        try{

            FileInputStream fis = new FileInputStream(namesFile);

            ObjectInputStream ois = new ObjectInputStream(fis);

            try {
                getNomiRisorse().addAll((ArrayList<String>) ois.readObject()); //aggiungo gli elementi copiati alla lista ausiliaria
            } finally {
                ois.close();
            }

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
