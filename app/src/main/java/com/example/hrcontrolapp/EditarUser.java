package com.example.hrcontrolapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

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

public class EditarUser extends AppCompatActivity {
    private static int OK_RESULT_CODE = 1;
    private String perf;
    private static String URL_API_update = "http://35.175.173.253:3000/api/employee";
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    private Bundle savedInstanceStateP;
    private String user ="6272deabead2c9b95b5be231";
    private String admin ="6272deabead2c9b95b5be230";
    private String reportes = "6272deabead2c9b95b5be232";
    private String TAG = EditarUser.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        savedInstanceStateP = savedInstanceState;
        super.onCreate(savedInstanceStateP);
        setContentView(R.layout.activity_edit_user);
        System.out.println("Estoy en editar el usuario");
        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle("HRControlApp");

        SharedPreferences prefs = getSharedPreferences("shared_login_data",   Context.MODE_PRIVATE);
        String userId = prefs.getString("userIDSelec", "");

        perf = datosUser(userId);
        EditText textView = findViewById(R.id.textView2);
        Spinner areaText = findViewById(R.id.spinner_Areas);
        EditText nombreText = findViewById(R.id.nombre);
        TextView fecAlta = findViewById(R.id.fecAlta);
        fecAlta.setEnabled(false);
        EditText tlfFijo = findViewById(R.id.tlfFijo);
        EditText tlfMovil = findViewById(R.id.tlfMovil);
        Switch usuario = findViewById(R.id.userButton);
        Switch adminUs = findViewById(R.id.adminButton);
        Switch reportUser = findViewById(R.id.reportesButton);
        Button guardar = findViewById(R.id.buttoneditaruser);
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!nombreText.getText().toString().equals("")  && !textView.getText().toString().equals("") && !tlfFijo.getText().toString().equals("") && !tlfMovil.getText().toString().equals("")
                        && (usuario.isChecked() || adminUs.isChecked() || reportUser.isChecked())){
                    JSONObject c = new JSONObject();
                    try{
                        c.put("name", nombreText.getText().toString());
                        c.put("email", textView.getText().toString());
                        c.put("tlfFijo", tlfFijo.getText().toString());
                        c.put("tlfMovil", tlfMovil.getText().toString());
                        //c.put("activo", "true");

                        JSONArray roles = new JSONArray();
                        if(adminUs.isChecked() && reportUser.isChecked()){
                            roles.put("user"); //6272deabead2c9b95b5be231
                            roles.put("admin"); //6272deabead2c9b95b5be230
                            roles.put("reportes"); //6272deabead2c9b95b5be232
                        } else if(adminUs.isChecked()){
                            roles.put("user");
                            roles.put("admin");
                        } else if(reportUser.isChecked()){
                            roles.put("user");
                            roles.put("reportes");
                        } else {
                            roles.put("user");
                        }
                        c.put("roles", roles);
                    } catch (JSONException e){
                        Log.e(TAG, "Json parsing error: " + e.getMessage());
                    }

                    URL url = null;
                    try {
                        url = new URL(URL_API_update);
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
                        urlConnection.setRequestMethod("PUT");
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
                        Toast.makeText(getApplicationContext(), "Usuario creado correctamente", Toast.LENGTH_SHORT).show();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(), "El usuario ya existe", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                }
            }
        });
        String mas = perf;
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(mas);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            textView.setText((String) jsonObject.get("email"));
            String year = ((String) jsonObject.get("fechaAlta")).toString().split("-")[0];
            String month = ((String) jsonObject.get("fechaAlta")).toString().split("-")[1];
            String dayT = ((String) jsonObject.get("fechaAlta")).toString().split("-")[2];
            String day = dayT.split("T")[0];
            String hours = dayT.split("T")[1].split(":")[0] + ":" + dayT.split("T")[1].split(":")[1];
            String separador = "-";
            String fecAltaFormat = day + separador + month + separador + year + " " + hours;
            fecAlta.setText((String) fecAltaFormat);
            nombreText.setText((String) jsonObject.get("name"));
            try{
                String tlfF = ((Integer) jsonObject.get("tlfFijo")).toString();
                tlfFijo.setText((String) tlfF);
            } catch (Exception e){}
            try{
                String tlfM = ((Integer) jsonObject.get("tlfMovil")).toString();
                tlfMovil.setText((String) tlfM);
            } catch (Exception e){}


            JSONArray areas = jsonObject.getJSONArray("area");
            String areasList =null;
            for(int i=0; i< areas.length(); i++){
                if(!(areasList == null)){
                    areasList += (String) areas.get(i);
                } else {
                    areasList = (String) areas.get(i);
                }
            }
            areaText.setSelection(1);

            JSONArray roles = jsonObject.getJSONArray("roles");
            for(int i=0; i<roles.length(); i++){
                String rol = (String)roles.get(i);
                if(rol.equals(admin)){
                    adminUs.setChecked(true);
                } else if(rol.equals(user)){
                    usuario.setChecked(true);
                } else if(rol.equals(reportes)){
                    reportUser.setChecked(true);
                }
            }

            ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this, R.array.Areas, android.R.layout.simple_spinner_item);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
            areaText.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public String datosUser(String id){
        //Toast.makeText(getApplicationContext(), id, Toast.LENGTH_SHORT).show();
        Conexion con = new Conexion();
        String api = "employee/" + id;
        String perfil = null;
        perfil = con.ConexionGetByID(api, "GET");
        return perfil;
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
              //  Toast.makeText(this, "Perfil", Toast.LENGTH_SHORT).show();
                i = new Intent(this, EditarUser.class);
                startActivity(i);
                break;
            case R.id.nav_settings:
                System.out.println("Fichaje");
              //  Toast.makeText(this, "Fichaje", Toast.LENGTH_SHORT).show();
                i = new Intent(this, Fichar.class);
                startActivity(i);
                break;
            case R.id.nav_tareas:
                System.out.println("Tareas");
               // Toast.makeText(this, "Tareas", Toast.LENGTH_SHORT).show();
                i = new Intent(this, Tareas.class);
                startActivity(i);
                break;
            case R.id.users:
                System.out.println("Usuarios");
               // Toast.makeText(this, "Usuarios", Toast.LENGTH_SHORT).show();
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