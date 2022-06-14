package com.example.hrcontrolapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Fichar extends AppCompatActivity {
    private static int OK_RESULT_CODE = 1;
    private static String URL_API_fichar = "http://35.175.173.253:3000/api/ficha";
    private String perf;
    private Intent i;
    private JSONArray res = new JSONArray();
    private JSONArray fichajeDia = new JSONArray();
    private String userId;
    public TableLayout tabla;
    public TextView fecFic, nombreText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fichar);
        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle("HRControlApp");


        SharedPreferences prefs = getSharedPreferences("shared_login_data", Context.MODE_PRIVATE);
        userId = prefs.getString("userID", "");
        //String userId = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        perf = datosUser(userId);

        nombreText = findViewById(R.id.userFic);
        fecFic = findViewById(R.id.editTextDate);
        Button fichar = findViewById(R.id.buttonFichar);
        tabla = findViewById(R.id.tablausuarios);



        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(perf);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {

            String year = ((String) jsonObject.get("fechaAlta")).toString().split("-")[0];
            String month = ((String) jsonObject.get("fechaAlta")).toString().split("-")[1];
            String dayT = ((String) jsonObject.get("fechaAlta")).toString().split("-")[2];
            String day = dayT.split("T")[0];
            String separador = "/";
            //String fecFicFormat = day + separador + month + separador + year;

            DateFormat fecha = new SimpleDateFormat("dd/MM/yyyy");
            Date initDate = new Date();
            String fecFicFormat = fecha.format(initDate);
            System.out.println(fecFicFormat);
            //initDate = dateIni.parse(fecFicFormat);

            fecFic.setText(fecFicFormat);
            nombreText.setText((String) jsonObject.get("name"));
            try{
                //obtengo los fichajes del día
                res = datosFicUser(userId, fecFic.getText().toString());
                //creo una fila por cada uno de los fichajes del día
                tablas();

            } catch (JSONException jsonException) {
            jsonException.printStackTrace();
            }
        } catch (JSONException  e) {
                e.printStackTrace();
            }

    }

    public void tablas(){
        try{
            res = datosFicUser(userId, fecFic.getText().toString());
            int count = tabla.getChildCount();
            //Borramos las filas de la tabla de fichaje excepto la cabecera
            for (int i = 1; i < count; i++) {
                View child = tabla.getChildAt(i);
                if (child instanceof TableRow) ((ViewGroup) child).removeAllViews();
            }
            //recorremos la respuesta del servidor y generamos una fila por cada objeto
            for (int i = 0; i< res.length(); i++){
                View registro = LayoutInflater.from(this).inflate(R.layout.table_row_fichaje, null, false);
                TextView hora_Inicio = registro.findViewById(R.id.horaInicio);
                TextView hora_Fin = registro.findViewById(R.id.horaFin);
                JSONObject object = res.getJSONObject(i);
                System.out.println(object.getString("horaInicio"));
                hora_Inicio.setText(object.getString("horaInicio"));
                if(object.has("horaFin")) {
                    System.out.println(object.getString("horaFin"));
                    hora_Fin.setText(object.getString("horaFin"));
                }
                tabla.addView(registro);
            }
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }
    }


    public String datosUser(String id) {
        //Toast.makeText(getApplicationContext(), id, Toast.LENGTH_SHORT).show();
        Conexion con = new Conexion();
        String api = "employee/" + id;
        String perfil = null;
        perfil = con.ConexionGetByID(api, "GET");
        return perfil;
    }


    public JSONArray datosFicUser(String id, String fechaHoy) throws JSONException {
        //Toast.makeText(getApplicationContext(), "Conectando", Toast.LENGTH_SHORT).show();
        //creo un List donde guardar el array que me viene como string
        String resultado = new String();
        Conexion con = new Conexion();
        String api = "ficha";
        resultado =con.ConexionGetByID(api, "GET");
        //Paso el string que me devuelve el API a un JSONArray que recorreré para el filtrado
        JSONArray json = new JSONArray(resultado);
        //Creo un JsonArray que llenaré con los fichajes del día actual
        JSONArray res = new JSONArray();
        //recorro json para obtener los valores del día actual y usuario
        for (int i = 0; i < json.length(); i++) {
            try {
                //convierto el primer elemento del array en un JSONObject y compruebo que para el campo fecha valor de la actual
                JSONObject jsonObject = json.getJSONObject(i);
                String fec = jsonObject.getString("fecha");
                String fecha = fec.split("T")[0];
                String dateI = fecha.split("-")[2] + "/" + fecha.split("-")[1] + "/" + fecha.split("-")[0];
                if (dateI.equals(fechaHoy)) {
                    //meto el valor en el resultado de la consulta para luego meterlo en la tabla
                    res.put(jsonObject);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return res;

    }

    public void clickFichar(View v) throws JSONException {
        //Compruebo si hay fichajes previos en el día
        if (res.length() > 0) {
            int i = res.length();
            JSONObject object = res.getJSONObject(i-1);
            //Compruebo si tiene hora de inicio y la de fin está vacía
            String hI = object.getString("horaInicio");

            if (!hI.equals("") && !object.has("horaFin")) {
                //llamamos al api para enviar _id y hora de fin y creamos la tabla (actualizar registro del fichaje)
                JSONObject c = new JSONObject();
                DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                Date date = new Date();
                String horaActual = dateFormat.format(date);
                c.put("horaFin", horaActual);

                URL url = null;
                try {
                    String link = URL_API_fichar + "/" + object.getString("_id") + "-" + horaActual;
                    url = new URL(link);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                HttpURLConnection urlConnection = null;
                try {
                    urlConnection = (HttpURLConnection) url.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    urlConnection.setConnectTimeout(5000);
                    urlConnection.setRequestProperty("X-HTTP-Method-Override", "PATCH");
                    urlConnection.setRequestMethod("PATCH");
                    urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    try (OutputStream os = urlConnection.getOutputStream()) {
                        byte[] input = c.toString().getBytes("utf-8");
                        os.write(input);
                        os.close();
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    InputStream br = new BufferedInputStream(urlConnection.getInputStream());
                    Toast.makeText(getApplicationContext(), "Fichaje creado correctamente", Toast.LENGTH_SHORT).show();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                tablas();

            } else {
                JSONObject c = new JSONObject();
                c.put("idUser", userId);
                SimpleDateFormat dateIni = new SimpleDateFormat("dd/MM/yyyy");
                Date initDate = null;
                try {
                    initDate = dateIni.parse(fecFic.getText().toString());
                    initDate = new Date(initDate.getTime() + (1000 * 60 * 60 * 24));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                c.put("fecha", initDate);
                DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                Date date = new Date();
                String horaActual = dateFormat.format(date);
                c.put("horaInicio", horaActual);

                URL url = null;
                try {
                    url = new URL(URL_API_fichar);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                HttpURLConnection urlConnection = null;
                try {
                    urlConnection = (HttpURLConnection) url.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    urlConnection.setConnectTimeout(5000);
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    try (OutputStream os = urlConnection.getOutputStream()) {
                        byte[] input = c.toString().getBytes("utf-8");
                        os.write(input);
                        os.close();
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    InputStream br = new BufferedInputStream(urlConnection.getInputStream());
                    Toast.makeText(getApplicationContext(), "Fichaje creado correctamente", Toast.LENGTH_SHORT).show();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                tablas();
            }
        } else {
            JSONObject c = new JSONObject();
            c.put("idUser", userId);
            SimpleDateFormat dateIni = new SimpleDateFormat("dd/MM/yyyy");
            Date initDate = null;
            try {
                initDate = dateIni.parse(fecFic.getText().toString());
                initDate = new Date(initDate.getTime() + (1000 * 60 * 60 * 24));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            c.put("fecha", initDate);
            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            Date date = new Date();
            String horaActual = dateFormat.format(date);
            c.put("horaInicio", horaActual);

            URL url = null;
            try {
                url = new URL(URL_API_fichar);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                urlConnection.setConnectTimeout(5000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                try (OutputStream os = urlConnection.getOutputStream()) {
                    byte[] input = c.toString().getBytes("utf-8");
                    os.write(input);
                    os.close();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                InputStream br = new BufferedInputStream(urlConnection.getInputStream());
                Toast.makeText(getApplicationContext(), "Fichaje creado correctamente", Toast.LENGTH_SHORT).show();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            res = datosFicUser(userId, fecFic.getText().toString());
            tablas();
        }
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        getMenuInflater().inflate(R.menu.navigation_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected( @NonNull MenuItem item ) {

        switch (item.getItemId()){
            case R.id.nav_account:
                System.out.println("Perfil");
                //Toast.makeText(this, "Perfil", Toast.LENGTH_SHORT).show();
                i = new Intent(this, Perfil.class);
                startActivity(i);
                break;
            case R.id.nav_settings:
                System.out.println("Fichaje");
                //Toast.makeText(this, "Fichaje", Toast.LENGTH_SHORT).show();
                i = new Intent(this, Fichar.class);
                startActivity(i);
                break;
            case R.id.nav_tareas:
                System.out.println("Tareas");
                //Toast.makeText(this, "Tareas", Toast.LENGTH_SHORT).show();
                i = new Intent(this, Tareas.class);
                startActivity(i);
                break;
            case R.id.nav_admin:
                System.out.println("Admin");
                //Toast.makeText(this, "Signup", Toast.LENGTH_SHORT).show();
                i = new Intent(this, Singup.class);
                startActivity(i);
                break;
            case R.id.nav_report:
                System.out.println("Reportes");
                //Toast.makeText(this, "Tareas", Toast.LENGTH_SHORT).show();
                i = new Intent(this, Reportes.class);
                startActivity(i);
                break;
            case R.id.nav_logout:
                finishAffinity();
                System.exit(0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
