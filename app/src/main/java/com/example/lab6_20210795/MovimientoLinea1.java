package com.example.lab6_20210795;

public class MovimientoLinea1 {
    private String idMovimiento;
    private String fecha; // puedes usar Timestamp también
    private String estacionEntrada;
    private String estacionSalida;
    private String tiempoViaje;

    public MovimientoLinea1() {} // Necesario para Firestore

    public MovimientoLinea1(String idMovimiento, String fecha, String estacionEntrada, String estacionSalida, String tiempoViaje) {
        this.idMovimiento = idMovimiento;
        this.fecha = fecha;
        this.estacionEntrada = estacionEntrada;
        this.estacionSalida = estacionSalida;
        this.tiempoViaje = tiempoViaje;
    }

    // Getters y setters


    public String getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(String idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getEstacionEntrada() {
        return estacionEntrada;
    }

    public void setEstacionEntrada(String estacionEntrada) {
        this.estacionEntrada = estacionEntrada;
    }

    public String getEstacionSalida() {
        return estacionSalida;
    }

    public void setEstacionSalida(String estacionSalida) {
        this.estacionSalida = estacionSalida;
    }

    public String getTiempoViaje() {
        return tiempoViaje;
    }

    public void setTiempoViaje(String tiempoViaje) {
        this.tiempoViaje = tiempoViaje;
    }
}

