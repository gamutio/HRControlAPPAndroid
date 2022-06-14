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
import android.widget.Switch;
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


public class Singup extends AppCompatActivity {
    private static int OK_RESULT_CODE = 1;
    private static String URL_API_signup = "http://35.175.173.253:3000/api/login/signup";
    private String perf;
    private Intent i;
    private String TAG = Singup.class.getSimpleName();
    private String [] rolesSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle("HRControlApp");


        SharedPreferences prefs = getSharedPreferences("shared_login_data",   Context.MODE_PRIVATE);
        String userId = prefs.getString("userID", "");
        String tokenUser = prefs.getString("token", "");

        EditText mailUser = findViewById(R.id.mailUser);
        EditText pwd = findViewById(R.id.pwdUser);
        //TextView areaText = findViewById(R.id.area);
        EditText nombreText = findViewById(R.id.nombre);
        EditText tlfFijo = findViewById(R.id.tlfFijo);
        EditText tlfMovil = findViewById(R.id.tlfMovil);
        Switch usuario = findViewById(R.id.userButton);
        Switch adminUs = findViewById(R.id.adminButton);
        Switch reportUser = findViewById(R.id.reportesButton);
        Button crearUser =  findViewById(R.id.buttoncrear);

        crearUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //compruebo si los campos de usuario y pass están rellenos
                if(!nombreText.getText().toString().equals("")  && !pwd.getText().toString().equals("") && !mailUser.getText().toString().equals("") && !tlfFijo.getText().toString().equals("") && !tlfMovil.getText().toString().equals("")
                        && (usuario.isChecked() || adminUs.isChecked() || reportUser.isChecked())){
                    //Cargo los roles seleccionados


                    JSONObject c = new JSONObject();
                    try{
                        c.put("name", nombreText.getText().toString());
                        c.put("email", mailUser.getText().toString());
                        c.put("tlfFijo", tlfFijo.getText().toString());
                        c.put("tlfMovil", tlfMovil.getText().toString());
                        c.put("activo", "true");
                        c.put("pwd", pwd.getText().toString());
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
                        url = new URL(URL_API_signup);
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
                        Toast.makeText(getApplicationContext(), "Usuario creado correctamente", Toast.LENGTH_SHORT).show();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(), "El usuario ya existe", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    //Una vez creado el usuario vaciamos el formulario
                    mailUser.setText("");
                    pwd.setText("");
                    nombreText.setText("");
                    tlfFijo.setText("");
                    tlfMovil.setText("");
                } else {
                    Toast.makeText(getApplicationContext(), "Revise los campos del formulario, hay campos vacíos", Toast.LENGTH_SHORT).show();
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

        switch (item.getItemId()){
            case R.id.nav_account:
                System.out.println("Perfil");
            //    Toast.makeText(this, "Perfil", Toast.LENGTH_SHORT).show();
                i = new Intent(this, Perfil.class);
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
             //   Toast.makeText(this, "Tareas", Toast.LENGTH_SHORT).show();
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
               // Toast.makeText(this, "Reportes", Toast.LENGTH_SHORT).show();
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
