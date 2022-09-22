package com.univr.gestoreimmagini.modello;

import javafx.beans.property.SimpleDoubleProperty;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AnnotationDAOImpl implements AnnotationDAO {
    private static class AnnotationDTO implements Serializable {  //Gli oggetti di questa classe mi servono solo per rappresentare un annotazione in memoria

        public String tag;

        public String value;

        public double X;

        public double Y;

        public double width;

        public double height;

        public AnnotationDTO(double X, double Y, double width, double height, String tag, String value){
            this.tag=tag;
            this.value=value;
            this.X=X;
            this.Y=Y;
            this.width=width;
            this.height=height;
        }
    }

    Model modello = Model.getModel();
    protected File resourcesDir;

    public AnnotationDAOImpl(String resourcesPath){
        resourcesDir = new File(resourcesPath);

        if(!resourcesDir.exists()) { //se la cartella non esiste lo creo
            resourcesDir.mkdir();
        }
    }

    @Override
    public ArrayList<Annotation> getAnnotations(ImmagineAnnotata immagineAnnotata) {
        String path = resourcesDir + "/" + immagineAnnotata.getName() + ".dat";  //path del file su cui salvare il tag
        File annotationFile = new File(path);

        ArrayList<Annotation> list = new ArrayList<>();

        if(!annotationFile.exists()) { //se il file non esiste lo creo
            try {
                annotationFile.createNewFile();
            } catch (IOException e) {
                System.err.println("Impossibile creare file tag");
            } finally {
                return new ArrayList<Annotation>();  //se il file non esiste ritorno una lista vuota
            }

        }

        for(AnnotationDTO a: loadFromMemory(annotationFile)){

            if(modello.getTags().nomeInLista(a.tag)==false)
                continue;

            Tag tag = modello.getTags().getRisorsa(a.tag);

            list.add(new Annotation(immagineAnnotata, a.X, a.Y, a.width, a.height, tag, a.value));
        }

        return list;
    }

    private ArrayList<AnnotationDTO> loadFromMemory(File annotationFile) {  //leggo dalla memoria il file

        ArrayList<AnnotationDTO> list = new ArrayList<>();

        try{

            FileInputStream fis = new FileInputStream(annotationFile);

            try(ObjectInputStream ois = new ObjectInputStream(fis)) {
                list = (ArrayList<AnnotationDTO>) ois.readObject(); //aggiungo gli elementi copiati alla lista ausiliaria
            } catch(EOFException e) {

            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            try {
                new PrintWriter(annotationFile).close(); //Se c'è un eccezzione che non è IO o ClassNotFound prbabimente il file è illeggibile quindi pulisci il file

            } catch(FileNotFoundException e2) {
                System.err.printf("File %s non trovato", annotationFile);
            }
        }

        return list;
    }

    @Override
    public void saveAnnotations(ImmagineAnnotata immagineAnnotata) {

        String path = resourcesDir + "/" + immagineAnnotata.getName() + ".dat";  //path del file su cui salvare il tag
        File annotationFile = new File(path);

        ArrayList<AnnotationDTO> serializableList = fromAnnotation(immagineAnnotata.getAnnotazioni());

        try{
            FileOutputStream fos = new FileOutputStream(annotationFile);

            ObjectOutputStream oos = new ObjectOutputStream(fos);

            try{
                oos.writeObject(serializableList);  //salvo in memoria la lista ausiliaria
            } finally {
                oos.flush();
                oos.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<AnnotationDTO> fromAnnotation(List<Annotation> list){
        ArrayList<AnnotationDTO> serializableList = new ArrayList<>();

        for(Annotation a: list){
            AnnotationDTO aDTO = new AnnotationDTO(a.getX(), a.getY(), a.getWidth(), a.getHeight(), a.getTag().toString(), a.getValue());
            serializableList.add(aDTO);
        }

        return serializableList;
    }
}
