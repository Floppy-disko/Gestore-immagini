package com.univr.gestoreimmagini.modello;

public class Model {

    private final static Model modello = new Model();       // Contiene l'unica istanza del singleton pattern
    private final TagContainer tags;                        // Riguarda i tag

    private final AnnotatedImageContainer images;           // Riguarda le immagini

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
