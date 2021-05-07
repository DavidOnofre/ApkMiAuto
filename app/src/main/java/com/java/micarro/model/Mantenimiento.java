package com.java.micarro.model;

public class Mantenimiento {

    private String FechaKilometraje;
    private String Gastos;
    private String Observaciones;
    private String TipoMantenimiento;

    public Mantenimiento(){

    }

    public String getFechaKilometraje() {
        return FechaKilometraje;
    }

    public void setFechaKilometraje(String fechaKilometraje) {
        FechaKilometraje = fechaKilometraje;
    }

    public String getGastos() {
        return Gastos;
    }

    public void setGastos(String gastos) {
        Gastos = gastos;
    }

    public String getObservaciones() {
        return Observaciones;
    }

    public void setObservaciones(String observaciones) {
        Observaciones = observaciones;
    }

    public String getTipoMantenimiento() {
        return TipoMantenimiento;
    }

    public void setTipoMantenimiento(String tipoMantenimiento) {
        TipoMantenimiento = tipoMantenimiento;
    }

    @Override
    public String toString() {
        return getFechaKilometraje() + " | " + getGastos() + " | " + getObservaciones() + " | " + getTipoMantenimiento() ;
    }
}
