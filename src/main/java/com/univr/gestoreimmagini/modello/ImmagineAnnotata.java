package com.univr.gestoreimmagini.modello;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

public class ImmagineAnnotata {

    private SimpleObjectProperty<Image> image = new SimpleObjectProperty<>(this, "image");
    private String name;

    private String extension;

    //private ObservableList<Annotazione> annotazioni;
    private ObservableList<Annotation> annotazioni = FXCollections.observableArrayList();

    private AnnotatedImageContainer container;

    private AnnotationDAOImpl dao;

    public ImmagineAnnotata(AnnotatedImageContainer container, Image image, String name, String extension) {
        setImage(image);
        this.name = name;
        this.extension = extension;
        this.container=container;
    }

    public SimpleObjectProperty<Image> imageProperty() {
        return image;
    }

    public void setImage(Image image) {
        this.image.set(image);
    }

    public Image getImage(){
        return this.image.get();
    }

    public String toString(){
        return this.name + "." + this.extension;
    }

    public String getName(){
        return this.name;
    }

    public String getExtension() {
        return extension;
    }

    public ObservableList<Annotation> getAnnotazioni() {
        return annotazioni;
    }

    public void setAnnotazioni(ObservableList<Annotation> annotazioni) {
        this.annotazioni = annotazioni;
    }

    public void addAnnotation(Annotation annotation){
        annotazioni.add(annotation);
        updateMemory();
    }

    public void removeAnnotation(Annotation annotation){
        annotazioni.remove(annotation);
        updateMemory();
    }

    public void updateMemory(){
        if(dao!=null)
            dao.saveAnnotations(this);
    }

    public void populateAnnotations(String fileName){

        if(annotazioni.isEmpty()==false)  //Se ho già popolato la lista non rileggere
            return;

        dao = new AnnotationDAOImpl(getClass().getResource("").getPath() + "/" +fileName);
        annotazioni.addAll(dao.getAnnotations(this));
    }
}
