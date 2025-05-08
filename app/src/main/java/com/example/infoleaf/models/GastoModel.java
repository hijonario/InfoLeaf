package com.example.infoleaf.models;

public class GastoModel {
    private double dinero;
    private String descripcion;
    private String fecha;

    public GastoModel(double dinero, String descripcion, String fecha) {
        this.dinero = dinero;
        this.descripcion = descripcion;
        this.fecha = fecha;
    }

    public double getDinero() { return dinero; }
    public String getDescripcion() { return descripcion; }
    public String getFecha() { return fecha; }
}
