package com.univr.gestoreimmagini;

import com.univr.gestoreimmagini.modello.ImmagineAnnotata;
import com.univr.gestoreimmagini.modello.Model;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
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
import javafx.util.converter.NumberStringConverter;

import java.awt.event.MouseEvent;
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
    private SimpleDoubleProperty offsetX = new SimpleDoubleProperty(0); //di quanto sono spostato a dx
    private SimpleDoubleProperty offsetY = new SimpleDoubleProperty(0); //di quanto sono spostato a sx

    public WorkingImageController(){
        try {
            voidImage = new Image(getClass().getResource("void.png").openStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fullImage.imageProperty().bind(image);
        zoomImage.imageProperty().bind(image);

        updateImages(0);
        zoomin.disableProperty().bind(Bindings.createBooleanBinding(()-> zoomLevel.get()>=4, zoomLevel));
        zoomout.disableProperty().bind(Bindings.createBooleanBinding(()-> zoomLevel.get()<=1, zoomLevel));
        increaseY.disableProperty().bind(Bindings.createBooleanBinding(()-> offsetY.get()>=image.get().getHeight(), offsetY));

        zoomImage.viewportProperty().bind(viewPort);
        viewPort.bind(Bindings.createObjectBinding(()-> {  //faccio cambiare la viewPort quando cambiano zoom, offestX e offsetY
            double newWidth = image.get().getWidth() / zoomLevel.get();
            double newHeight = image.get().getHeight() / zoomLevel.get();
            return new Rectangle2D(offsetX.get(), offsetY.get(), newWidth, newHeight);
        }, image, zoomLevel, offsetX, offsetY));

        selectedImageIndex.addListener(this::changed);
    }

    private void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
        updateImages(newValue.intValue());
    }

    private void updateImages(int newValue){

        immagineAnnotata.set(modello.getImages().getRisorsa(newValue));
        image.set(getNextImage(newValue));

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
        offsetX.set(0);
        offsetY.set(0);
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

        zoomLevel.set(zoomLevel.get()+0.2);

    }

    @FXML
    private void zoomOut(ActionEvent actionEvent) {

        zoomLevel.set(zoomLevel.get() - 0.2);

    }

    @FXML
    private void decreaseX(ActionEvent actionEvent) {
    }

    @FXML
    private void increaseY(ActionEvent actionEvent) {
    }

    @FXML
    private void increaseX(ActionEvent actionEvent) {
    }

    @FXML
    private void decreaseY(ActionEvent actionEvent) {
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
