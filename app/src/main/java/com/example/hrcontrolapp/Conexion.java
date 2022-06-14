package com.example.hrcontrolapp;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Conexion {
    private String URL_API = "http://35.175.173.253:3000/api/";
    public Conexion(){

    }

    public String ConexionGetByID(String api, String metodo){
        URL_API += api;
        System.out.println("---____________________________________________________________________________");
        System.out.println(api);
        System.out.println("---____________________________________________________________________________");
        String method = metodo;
        String respuesta = null;
        URL url = null;
        try {
            url = new URL(URL_API);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(5000);
            try(InputStream in = urlConnection.getInputStream()){
                InputStream buffer = new BufferedInputStream(in);
                respuesta = IOUtils.toString(buffer, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return respuesta;
    }


}
