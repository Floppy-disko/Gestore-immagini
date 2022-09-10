package com.univr.gestoreimmagini.modello;

import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.io.*;
import java.util.*;

public abstract class ContenitoreRisorse<T> implements Serializable {

    private final ObservableList<T> risorse = FXCollections.observableArrayList();

    //lista ausiliaria che tiene solo i nomi, utile perchè serializzabile e più rapida per la ricerca di duplicati tra i tag
    private final ArrayList<String> nomiRisorse = new ArrayList<>();  //ATTENZIONE: ogni volta in cui modifichi una delle due liste devi modificare anche l'altra per evitare inconsistenze

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

        nomiRisorse.remove(risorse.indexOf(r));  //rimuovi l'entry allo stesso index di r
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

    protected abstract void addToMemory(T r);

    protected abstract void removeFromMemory(String nome);

    protected abstract void loadFromMemory();
}
