package com.univr.gestoreimmagini.modello;

public class TagContainer extends ResourcesContainer<Tag> {

    protected TagContainer() {
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
        updateMemory();     // Aggiungere e cancellare dalla memoria significa sovrascrivere tags.dat
    }

    /**
     * Sovrascrive tags.dat con la lista ausiliaria contenente i nomi dei tags
     */
    @Override
    protected void loadFromMemory(){
        super.loadFromMemory();                 // Riempie la lista ausiliaria
        for(String nome: getNomiRisorse())      // Creo un tag nella lista principale per ogni nome letto da memoria
            getRisorse().add(new Tag(nome));
    }
}
