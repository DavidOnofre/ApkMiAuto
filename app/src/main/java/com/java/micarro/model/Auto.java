package com.java.micarro.model;

public class Auto {

    private String Placa;
    private String Modelo;
    private String Kilometraje;
    private String Marca;

    public Auto() {
    }

    public String getModelo() {
        return Modelo;
    }

    public void setModelo(String modelo) {
        Modelo = modelo;
    }

    public String getPlaca() {
        return Placa;
    }

    public void setPlaca(String placa) {
        Placa = placa;
    }

    public String getKilometraje() {
        return Kilometraje;
    }

    public void setKilometraje(String kilometraje) {
        Kilometraje = kilometraje;
    }

    public String getMarca() {
        return Marca;
    }

    public void setMarca(String marca) {
        Marca = marca;
    }
}
