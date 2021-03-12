package com.java.micarro.model;

import java.util.List;

public class Persona {

    private String uid;
    private String Nombre;
    private String Apellido;
    private String Correo;
    private String Telefono;
    private List<Auto> auto;
    private Mantenimiento mantenimiento;

    public Persona() {
    }

    public List<Auto> getAuto() {
        return auto;
    }

    public void setAuto(List<Auto> auto) {
        this.auto = auto;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getApellido() {
        return Apellido;
    }

    public void setApellido(String apellido) {
        Apellido = apellido;
    }

    public String getCorreo() {
        return Correo;
    }

    public void setCorreo(String correo) {
        Correo = correo;
    }

    public String getTelefono() {
        return Telefono;
    }

    public void setTelefono(String telefono) {
        Telefono = telefono;
    }

    public Mantenimiento getMantenimiento() {
        return mantenimiento;
    }

    public void setMantenimiento(Mantenimiento mantenimiento) {
        this.mantenimiento = mantenimiento;
    }

    @Override
    public String toString() {
        return auto.get(0).getPlaca() + " | " + auto.get(0).getModelo() +" | " + auto.get(0).getKilometraje() + " | " + auto.get(0).getMarca();
    }
}