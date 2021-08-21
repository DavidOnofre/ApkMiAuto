package com.java.micarro.model;

public class Auto {

    private String Placa;
    private String Marca;
    private String Modelo;
    private String KilometrajeInicial;
    private String Kilometraje;
    private String KilometrajeAceite;
    private String KilometrajeBateria;
    private String KilometrajeElectricidad;
    private String KilometrajeGasolina;
    private String KilometrajeLlantas;

    public Auto() {
    }

    public String getPlaca() {
        return Placa;
    }

    public void setPlaca(String placa) {
        Placa = placa;
    }

    public String getMarca() {
        return Marca;
    }

    public void setMarca(String marca) {
        Marca = marca;
    }

    public String getModelo() {
        return Modelo;
    }

    public void setModelo(String modelo) {
        Modelo = modelo;
    }

    public String getKilometrajeInicial() {
        return KilometrajeInicial;
    }

    public void setKilometrajeInicial(String kilometrajeInicial) {
        KilometrajeInicial = kilometrajeInicial;
    }

    public String getKilometraje() {
        return Kilometraje;
    }

    public void setKilometraje(String kilometraje) {
        Kilometraje = kilometraje;
    }

    public String getKilometrajeAceite() {
        return KilometrajeAceite;
    }

    public void setKilometrajeAceite(String kilometrajeAceite) {
        KilometrajeAceite = kilometrajeAceite;
    }

    public String getKilometrajeBateria() {
        return KilometrajeBateria;
    }

    public void setKilometrajeBateria(String kilometrajeBateria) {
        KilometrajeBateria = kilometrajeBateria;
    }

    public String getKilometrajeElectricidad() {
        return KilometrajeElectricidad;
    }

    public void setKilometrajeElectricidad(String kilometrajeElectricidad) {
        KilometrajeElectricidad = kilometrajeElectricidad;
    }

    public String getKilometrajeGasolina() {
        return KilometrajeGasolina;
    }

    public void setKilometrajeGasolina(String kilometrajeGasolina) {
        KilometrajeGasolina = kilometrajeGasolina;
    }

    public String getKilometrajeLlantas() {
        return KilometrajeLlantas;
    }

    public void setKilometrajeLlantas(String kilometrajeLlantas) {
        KilometrajeLlantas = kilometrajeLlantas;
    }

    @Override
    public String toString() {
        return getPlaca() + " | " + getMarca() + " | " + getModelo() ;
    }
}