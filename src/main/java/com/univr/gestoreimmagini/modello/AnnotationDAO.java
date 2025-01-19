package com.univr.gestoreimmagini.modello;

import java.util.ArrayList;

public interface AnnotationDAO {
    public ArrayList<Annotation> getAnnotations(AnnotatedImage annotatedImage);
    public void saveAnnotations(AnnotatedImage annotatedImage);
}
