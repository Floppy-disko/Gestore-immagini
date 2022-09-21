package com.univr.gestoreimmagini.modello;

import java.util.ArrayList;

public interface AnnotationDAO {
    public ArrayList<Annotation> getAnnotations(ImmagineAnnotata immagineAnnotata);
    public void saveAnnotations(ImmagineAnnotata immagineAnnotata);
}
