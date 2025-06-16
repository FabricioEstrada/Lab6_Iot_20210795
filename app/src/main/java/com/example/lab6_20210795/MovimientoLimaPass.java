package com.example.lab6_20210795;

public class MovimientoLimaPass {
    private String idMovimiento;
    private String fecha;
    private String paraderoEntrada;
    private String paraderoSalida;
    private String tiempoViaje;
    public MovimientoLimaPass() {}

    public MovimientoLimaPass(String idMovimiento, String fecha, String paraderoEntrada, String paraderoSalida, String tiempoViaje) {
        this.idMovimiento = idMovimiento;
        this.fecha = fecha;
        this.paraderoEntrada = paraderoEntrada;
        this.paraderoSalida = paraderoSalida;
        this.tiempoViaje = tiempoViaje;
    }
    // Getters y Setters

    public String getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(String idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public String getTiempoViaje() {
        return tiempoViaje;
    }

    public void setTiempoViaje(String tiempoViaje) {
        this.tiempoViaje = tiempoViaje;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getParaderoEntrada() {
        return paraderoEntrada;
    }

    public void setParaderoEntrada(String paraderoEntrada) {
        this.paraderoEntrada = paraderoEntrada;
    }

    public String getParaderoSalida() {
        return paraderoSalida;
    }

    public void setParaderoSalida(String paraderoSalida) {
        this.paraderoSalida = paraderoSalida;
    }
}
