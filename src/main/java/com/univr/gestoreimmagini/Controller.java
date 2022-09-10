package com.univr.gestoreimmagini;

import com.univr.gestoreimmagini.modello.ImmagineAnnotata;
import com.univr.gestoreimmagini.modello.Model;
import com.univr.gestoreimmagini.modello.Tag;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.util.Callback;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

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

        displayImages(modello.getImages().getRisorse());
    }

    private void displayImages(List<ImmagineAnnotata> listaImmagini){
        for(ImmagineAnnotata immagineAnnotata : listaImmagini){
            displayImage(immagineAnnotata);
        }
    }

    private void displayImage(ImmagineAnnotata immagineAnnotata){
        ImageView imageView = new ImageView(immagineAnnotata.getImmagine());
        double width = 180;
        double height = width*9/16;
//        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        imageView.setPreserveRatio(true);
        Label label = new Label(immagineAnnotata.toString());
        Button button = new Button("x");
        HBox hBox = new HBox(label, button);
        VBox vBox = new VBox(imageView, hBox);
        vBox.setMaxWidth(width);
        vBox.setMinWidth(width);
        button.setOnAction((actionEvent) -> {
            imageGrid.getChildren().remove(vBox);
            modello.getImages().removeRisorsa(immagineAnnotata.toString());
        });
        imageGrid.getChildren().add(vBox);
    }

    @FXML
    private void addTag(ActionEvent actionEvent) {
        String nome = tagTextField.getText();
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
        System.out.println("Got " + files.size() + " files");
        String extension = FilenameUtils.getExtension(files.get(0).getPath());

        if(!(extension.equals("png") || extension.equals("jpg")))  //se il file non ha le estensioni supportate non piazzarlo
            return;

        Image image = new Image(files.get(0).getPath());
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

        String nome = immagineTextField.getText();
        if(modello.getImages().nomeInLista(nome))  //Non puoi asseganare lo stesso nome a due immagini diverse
            return;

        modello.getImages().addRisorsa(placedImage.getImage(), nome);
        displayImage(modello.getImages().getRisorsa(nome));
    }

}