package com.univr.gestoreimmagini;

import java.util.List;

public class Modello {

    private final static Modello modello = new Modello();

    private Modello() {

    }

    public static Modello getModello() {
        return modello;
    }


}
