package com.univr.gestoreimmagini.modello;

public class Model {

    private final static Model modello = new Model(); //contiene l'unica istanza del singleton
    private final TagContainer tags;

    private final AnnotatedImageContainer images;

    private Model() {
        tags = new TagContainer();
        images = new AnnotatedImageContainer();
    }

    public static Model getModel() {
        return modello;
    }

    public TagContainer getTags(){
        return tags;
    }

    public AnnotatedImageContainer getImages() {
        return images;
    }

    public void updateMemory(){
        tags.updateMemory();
        images.updateMemory();
    }
}
