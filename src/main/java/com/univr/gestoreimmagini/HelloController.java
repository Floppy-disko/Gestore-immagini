package com.univr.gestoreimmagini;

import com.univr.gestoreimmagini.modello.Model;
import com.univr.gestoreimmagini.modello.Tag;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import javafx.util.Callback;
import java.net.URL;
import java.util.ResourceBundle;

public class HelloController implements Initializable {

    @FXML
    private ListView<Tag> tagsList;

    @FXML
    private TextField tagTextField;

    private Model modello = Model.getModel();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {  //lancia all'avvio
        tagsList.setItems(modello.getTags().getRisorse()); //linka la lista di tag nella view alla lista di tag nel modello
        //modello.getTags().addRisorsa(new Tag("Test"));
        tagsList.setCellFactory(new Callback<ListView<Tag>, ListCell<Tag>>() {  //faccio celle della lista custom, la loro composizione è nella classe CustomCell
            @Override
            public ListCell<Tag> call(ListView<Tag> listView) {
                TagListCell cell = new TagListCell(HelloController.this.modello.getTags()); //creo una custoListCell con un bottone che ha come eventHadler removeTag
                return cell;
            }
        });
    }

    @FXML
    private void addTag(ActionEvent actionEvent) {
        modello.getTags().addRisorsa(new Tag(tagTextField.getText()));  //aggiungo il valore del textfield alla lista di tag nel modello
    }

}