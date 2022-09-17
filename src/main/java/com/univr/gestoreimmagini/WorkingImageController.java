package com.univr.gestoreimmagini;

import com.univr.gestoreimmagini.modello.ImmagineAnnotata;
import com.univr.gestoreimmagini.modello.Model;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
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


    private Model modello = Model.getModel();

    private SimpleIntegerProperty selectedImageIndex = new SimpleIntegerProperty();

    private Image voidImage;

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

        updateImages(0);
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
        centerX.set(centerX.get()+15);
    }
    @FXML
    private void decreaseX(ActionEvent actionEvent) {
        centerX.set(centerX.get()-15);
    }
    @FXML
    private void increaseY(ActionEvent actionEvent) {
        centerY.set(centerY.get()+15);
    }
    @FXML
    private void decreaseY(ActionEvent actionEvent) {
        centerY.set(centerY.get()-15);
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
