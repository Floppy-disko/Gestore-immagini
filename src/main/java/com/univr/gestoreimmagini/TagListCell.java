package com.univr.gestoreimmagini;

import com.univr.gestoreimmagini.modello.Tag;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class TagListCell extends ListCell<Tag> {
    @FXML
    private HBox content;
    @FXML
    private Label label;
    @FXML
    private Button button;

    public TagListCell() {  //la cella sarà formata da un label ed un bottone per eliminarla
        super();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("tagListCell.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    protected void updateItem(Tag item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null && !empty) { // <== test for null item and empty parameter
            label.setText(item.toString());
            button.setId(item.toString() + "Button"); //setto un id al bottone così lo posso identificare
            button.getStyleClass().add("tagButton");
            setGraphic(content);
        } else {
            setGraphic(null);
        }
    }

    public void setButtonOnAction(EventHandler<ActionEvent> eventHandler){
        button.setOnAction(eventHandler);
    }

}