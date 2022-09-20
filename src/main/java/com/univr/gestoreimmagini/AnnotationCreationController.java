package com.univr.gestoreimmagini;

import com.univr.gestoreimmagini.modello.Model;
import com.univr.gestoreimmagini.modello.Tag;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;

public class AnnotationCreationController implements Initializable {

    @FXML
    private ChoiceBox<String> tagPicker;
    @FXML
    private TextField valueTextField;
    @FXML
    private Button closeButton;

    private Model modello = Model.getModel();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tagPicker.getItems().addAll(modello.getTags().getNomiRisorse());
    }

    @FXML
    private void addAnnotation(ActionEvent actionEvent) {
        //immagineAnnotata.get().getAnnotazioni().add(new Annotazione(immagineAnnotata.get(), centerX.get(), centerY.get(), viewPort.get().getWidth()/10, viewPort.get().getHeight()/10));
    }

    @FXML
    private void closeWindow(ActionEvent actionEvent) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    public void addAnnotationMethod(BiConsumer<Tag, String> addAnnotation) {

    }
}
