package com.example.infoleaf.models;

public class GastoModel {
    private int id;
    private double dinero;
    private String descripcion;
    private String fecha;

    public GastoModel(int id, double dinero, String descripcion, String fecha) {
        this.id = id;
        this.dinero = dinero;
        this.descripcion = descripcion;
        this.fecha = fecha;
    }

    public double getDinero() { return dinero; }
    public String getDescripcion() { return descripcion; }
    public String getFecha() { return fecha; }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setDinero(double dinero) {
        this.dinero = dinero;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
