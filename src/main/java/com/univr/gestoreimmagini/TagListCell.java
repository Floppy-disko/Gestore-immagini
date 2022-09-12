package com.univr.gestoreimmagini;

import com.univr.gestoreimmagini.modello.ContenitoreTag;
import com.univr.gestoreimmagini.modello.Tag;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

import java.util.function.Consumer;

public class TagListCell extends ListCell<Tag> {
    private HBox content;
    private Label nome;
    private Button button;
    private Consumer<Tag> buttonHandler; //Il metodo onAction per il bottone chiamerà il metodo passato al costruttore della cella con come parametro l'elemento rappresentato dalla cella

    public TagListCell(Button button) {  //la cella sarà formata da un label ed un bottone per eliminarla
        super();
        nome = new Label();
        this.button = button;
        content = new HBox(button, nome);
        content.setSpacing(10);
    }

    @Override
    protected void updateItem(Tag item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null && !empty) { // <== test for null item and empty parameter
            nome.setText(item.toString());
            button.setId(item.toString() + "Button"); //setto un id al bottone così lo posso identificare
            button.getStyleClass().add("tagButton");
            setGraphic(content);
        } else {
            setGraphic(null);
        }
    }

}