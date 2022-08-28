package com.univr.gestoreimmagini.modello;

import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.io.*;
import java.util.*;

public abstract class ContenitoreRisorse<T extends Risorsa> implements Serializable {

    private final ObservableList<T> risorse = FXCollections.observableArrayList();

    //lista ausiliaria che tiene solo i nomi, utile perchè serializzabile e più rapida per la ricerca di duplicati tra i tag
    private final ArrayList<String> nomiRisorse = new ArrayList<>();  //ATTENZIONE: ogni volta in cui modifichi una delle due liste devi modificare anche l'altra per evitare inconsistenze

    protected ContenitoreRisorse(){

    }

    public T getRisorsa(int index){
        return risorse.get(index);
    }

    public void addRisorsa(T r){

        nomiRisorse.add(r.getNome()); //aggiungo nome a lista ausiliaria
        risorse.add(r); //aggiungo tag con quel nome alla lista principale

        updateMemory();
    }


    public void removeRisorsa(T r){

        nomiRisorse.remove(risorse.indexOf(r));  //rimuovi l'entry allo stesso index di r
        risorse.remove(r);

        updateMemory();
    }

    public ObservableList<T> getRisorse(){
        return risorse;
    }
    public ArrayList<String> getNomiRisorse() {
        return nomiRisorse;
    }

    public boolean nomeInLista(String nome){
        for(String nomeRisorsa : nomiRisorse){   //cerca se c'è una risorsa con lo stesso nome
            if(nomeRisorsa.equals(nome))
                return true;
        }

        return false;
    }

    protected abstract void updateMemory();

    protected abstract void loadFromMemory() throws FileNotFoundException;
}
