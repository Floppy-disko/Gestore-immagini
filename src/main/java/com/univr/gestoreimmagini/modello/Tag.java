package com.univr.gestoreimmagini.modello;

import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Tag {
    private StringProperty nome = new SimpleStringProperty();

    public Tag(String nome) {
        this.nome.set(nome);
    }

    public StringProperty nomeProperty() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome.set(nome);
    }

    @Override
    public String toString() {
        return nome.get();
    }
}
