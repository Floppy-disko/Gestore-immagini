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
        updateMemory(); //aggiungere e cancellare dalla memoria significa sovrascrivere tags.dat
    }

    /**
     * Sovrascrive tags.dat con la lista ausiliaria contenente i nomi dei tags
     */

    @Override
    protected void loadFromMemory(){
        super.loadFromMemory();  //riempie la lista ausiliaria
        for(String nome: getNomiRisorse())  //creo un tag nella lista principale per ogni nome letto da memoria
            getRisorse().add(new Tag(nome));
    }
}
