package com.univr.gestoreimmagini.modello;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;

public abstract class ResourcesContainer<T> implements Serializable {

    private final ObservableList<T> risorse = FXCollections.observableArrayList();

    // Lista ausiliaria che tiene solo i nomi, utile perché serializzabile e più rapida per la ricerca di duplicati tra i tag
    private final ArrayList<String> nomiRisorse = new ArrayList<>();  //ATTENZIONE: ogni volta in cui modifichi una delle due liste devi modificare anche l'altra per evitare inconsistenze

    protected File namesFile;       // Nome del file
    protected File resourcesDir;    // Directory che contiene le risorse

    protected ResourcesContainer(){

    }

    /**
    * Ritorna la risorsa all'indice passato
    *
    * @param index indice
    * @return risorsa
    * */
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
     *
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
        nomiRisorse.remove(r.toString());       // Rimuovi l'entry allo stesso index di r
        risorse.remove(r);
        removeFromMemory(r.toString());
    }

    public ObservableList<T> getRisorse(){
        return risorse;
    }
    public ArrayList<String> getNomiRisorse() {
        return nomiRisorse;
    }

    /**
    * Verifica che esiste una risorsa con il nome indicato
    *
    * @param nome nome risorsa
    * @return true se esiste
    * */
    public boolean nomeInLista(String nome){
        for(String nomeRisorsa : nomiRisorse){   //cerca se c'è una risorsa con lo stesso nome
            if(nomeRisorsa.equals(nome))
                return true;
        }

        return false;
    }


    public void populateList(String fileName){
        URL url = getClass().getResource("");
        String path = url.getPath() + fileName;         // Path della cartella tagss
        resourcesDir = new File(path);

        if(!resourcesDir.exists()) {                    // Se la cartella non esiste lo creo
            resourcesDir.mkdir();
        }

        path = path + "/" + fileName + ".dat";          // Path del file su cui salvare il tag
        namesFile = new File(path);

        if(!namesFile.exists()) {                       // Se il file non esiste lo creo
            try {
                namesFile.createNewFile();
            } catch (IOException e) {
                System.err.println("Impossibile creare file risorse");
            }

        }

        getNomiRisorse().clear();
        getRisorse().clear();

        if(namesFile.length() > 0)                        // Controllo che il file non sia vuoto e se non lo è provo a caricare la lista di tag e che la lista di tag non si agià stata inizializzata
            loadFromMemory();
    }

    /**
    * Si salvano gli oggetti delle risorse nei file .dat
    * */
    protected void updateMemory(){
        try{
            FileOutputStream fos = new FileOutputStream(namesFile);

            ObjectOutputStream oos = new ObjectOutputStream(fos);

            try{
                oos.writeObject(getNomiRisorse());      // Salvo in memoria la lista ausiliaria
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

    /**
    * Legge le risorse dalla memoria leggendo l'oggetto memorizzato nel file .dat
    * */
    protected void loadFromMemory(){
        try{

            FileInputStream fis = new FileInputStream(namesFile);

            ObjectInputStream ois = new ObjectInputStream(fis);

            try {
                getNomiRisorse().addAll((ArrayList<String>) ois.readObject());      // Si aggiunge gli elementi copiati alla lista ausiliaria
            } finally {
                ois.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            try {
                new PrintWriter(namesFile).close();                                // Se c'è un eccezione che non è IO o ClassNotFound prbabimente il file è illeggibile quindi pulisci il file

            } catch(FileNotFoundException e2) {
                System.err.printf("File %s non trovato", namesFile);
            }
        }
    }
}
