package com.univr.gestoreimmagini;

import com.univr.gestoreimmagini.modello.Model;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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

    private Model modello = Model.getModel();

    private SimpleIntegerProperty selectedImageIndex = new SimpleIntegerProperty();

    private Image voidImage;

    public WorkingImageController(){
        try {
            voidImage = new Image(getClass().getResource("void.png").openStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fullImage.imageProperty().bind(mainImage.imageProperty());
        zoomImage.imageProperty().bind(mainImage.imageProperty());

        updateImages(0);

        selectedImageIndex.addListener(this::changed);
    }

    private void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
        updateImages(newValue.intValue());
    }

    private void updateImages(int newValue){
        Image nextMainImage = getNextImage(newValue);
        Image nextLeftImage = getNextImage(getLeftIndex(newValue));
        Image nextRightImage = getNextImage(getRightIndex(newValue));

        mainImage.setImage(nextMainImage);

        int size = modello.getImages().getSize();

        if(size==1) {  //Se ho solo un immagine mostro solo quella al centro
            leftButton.setOnAction(null);  //disattivo entrambi i bottoni
            leftImage.setImage(voidImage);
            leftButton.getStyleClass().add("deactivatedButton");
            rightButton.setOnAction(null);
            rightImage.setImage(voidImage);
            rightButton.getStyleClass().add("deactivatedButton");
        }

        else if(size==2){
            if(newValue==1){  //se l'immagini principale è l'ultima della lista mostro solo l'immagine alla sua sinistra
                leftButton.setOnAction(this::scrollLeft);
                leftImage.setImage(nextLeftImage);
                rightButton.setOnAction(null);
                rightButton.getStyleClass().add("deactivatedButton");
                rightImage.setImage(voidImage);
            }

            else{  //se invece è la prima della lista mostro solo quella alla sua destra
                leftButton.setOnAction(null);
                leftButton.getStyleClass().add("deactivatedButton");
                leftImage.setImage(voidImage);
                rightButton.setOnAction(this::scrollRight);
                rightImage.setImage(nextRightImage);
            }
        }

        else{
            leftImage.setImage(nextLeftImage);
            rightImage.setImage(nextRightImage);
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
