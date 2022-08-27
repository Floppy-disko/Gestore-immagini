package com.univr.gestoreimmagini;

import com.univr.gestoreimmagini.modello.Model;
import com.univr.gestoreimmagini.modello.Tag;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class HelloController implements Initializable {

    @FXML
    private TextField tagTextField;
    @FXML
    private ListView<Tag> lstTags;

    private Model modello = Model.getModel();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {  //lancia all'avvio
        lstTags.setItems(modello.getTags().getRisorse()); //linka la lista di tag nella view alla lista di tag nel modello
        //modello.getTags().addRisorsa(new Tag("Test"));
    }

    @FXML
    private void addTag(ActionEvent actionEvent) {
        modello.getTags().addRisorsa(new Tag(tagTextField.getText()));
    }
}