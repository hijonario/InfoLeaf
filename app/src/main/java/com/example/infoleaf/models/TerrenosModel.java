package com.example.infoleaf.models;

public class TerrenosModel {
    private int id;
    private String poligono;
    private String num_parcela;
    private String ubicacion;
    private double superficie;

    public TerrenosModel(int id, double superficie, String ubicacion, String num_parcela, String poligono) {
        this.id = id;
        this.superficie = superficie;
        this.ubicacion = ubicacion;
        this.num_parcela = num_parcela;
        this.poligono = poligono;
    }

    public TerrenosModel(double superficie, String ubicacion, String num_parcela, String poligono) {
        this.superficie = superficie;
        this.ubicacion = ubicacion;
        this.num_parcela = num_parcela;
        this.poligono = poligono;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPoligono() {
        return poligono;
    }

    public void setPoligono(String poligono) {
        this.poligono = poligono;
    }

    public String getNum_parcela() {
        return num_parcela;
    }

    public void setNum_parcela(String num_parcela) {
        this.num_parcela = num_parcela;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public double getSuperficie() {
        return superficie;
    }

    public void setSuperficie(double superficie) {
        this.superficie = superficie;
    }

    @Override
    public String toString() {
        return "Terreno{" +
                "poligono='" + poligono + '\'' +
                ", numParcela='" + num_parcela + '\'' +
                ", ubicacion='" + ubicacion + '\'' +
                ", superficie=" + superficie +
                '}';
    }
}
