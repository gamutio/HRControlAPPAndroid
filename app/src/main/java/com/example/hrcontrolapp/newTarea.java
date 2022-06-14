package com.example.hrcontrolapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class newTarea extends AppCompatActivity {
        private static int OK_RESULT_CODE = 1;
        private static String URL_API_tareas = "http://35.175.173.253:3000/api/tareas";
        private String perf;
        private Intent i;
        private String TAG = newTarea.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newtarea);
        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle("HRControlApp");


        SharedPreferences prefs = getSharedPreferences("shared_login_data",   Context.MODE_PRIVATE);
        String userId = prefs.getString("userID", "");

        EditText titulo = findViewById(R.id.titulo);
        EditText description = findViewById(R.id.description);
        EditText fechaInicio = findViewById(R.id.fechaInicio);
        EditText fechaFin = findViewById(R.id.fechaFin);

        Button crearUser =  findViewById(R.id.buttoncrear);

        crearUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //compruebo si los campos de usuario y pass están rellenos
                if (titulo.getText().toString().equals("") && fechaInicio.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Título y Fecha de inicio son obligatorios", Toast.LENGTH_SHORT).show();
                } else {
                    JSONObject c = new JSONObject();
                    try {
                        c.put("titulo", titulo.getText().toString());
                        SimpleDateFormat dateIni = new SimpleDateFormat("dd/MM/yyyy");
                        Date initDate = dateIni.parse(fechaInicio.getText().toString());
                        c.put("fechaInicio", initDate);
                        c.put("idUser", userId);
                        if (!description.getText().toString().equals("")) {
                            c.put("description", description.getText().toString());
                        }
                        if (!fechaFin.getText().toString().equals("")) {
                            c.put("fechaFin", fechaFin.getText().toString());
                        }
                    } catch (JSONException | ParseException e) {
                        Log.e(TAG, "Json parsing error: " + e.getMessage());
                    }
                    URL url = null;
                    try {
                        url = new URL(URL_API_tareas);
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
                        Toast.makeText(getApplicationContext(), "Tarea creada correctamente", Toast.LENGTH_SHORT).show();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //Una vez creado el usuario vaciamos el formulario
                    titulo.setText("");
                    description.setText("");
                    fechaInicio.setText("");
                    fechaFin.setText("");

                }
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        getMenuInflater().inflate(R.menu.navigation_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected( @NonNull MenuItem item ) {
        Intent i;
        switch (item.getItemId()){
            case R.id.nav_account:
                System.out.println("Perfil");
                //Toast.makeText(this, "Perfil", Toast.LENGTH_SHORT).show();
                i = new Intent(this, Usuarios.class);
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
            case R.id.users:
                System.out.println("Usuarios");
                //Toast.makeText(this, "Usuarios", Toast.LENGTH_SHORT).show();
                i = new Intent(this, Usuarios.class);
                startActivity(i);
                break;
            case R.id.new_user:
                System.out.println("Nuevo usuario");
                //Toast.makeText(this, "Nuevo usuario", Toast.LENGTH_SHORT).show();
                i = new Intent(this, Singup.class);
                startActivity(i);
                break;
            case R.id.nav_report:
                System.out.println("Reportes");
                //Toast.makeText(this, "Reportes", Toast.LENGTH_SHORT).show();
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
