package com.example.hrcontrolapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.IOUtils;

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


public class MainActivity extends Activity {
    public static final String EXTRA_MESSAGE = "com.example.hrcontrolapp.Perfil";
    private static int OK_RESULT_CODE = 1;
    private static String URL_API_signin = "http://35.175.173.253:3000/api/login/signin";
    protected String tokenuser = null;
    protected String userId = null;
    Button b1, b2;
    EditText user, pas;
    String respuesta = null;
    private String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //cargo la vista del login al inicio
        setContentView(R.layout.login);

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        //enlazo los campos del formulario al layout
        b1 = (Button)  findViewById(R.id.buttonLogin);
        user = (EditText) findViewById(R.id.user);
        pas = (EditText) findViewById(R.id.pwd);
        b2 = (Button) findViewById(R.id.cancel);
        //pongo el modo de escucha del botón de nuevo pass
        b2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Función no disponible, solicite al administrador", Toast.LENGTH_SHORT).show();
            }
        });
        //pongo el modo de escucha del botón de login
        b1.setOnClickListener(new View.OnClickListener(){
            //modifico el método genérico del onclick para adapatarlo a lo que necesito en este caso
            @Override
            public void onClick(View v) {
                //compruebo si los campos de usuario y pass están rellenos
                if (!user.getText().toString().equals("") && !pas.getText().toString().equals("")) {
                    String usuario = user.getText().toString();
                    String pwd = pas.getText().toString();
                    JSONObject c = new JSONObject();
                    try {
                        c.put("email", usuario);
                        c.put("pwd", pwd);
                    } catch (JSONException e) {
                        Log.e(TAG, "Json parsing error: " + e.getMessage());
                    }

                    URL url = null;
                    try {
                        url = new URL(URL_API_signin);
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
                            //byte[] input = c.toString().getBytes("utf-8");
                            os.write(c.toString().getBytes("UTF-8"));
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
                        //guardo el token recibido en el String tokenuser
                        tokenuser = IOUtils.toString(br, "UTF-8");
                        //convertimos el string en json
                        try{
                            JSONObject jsonObject = new JSONObject(tokenuser);
                            userId = (String) jsonObject.get("userId");
                        } catch (JSONException err) {
                            System.out.println("Exception : "+err.toString());
                        }
                        //Muestro en pantalla el token, solo para dev
                        Toast.makeText(getApplicationContext(), userId, Toast.LENGTH_SHORT).show();
                        //nueva actividad -- Muestro el perfil del usuario
                        if(tokenuser!= null){
                            //Toast.makeText(getApplicationContext(), "lanzaremos perfil", Toast.LENGTH_SHORT).show();
                            SharedPreferences prefs = getSharedPreferences("shared_login_data",   Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("userID", userId);
                            editor.putString("token", tokenuser);
                            editor.commit();
                            perfil(v);
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(), "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Introduzca su usuario y contraseña", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void perfil(View view){
        Intent intent = new Intent(this, Perfil.class);
        String message = userId;
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }
}

