package com.example.infoleaf.models;

public class ProduccionModel {
    private String nombreTierra;
    private String detalles;

    public ProduccionModel(String nombreTierra, String detalles) {
        this.nombreTierra = nombreTierra;
        this.detalles = detalles;
    }

    public String getNombreTierra() {
        return nombreTierra;
    }

    public String getDetalles() {
        return detalles;
    }
}
