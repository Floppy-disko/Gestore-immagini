package com.univr.gestoreimmagini.modello;

public class Model {

    private final static Model modello = new Model(); //contiene l'unica istanza del singleton
    private final ContenitoreTag tags;

    private final ContenitoreImmaginiAnnotate images;

    private Model() {
        tags = new ContenitoreTag();
        images = new ContenitoreImmaginiAnnotate();
    }

    public static Model getModel() {
        return modello;
    }

    public ContenitoreTag getTags(){
        return tags;
    }

    public ContenitoreImmaginiAnnotate getImages() {
        return images;
    }
}
