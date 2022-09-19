package com.univr.gestoreimmagini;

import com.univr.gestoreimmagini.modello.Annotazione;
import com.univr.gestoreimmagini.modello.ImmagineAnnotata;
import com.univr.gestoreimmagini.modello.Model;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

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

    private Model modello = Model.getModel();

    private SimpleIntegerProperty selectedImageIndex = new SimpleIntegerProperty();

    private Image voidImage;

    private static double movementPercentage = 0.10; //Ad ogni passo scorro il 10 percento dell'immagine

    ArrayList<Annotazione> annotationList;

    private SimpleObjectProperty<Rectangle2D> viewPort = new SimpleObjectProperty<>(this, "viewPort");

    private SimpleObjectProperty<ImmagineAnnotata> immagineAnnotata = new SimpleObjectProperty<>(this, "immagineAnnotata");

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

        fullImage.imageProperty().bind(image);
        zoomImage.imageProperty().bind(image);

        rectanglesContainer.clipProperty().bind(Bindings.createObjectBinding(()->{
            Rectangle limit = new Rectangle();
            limit.setWidth(zoomImage.getBoundsInParent().getWidth());
            limit.setHeight(zoomImage.getBoundsInParent().getHeight());
//            Rectangle limit2 = new Rectangle();
//            limit2.setWidth(zoomImage.getBoundsInParent().getWidth());
//            limit2.setHeight(zoomImage.getBoundsInParent().getHeight());
//            rectanglesContainer.getChildren().add(limit2);
            return limit;
        }, mainImage.imageProperty()));  //tocca mettere questa al posto di zoomImage sennò sbagli larghezza immagine chissà perchè

        immagineAnnotata.addListener(new ChangeListener<ImmagineAnnotata>() {
            @Override
            public void changed(ObservableValue<? extends ImmagineAnnotata> observableValue, ImmagineAnnotata oldValue, ImmagineAnnotata newValue) {
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

    }

    private void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
        updateImages(newValue.intValue());
    }

    private void updateImages(int newValue){

        Image nextMainImage = image.get();
        Image nextLeftImage = getNextImage(getLeftIndex(newValue));
        Image nextRightImage = getNextImage(getRightIndex(newValue));

        mainImage.setImage(nextMainImage);

        resetViewPort();

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

    private ListChangeListener<Annotazione> annotationListener = new ListChangeListener<Annotazione>() {
        @Override
        public void onChanged(Change<? extends Annotazione> c) {
            while (c.next()) {
                for (Annotazione additem : c.getAddedSubList()) {
                    ResizableRectangle r = new ResizableRectangle(additem, immagineAnnotata.get());
                    rectanglesContainer.getChildren().add(r);
                    annotationList.add(additem);
                }

                for(Annotazione remitem : c.getRemoved()){
                    rectanglesContainer.getChildren().remove(annotationList.indexOf(remitem));
                    annotationList.remove(remitem);
                }
            }
        }
    };

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

}
