package com.univr.gestoreimmagini;

import com.univr.gestoreimmagini.modello.Tag;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class CustomListCell extends ListCell<Tag> {
    private HBox content;
    private Label nome;

    public CustomListCell() {  //la cella sarà formata da un label ed un bottone per eliminarla
        super();
        nome = new Label();
        Button deleteButton = new Button("X");
        content = new HBox(deleteButton, nome);
        content.setSpacing(10);
    }

    @Override
    protected void updateItem(Tag item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null && !empty) { // <== test for null item and empty parameter
            nome.setText(item.getNome());
            setGraphic(content);
        } else {
            setGraphic(null);
        }
    }
}