package com.univr.gestoreimmagini;

import com.univr.gestoreimmagini.modello.Model;
import com.univr.gestoreimmagini.modello.Tag;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.util.Callback;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private ListView<Tag> tagsList;

    @FXML
    private TextField tagTextField;

    @FXML
    private ImageView imageDnD;

    private Model modello = Model.getModel();

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

    }

    @FXML
    private void addTag(ActionEvent actionEvent) {
        String nome = tagTextField.getText();
        if(modello.getTags().nomeInLista(nome))  //Non puoi due tag uguali
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
    private void addImage(DragEvent event) {
        List<File> files = event.getDragboard().getFiles();
        System.out.println("Got " + files.size() + " files");
        try {
            imageDnD.setImage(new Image(new FileInputStream(files.get(0))));
        } catch(FileNotFoundException e) {
            System.err.printf("File %s not found", files.get(0).toString());
        }

        event.consume();
    }
}