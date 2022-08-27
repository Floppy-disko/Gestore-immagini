package com.univr.gestoreimmagini.modello;

import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.*;

public class ContenitoreRisorse<T> {

    private final ObservableList<T> risorse;

    protected ContenitoreRisorse(){
        risorse = FXCollections.observableArrayList();

    }

    public T getRisorsa(int index){
        return risorse.get(index);
    }

    public void addRisorsa(T r){
        risorse.add(r);
    }

    public void removeRisorsa(T r){
        risorse.remove(r);
    }

    public ObservableList<T> getRisorse(){
        return risorse;
    }
}
