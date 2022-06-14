package com.example.hrcontrolapp;

public class Lista_entrada {
    private int idImagen = android.R.drawable.ic_menu_send;
    private String textoEncima;
    private String textoDebajo;
    private String fechaInicio;
    private String fechaFin;

    public Lista_entrada (String textoEncima, String textoDebajo, String fechaInicio, String fechaFin) {
        this.textoEncima = textoEncima;
        this.textoDebajo = textoDebajo;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    public String get_textoEncima() {
        return textoEncima;
    }

    public String get_textoDebajo() {
        return textoDebajo;
    }

    public String get_fechaInicio() {
        return fechaInicio;
    }

    public String get_fechaFin() {
        return fechaFin;
    }

    public int get_idImagen() {
        return idImagen;
    }
}
