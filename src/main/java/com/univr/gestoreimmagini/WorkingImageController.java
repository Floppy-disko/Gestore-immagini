package com.univr.gestoreimmagini;

import com.univr.gestoreimmagini.modello.Annotation;
import com.univr.gestoreimmagini.modello.AnnotatedImage;
import com.univr.gestoreimmagini.modello.Model;
import com.univr.gestoreimmagini.modello.Tag;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class WorkingImageController implements Initializable {

    @FXML
    private Button button;

    @FXML
    private ImageView mainImage;
    @FXML
    private ImageView leftImage;
    @FXML
    private ImageView rightImage;
    @FXML
    private Button leftButton;
    @FXML
    private Button rightButton;
    @FXML
    private ImageView fullImage;
    @FXML
    private ImageView zoomImage;
    @FXML
    private Button zoomin;
    @FXML
    private Button zoomout;
    @FXML
    private Button increaseY;
    @FXML
    private Button decreaseY;
    @FXML
    private Button increaseX;
    @FXML
    private Button decreaseX;
    @FXML
    private Rectangle redRectangle;
    @FXML
    private Group rectanglesContainer;
    @FXML
    private ListView<Annotation> annotationListView;

    private Model modello = Model.getModel();

    private SimpleIntegerProperty selectedImageIndex = new SimpleIntegerProperty();

    private Image voidImage;

    private static double movementPercentage = 0.10; //Ad ogni passo scorro il 10 percento dell'immagine

    ArrayList<Annotation> annotationList = new ArrayList<>();

    private double ratio;

    private SimpleObjectProperty<Rectangle2D> viewPort = new SimpleObjectProperty<>(this, "viewPort");

    private SimpleObjectProperty<AnnotatedImage> immagineAnnotata = new SimpleObjectProperty<>(this, "immagineAnnotata");

    private SimpleObjectProperty<Image> image = new SimpleObjectProperty<>(this, "image");

    private SimpleDoubleProperty zoomLevel = new SimpleDoubleProperty(1);  //quanto sono zoommatop
    private SimpleDoubleProperty centerX = new SimpleDoubleProperty(0); //di quanto sono spostato a dx
    private SimpleDoubleProperty centerY = new SimpleDoubleProperty(0); //di quanto sono spostato a sx

    public WorkingImageController(){
        try {
            voidImage = new Image(getClass().getResource("void.png").openStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        immagineAnnotata.bind(Bindings.createObjectBinding(()-> modello.getImages().getRisorsa(selectedImageIndex.get()), selectedImageIndex));
        image.bind(Bindings.createObjectBinding(()-> immagineAnnotata.get().getImage(), immagineAnnotata));

        updateImages(0); //non togliere questo sennò se clicchi la prima immagine parte con centro in 0

        fullImage.imageProperty().bind(image);
        zoomImage.imageProperty().bind(image);

        viewPort.addListener(transformListener);

        //immagineAnnotata.get().getAnnotazioni().addListener(annotationListener);
        immagineAnnotata.get().getAnnotazioni().addListener(annotationListener);
        immagineAnnotata.addListener(new ChangeListener<AnnotatedImage>() {
            @Override
            public void changed(ObservableValue<? extends AnnotatedImage> observableValue, AnnotatedImage oldValue, AnnotatedImage newValue) {
                resetLists();
                oldValue.getAnnotazioni().removeListener(annotationListener);
                newValue.getAnnotazioni().addListener(annotationListener);
            }
        });

        zoomin.disableProperty().bind(Bindings.createBooleanBinding(()-> zoomLevel.get()>=4, zoomLevel));
        zoomout.disableProperty().bind(Bindings.createBooleanBinding(()-> zoomLevel.get()<=1, zoomLevel));
        increaseY.disableProperty().bind(Bindings.createBooleanBinding(()-> {
            double height = image.get().getHeight();
            double weightedHeight = height/zoomLevel.get();  //altezza pesata con il livello di zoom
            return centerY.get()+weightedHeight/2 >= height;  //prendo la y del centro più quanto è continua l'immagine dopo al centro e verifico che non superi l'altezza
        }, zoomLevel, centerY));
        decreaseY.disableProperty().bind(Bindings.createBooleanBinding(()-> {
            double height = image.get().getHeight();
            double weightedHeight = height/zoomLevel.get();
            return centerY.get()-weightedHeight/2 <= 0;
        }, zoomLevel, centerY));
        increaseX.disableProperty().bind(Bindings.createBooleanBinding(()-> {
            double width = image.get().getWidth();
            double weightedWidth = width/zoomLevel.get();
            return centerX.get()+weightedWidth/2 >= width;
        }, zoomLevel, centerX));
        decreaseX.disableProperty().bind(Bindings.createBooleanBinding(()-> {
            double width = image.get().getWidth();
            double weightedWidth = width/zoomLevel.get();
            return centerX.get()-weightedWidth/2 <= 0;
        }, zoomLevel, centerX));

        zoomImage.viewportProperty().bind(viewPort);
        viewPort.bind(Bindings.createObjectBinding(()-> {  //faccio cambiare la viewPort quando cambiano zoom, offestX e offsetY
            double newWidth = image.get().getWidth() / zoomLevel.get();
            double newHeight = image.get().getHeight() / zoomLevel.get();
            double offsetX = centerX.get()-newWidth/2;     //offsetX è lo spigolo in alto a sx quindi è il centro meno metà della lunghezza
            double offsetY = centerY.get()-newHeight/2;
            return new Rectangle2D(offsetX, offsetY, newWidth, newHeight);
        }, image, zoomLevel, centerX, centerY));

        redRectangle.heightProperty().bind(Bindings.createDoubleBinding(()->{
            double portion = viewPort.get().getHeight() / fullImage.getImage().getHeight();
            return fullImage.getBoundsInParent().getHeight() * portion;
        }, viewPort));

        redRectangle.widthProperty().bind(Bindings.createDoubleBinding(()->{
            double portion = viewPort.get().getWidth() / fullImage.getImage().getWidth();
            return fullImage.getBoundsInParent().getWidth() * portion;
        }, viewPort));

        redRectangle.translateXProperty().bind(Bindings.createDoubleBinding(()->{
            double ratio = fullImage.getBoundsInParent().getWidth() / fullImage.getImage().getWidth(); //Calcolo quanto è grande fullImage rispetto al numero di pixel dell'immagine
            double imageCenterX = fullImage.getImage().getWidth() / 2;
            double offsetX = centerX.get()-imageCenterX;  //quanto è spostato il displayPort rispetto al centro dell'immagine
            return offsetX * ratio;
        }, centerX));

        redRectangle.translateYProperty().bind(Bindings.createDoubleBinding(()->{
            double ratio = fullImage.getBoundsInParent().getHeight() / fullImage.getImage().getHeight(); //Calcolo quanto è grande fullImage rispetto al numero di pixel dell'immagine
            double imageCenterY = fullImage.getImage().getHeight() / 2;
            double offsetY = centerY.get()-imageCenterY;  //quanto è spostato il displayPort rispetto al centro dell'immagine
            return offsetY * ratio;
        }, centerY));

        selectedImageIndex.addListener(this::changed);

        annotationListView.itemsProperty().bind(Bindings.createObjectBinding(()->
                immagineAnnotata.get().getAnnotazioni(), immagineAnnotata));

        //faccio celle della lista custom, la loro composizione è nella classe CustomCell
        annotationListView.cellFactoryProperty().bind(Bindings.createObjectBinding(()-> (Callback<ListView<Annotation>, ListCell<Annotation>>) listView -> {
            AnnotationListCell cell = new AnnotationListCell();
            cell.setButtonOnAction((actionEvent)->{
                immagineAnnotata.get().removeAnnotation(cell.getItem());         //quando premo il bottone elimina l'item della cella
            });
            return cell;
        }, immagineAnnotata));

//        annotationListView.setCellFactory(new Callback<ListView<Annotazione>, ListCell<Annotazione>>() {
//            @Override
//            public ListCell<Annotazione> call(ListView<Annotazione> listView) {
//                AnnotationListCell cell = new AnnotationListCell();
//                cell.setButtonOnAction((actionEvent)->
//                    immagineAnnotata.get().getAnnotazioni().remove(cell.getItem())); //quando premo il bottone elimina l'item della cella
//            return cell;
//            }
//        });
    }

    private void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
        updateImages(newValue.intValue());
    }

    private void updateImages(int newValue){

        resetViewPort();

        Image nextMainImage = image.get();
        Image nextLeftImage = getNextImage(getLeftIndex(newValue));
        Image nextRightImage = getNextImage(getRightIndex(newValue));

        mainImage.setImage(nextMainImage);

        int size = modello.getImages().getSize();

        if(size==1) {  //Se ho solo un immagine mostro solo quella al centro
            leftButton.setDisable(true);
            leftImage.setImage(voidImage);

            rightButton.setDisable(true);
            rightImage.setImage(voidImage);
        }

        else if(size==2){
            if(newValue==1){  //se l'immagini principale è l'ultima della lista mostro solo l'immagine alla sua sinistra
                leftButton.setDisable(false);
                leftImage.setImage(nextLeftImage);

                rightButton.setDisable(true);
                rightImage.setImage(voidImage);
            }

            else{  //se invece è la prima della lista mostro solo quella alla sua destra
                leftButton.setDisable(true);
                leftImage.setImage(voidImage);

                rightButton.setDisable(false);
                rightImage.setImage(nextRightImage);
            }
        }

        else{
            leftImage.setImage(nextLeftImage);
            rightImage.setImage(nextRightImage);
        }

    }

    private void resetViewPort(){
        zoomLevel.set(1);
        centerX.set(image.get().getWidth()/2);
        centerY.set(image.get().getHeight()/2);
    }

    private ChangeListener<Rectangle2D> transformListener = new ChangeListener<Rectangle2D>() {
        @Override
        public void changed(ObservableValue<? extends Rectangle2D> observableValue, Rectangle2D oldvalue, Rectangle2D newvalue) {
            ratio = zoomImage.getBoundsInParent().getWidth() / image.get().getWidth();
            rectanglesContainer.getTransforms().clear();
            double translateX = -(centerX.get()-image.get().getWidth()/2)*ratio;
            double translateY = -(centerY.get()-image.get().getHeight()/2)*ratio;
            Translate translate = new Translate(translateX, translateY);
            Scale scale = new Scale(zoomLevel.get(), zoomLevel.get(), zoomImage.getBoundsInParent().getWidth()/2, zoomImage.getBoundsInParent().getHeight()/2);

            rectanglesContainer.getTransforms().add(scale); //devo mettere prima lo scale del translate sennò moltiplica anche lo spostamento
            rectanglesContainer.getTransforms().add(translate);

            Rectangle limit = new Rectangle(); //creo il rettangolo per clippare il rectanglesContainer
            double width = viewPort.get().getWidth()*ratio;  //la larghezza di viewPort pesata con il rateo
            double height = viewPort.get().getHeight()*ratio;
            double offsetX = centerX.get()*ratio-width/2;  //la posizione di center pesata meno metà della larghezza mi da la posizione del bordo del rettangolo
            double offsetY = centerY.get()*ratio-height/2;
            limit.setX(offsetX);
            limit.setY(offsetY);
            limit.setWidth(width);
            limit.setHeight(height);

            rectanglesContainer.setClip(limit);
        }
    };

    private ListChangeListener<Annotation> annotationListener = new ListChangeListener<Annotation>() {
        @Override
        public void onChanged(Change<? extends Annotation> c) {
            while (c.next()) {
                for (Annotation additem : c.getAddedSubList()) {
                    ratio = zoomImage.getBoundsInParent().getWidth() / image.get().getWidth();
                    ResizableRectangle r = new ResizableRectangle(additem, ratio);
                    rectanglesContainer.getChildren().add(r);
                    annotationList.add(additem);
                }

                for(Annotation remitem : c.getRemoved()){
                    rectanglesContainer.getChildren().remove(annotationList.indexOf(remitem));  //mantengo la lista solo per sapere l'index dell'element eliminato
                    annotationList.remove(remitem);
                }
            }
        }
    };

    private void resetLists(){
        annotationList.clear(); //se immagineAnnotata è cambiata devi ripopolare la lista con le sue annotazioni
        rectanglesContainer.getChildren().clear(); //se immagineAnnotata cambia devo resettare i figli di rectanglesConteiner e ripopolarli
        for(Annotation annotation : immagineAnnotata.get().getAnnotazioni()){
            annotationList.add(annotation);
            ratio = zoomImage.getBoundsInParent().getWidth() / image.get().getWidth();
            rectanglesContainer.getChildren().add(new ResizableRectangle(annotation, ratio));
        }
    }

    private Image getNextImage(int newValue){
        return modello.getImages().getRisorsa(newValue).getImage();
    }

    public int getSelectedImageIndex() {
        return selectedImageIndex.get();
    }

    public SimpleIntegerProperty selectedImageIndexProperty() {
        return selectedImageIndex;
    }

    public void setSelectedImageIndex(int selectedImageIndex) {
        this.selectedImageIndex.set(selectedImageIndex);
        immagineAnnotata.get().populateAnnotations();

//        ResizableRectangle r1 = new ResizableRectangle();
//        //r1.setTranslateX(0);
////        Rectangle rec = new Rectangle();
////        rec.setWidth(100);
////        rec.setHeight(100);
////        rec.setX(-1);
////        rec.setY(-2);
////        rectanglesContainer.setClip(rec);
//        Scale scale = new Scale(2, 2, zoomImage.getBoundsInParent().getWidth()/2, zoomImage.getBoundsInParent().getHeight()/2);
////        Translate translate = new Translate(zoomImage.getBoundsInParent().getWidth()/2, zoomImage.getBoundsInParent().getHeight()/2);
//        Translate translate = new Translate(-5, -5);
//        r1.getTransforms().add(translate);
//        rectanglesContainer.getChildren().add(r1);
//        System.out.println(rectanglesContainer.prefWidth(0));
//        //rectanglesContainer.setLayoutX();
    }

    @FXML
    private void scrollRight(ActionEvent actionEvent) {
        setSelectedImageIndex(getRightIndex(getSelectedImageIndex()));
    }

    @FXML
    private void scrollLeft(ActionEvent actionEvent) {
        setSelectedImageIndex(getLeftIndex(getSelectedImageIndex()));
    }

    private int getRightIndex(int index){
        int size = modello.getImages().getSize();

        int rightIndex = (getSelectedImageIndex()+1) % size; //Aumento di uno ma se arrivo alla fine riparto dall'inizio (effetto PacMan)

        return rightIndex;
    }

    private int getLeftIndex(int index){
        int size = modello.getImages().getSize();

        int leftIndex = (index-1+size) % size;  //per evitare di avere resto negativo sommo sempre il valore del numero di immagini (size), così da rendere l'operazione modulo e non resto

        return leftIndex;
    }

    @FXML
    private void zoomIn(ActionEvent actionEvent) {
        double newZoomLevel = zoomLevel.get() + 0.2;
//        double newOffsetX = offsetX.get() + (image.get().getWidth()/zoomLevel.get() - image.get().getWidth()/newZoomLevel);
//        double newOffsetY = offsetX.get() + (image.get().getHeight()/zoomLevel.get() - image.get().getHeight()/newZoomLevel);
//        offsetX.set(newOffsetX);
//        offsetY.set(newOffsetY);
        zoomLevel.set(newZoomLevel);

    }

    @FXML
    private void zoomOut(ActionEvent actionEvent) {
        double newZoomLevel = zoomLevel.get() - 0.2;
//        double newOffsetX = offsetX.get() + (image.get().getWidth()/zoomLevel.get() - image.get().getWidth()/newZoomLevel);
//        double newOffsetY = offsetX.get() + (image.get().getHeight()/zoomLevel.get() - image.get().getHeight()/newZoomLevel);
//        offsetX.set(newOffsetX);
//        offsetY.set(newOffsetY);
        zoomLevel.set(newZoomLevel);

        correctCenter(); //Se faccio zoomout posso avere l'immagine fuori dal bordo quindi devo correggere la posizione del centro

    }

    private void correctCenter(){
        //Correggo la y
        double height = image.get().getHeight();
        double weightedHeight = height/zoomLevel.get();
        if(centerY.get()+weightedHeight/2 >= height)  //Se sborda in basso (y centro troppo alta)
            centerY.set(height - weightedHeight/2); //Se sbordo tolgo la differenza che c'è tra la larghezza della viewPort e lo spazio disponibile
        else if(centerY.get()-weightedHeight/2 <= 0)  //Se sbordo in alto
            centerY.set(weightedHeight/2);

        //Correggo la x
        double width = image.get().getWidth();
        double weightedWidth = width/zoomLevel.get();
        if(centerX.get()+weightedWidth/2 >= width)
            centerX.set(width - weightedWidth/2); //Se sbordo tolgo la differenza che c'è tra la larghezza della viewPort e lo spazio disponibile
        else if(centerX.get()-weightedWidth/2 <= 0)
            centerX.set(weightedWidth/2);
    }

    @FXML
    private void increaseX(ActionEvent actionEvent) {
        centerX.set(centerX.get()+movementX());
        correctCenter();  //Ogni volta che sposto l'immagine potrei sbordare, quindi se sbordo correggo
    }
    @FXML
    private void decreaseX(ActionEvent actionEvent) {
        centerX.set(centerX.get()-movementX());
        correctCenter();
    }
    @FXML
    private void increaseY(ActionEvent actionEvent) {
        centerY.set(centerY.get()+movementY());
        correctCenter();
    }
    @FXML
    private void decreaseY(ActionEvent actionEvent) {
        centerY.set(centerY.get()-movementY());
        correctCenter();
    }

    private double movementX(){
        return viewPort.get().getWidth() * movementPercentage;
    }

    private double movementY(){
        return viewPort.get().getHeight() * movementPercentage;
    }

    @FXML
    private void switchToImageEditorView(ActionEvent actionEvent) {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("ImageManagerView.fxml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Scene scene = new Scene(root, 1280, 720);
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        stage.setTitle("Image editor");
        stage.setScene(scene);
        stage.show();

        if(modello.getImages().resourceFileExists(getSelectedImageIndex()) == false)
            modello.getImages().restoreImage(getSelectedImageIndex());
    }

    public void startAnnotation(MouseEvent mouseEvent) {

        if(modello.getTags().getRisorse().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(String.format("Errore tag"));
            alert.setContentText("La lista tag è vuota quindi non è possibile inserire annotazioni");
            alert.showAndWait();
            return;
        }

        Parent root = null;
        FXMLLoader loader = null;
        try {
            loader = new FXMLLoader(getClass().getResource("AnnotationCreationView.fxml"));
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Scene scene = new Scene(root, 280, 260);
        Stage secondStage = new Stage();
        secondStage.initModality(Modality.APPLICATION_MODAL);
        secondStage.setTitle("Annotation");
        secondStage.setScene(scene);
        secondStage.show();


        AnnotationCreationController controller = loader.getController();
        controller.addAnnotationMethod((Tag tag, String value)-> {
            Annotation annotation = new Annotation(immagineAnnotata.get(), centerX.get(), centerY.get(), viewPort.get().getWidth()/10, viewPort.get().getHeight()/10, tag, value);
            //immagineAnnotata.get().getAnnotazioni().add(annotazione);
            immagineAnnotata.get().addAnnotation(annotation);
        });

        //immagineAnnotata.get().getAnnotazioni().add(new Annotazione(immagineAnnotata.get(), centerX.get(), centerY.get(), viewPort.get().getWidth()/10, viewPort.get().getHeight()/10));
    }
}
