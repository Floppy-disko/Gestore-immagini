package com.univr.gestoreimmagini;

import com.univr.gestoreimmagini.modello.ContenitoreTag;
import com.univr.gestoreimmagini.modello.Tag;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

public class TagListCell extends ListCell<Tag> {
    private HBox content;
    private Label nome;
    private Button button;
    private ContenitoreTag tags;

    public TagListCell(ContenitoreTag tags) {  //la cella sarà formata da un label ed un bottone per eliminarla
        super();
        this.tags = tags;
        nome = new Label();
        Button button = new Button("X");
        button.setOnAction(this::removeTag);  //Quando clicco il bottone chiama remove tag che rimuove il tag dal modello
        content = new HBox(button, nome);
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

    private void removeTag(ActionEvent e){
        tags.removeRisorsa(getItem());
    }

}