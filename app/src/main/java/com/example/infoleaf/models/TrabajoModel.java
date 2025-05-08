package com.example.infoleaf.models;

public class TrabajoModel {
    private int id;
    private String nombre;

    public TrabajoModel(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
