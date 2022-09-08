package com.univr.gestoreimmagini.modello;

import java.io.*;

public class Model {

    private final static Model modello = new Model(); //contiene l'unica istanza del singleton
    private final ContenitoreTag tags;

    private final ContenitoreImmagini images;

    private Model() {
        tags = new ContenitoreTag();
        images = new ContenitoreImmagini();
    }

    public static Model getModel() {
        return modello;
    }

    public ContenitoreTag getTags(){
        return tags;
    }

    public ContenitoreImmagini getImages() {
        return images;
    }
}
