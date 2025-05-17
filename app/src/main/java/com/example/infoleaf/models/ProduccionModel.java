package com.example.infoleaf.models;

public class ProduccionModel {
    private Integer id;
    private String nombreTierra;
    private String detalles;

    public ProduccionModel(int id, String nombreTierra, String detalles) {
        this.id = id;
        this.nombreTierra = nombreTierra;
        this.detalles = detalles;
    }

    public String getNombreTierra() {
        return nombreTierra;
    }

    public String getDetalles() {
        return detalles;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setDetalles(String detalles) {
        this.detalles = detalles;
    }

    public void setNombreTierra(String nombreTierra) {
        this.nombreTierra = nombreTierra;
    }
}
