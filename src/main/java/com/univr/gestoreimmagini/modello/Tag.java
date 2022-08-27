package com.univr.gestoreimmagini.modello;

public class Tag{
    private String nome;

    public Tag(String nome) {
        this.nome = nome;
    }

    public void setTag(String nome) {
        this.nome = nome;
    }

    public String getTag() {
        return nome;
    }

    @Override
    public String toString() {
        return nome;
    }
}
