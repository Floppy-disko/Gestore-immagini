package com.univr.gestoreimmagini;

import com.univr.gestoreimmagini.modello.ImmagineAnnotata;
import com.univr.gestoreimmagini.modello.Model;
import com.univr.gestoreimmagini.modello.Tag;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ImageManagerController implements Initializable {

    @FXML
    private ListView<Tag> tagsList;

    @FXML
    private TextField tagTextField;

    @FXML
    private TextField immagineTextField;

    @FXML
    private ImageView placedImage;

    @FXML
    private FlowPane imageGrid;

    private Model modello = Model.getModel();

    private boolean placedImageSet=false;

    private String placedImageExtension;

    private int selectedImageIndex;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {  //lancia all'avvio
        tagsList.setItems(modello.getTags().getRisorse()); //linka la lista di tag nella view alla lista di tag nel modello
        //modello.getTags().addRisorsa(new Tag("Test"));
        tagsList.setCellFactory(new Callback<ListView<Tag>, ListCell<Tag>>() {  //faccio celle della lista custom, la loro composizione è nella classe CustomCell
            @Override
            public ListCell<Tag> call(ListView<Tag> listView) {
                Button button = new Button();
                TagListCell cell = new TagListCell(button); //creo una custoListCell con un bottone che ha come eventHadler removeTag
                button.setText("X");  //inizializzo il bottone che ho passato alla cella
                button.setOnAction((actionEvent)->removeTag(cell.getItem())); //quando premo il bottone elimina l'item della cella
                return cell;
            }
        });

        modello.getImages().getRisorse().addListener((ListChangeListener<ImmagineAnnotata>) c -> {  //binding tra la lista di immagini nel modello e i figli di ImageGrid
            while (c.next()) {
                if (c.wasPermutated()) {
                    for (int i = c.getFrom(); i < c.getTo(); ++i) {
                        //permutate
                    }
                } else if (c.wasUpdated()) {
                    //update item
                } else {
                    for (ImmagineAnnotata remitem : c.getRemoved()) {
                        imageGrid.getChildren().remove(imageGrid.lookup("#" + remitem.getName() + "Image"));
                    }
                    for (ImmagineAnnotata additem : c.getAddedSubList()) {
                        displayImage(additem);
                    }
                }
            }
        });

        populateLists();
    }

    private void populateLists() {
        modello.getTags().populateList("tags");
        modello.getImages().populateList("images");
    }

    private void displayImage(ImmagineAnnotata immagineAnnotata){
        ImageBox imageBox = new ImageBox();
        imageBox.setDisplayedImage(immagineAnnotata.getImage());
        imageBox.setNameLabelText(immagineAnnotata.getName());
        imageBox.setRemoveButtonOnAction((actionEvent) -> {
            modello.getImages().removeRisorsa(immagineAnnotata); //sarebbe il metodo removeImage
        });
        imageBox.setImageOnClick((mouseEvent) -> {
            switchToWorkingImageView(mouseEvent);
            selectedImageIndex = modello.getImages().getRisorse().indexOf(immagineAnnotata);
        });
        imageBox.setId(immagineAnnotata.getName() + "Image");
        imageGrid.getChildren().add(imageBox);
    }

    @FXML
    private void addTag(ActionEvent actionEvent) {
        String nome = tagTextField.getText();
        tagTextField.setText(null);
        if(modello.getTags().nomeInLista(nome))  //Non puoi aggiungere due tag uguali
            return;

        modello.getTags().addRisorsa(nome);  //aggiungo il valore del textfield alla lista di tag nel modello
    }

    private void removeTag(Tag t){
        modello.getTags().removeRisorsa(t);
    }

    @FXML
    private void acceptImage(DragEvent event){
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.ANY);
        }
        event.consume();
    }

    @FXML
    private void placeImage(DragEvent event) {
        List<File> files = event.getDragboard().getFiles();
        placedImageExtension = FilenameUtils.getExtension(files.get(0).getPath());
        System.out.printf("\nGot %s file", placedImageExtension);

        if(!(placedImageExtension.equalsIgnoreCase("png") || placedImageExtension.equalsIgnoreCase("jpg")))  //se il file non ha le estensioni supportate non piazzarlo
            return;

        Image image = null;
        try(FileInputStream stream = new FileInputStream(files.get(0).getPath())) {
            image = new Image(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //System.out.println(FilenameUtils.getExtension(image.getUrl()));
        placedImage.setImage(image);
        placedImageSet=true;

        event.consume();
    }

    @FXML
    private void addImage(){
        //System.out.println(imageDnD.getImage());
        //getClass().getClassLoader().getResourceAsStream("Simo.jpg");
        if(placedImageSet==false)  //Salvo solo se l'immagine è stata settata
            return;

        String name = immagineTextField.getText();
        immagineTextField.setText(null);
        if(modello.getImages().nomeInLista(name))  //Non puoi asseganare lo stesso nome a due immagini diverse
            return;


        modello.getImages().addRisorsa(placedImage.getImage(), name, placedImageExtension);

        placedImageSet=false; //Comunico che non ho più immagini settate
        placedImage.setImage(null);
    }

    private void switchToWorkingImageView(MouseEvent mouseEvent) {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("WorkingInmageView.fxml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Scene scene = new Scene(root);
        Stage stage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();
        stage.setTitle("Working Image");
        stage.setScene(scene);
        stage.show();

        System.out.println(selectedImageIndex);
    }

}