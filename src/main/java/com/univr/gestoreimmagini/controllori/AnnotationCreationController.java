package com.univr.gestoreimmagini.controllori;

import com.univr.gestoreimmagini.modello.Model;
import com.univr.gestoreimmagini.modello.Tag;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;

public class AnnotationCreationController implements Initializable {

    @FXML
    private ChoiceBox<Tag> tagPicker;
    @FXML
    private TextField valueTextField;

    private Model modello = Model.getModel();

    private BiConsumer<Tag, String> add;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tagPicker.getItems().addAll(modello.getTags().getRisorse());
    }

    @FXML
    private void keyListener(KeyEvent keyEvent){
        if(keyEvent.getCode() == KeyCode.ENTER) {
            if(valueTextField.equals(keyEvent.getSource()))
                addAnnotation(null);
            keyEvent.consume();
        }
    }

    @FXML
    private void addAnnotation(ActionEvent actionEvent) {

        String value = valueTextField.getText();

        if(value.length()>256) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore valore annotazione");
            alert.setContentText(String.format("Le annotazioni possono essere al massimo di 26 caratteri (ne hai usati %d)", value.length()));
            alert.showAndWait();
        }

        add.accept(tagPicker.getValue(), value);
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) tagPicker.getScene().getWindow();
        stage.close();
    }

    public void addAnnotationMethod(BiConsumer<Tag, String> add) {
        this.add=add;
    }
}
