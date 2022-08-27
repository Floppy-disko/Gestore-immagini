package com.univr.gestoreimmagini;

import java.util.List;

public class ContenitoreRisorse<T> {

    private List<T> risorse;

    public T getRisorsa(int index){
        return risorse.get(index);
    }

    public void addRisorsa(T r){
        risorse.add(r);
    }
}
