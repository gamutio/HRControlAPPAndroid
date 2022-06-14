package com.example.hrcontrolapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Usuarios extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.hrcontrolapp.EditarUser";
    private static int OK_RESULT_CODE = 1;
    private static String URL_API_usuarios = "http://35.175.173.253:3000/api/employee/";
    private JSONArray usuarios = new JSONArray();
    private String userSelected="";
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    private Bundle savedInstanceStateP;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_users);
        System.out.println("Estoy en admin usuarios");
        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle("HRControlApp");

        SharedPreferences prefs = getSharedPreferences("shared_login_data",   Context.MODE_PRIVATE);
        String userId = prefs.getString("userID", "");

        //TextView textView = findViewById(R.id.nombreuser);
        TableLayout tabla = findViewById(R.id.tablausuarios);
        try {
            usuarios = datosUser();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            for (int i = 0; i< usuarios.length(); i++){
                View registro = LayoutInflater.from(this).inflate(R.layout.table_row_np, null, false);
                TextView colNombre = registro.findViewById(R.id.nombreuser);
                TextView colMail = registro.findViewById(R.id.mailusuario);
                ImageButton colEdit = registro.findViewById(R.id.buttonEditar);
                JSONObject object = usuarios.getJSONObject(i);
                System.out.println(object.getString("name"));
                colNombre.setText(object.getString("name"));
                System.out.println(object.getString("email"));
                colMail.setText(object.getString("email"));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    colEdit.setAutofillHints(object.getString("_id"));
                }
                tabla.addView(registro);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public JSONArray datosUser() throws JSONException {
        //Toast.makeText(getApplicationContext(), "Conectando", Toast.LENGTH_SHORT).show();
        Conexion con = new Conexion();
        String api = "employee";
        String resultado= con.ConexionGetByID(api, "GET");
        JSONArray  array = new JSONArray (resultado);
        return array;
    }

    public void clickEditar(View v){
        userSelected = null;
        String id = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
           id = String.valueOf(v.getAutofillHints()[0]);
           userSelected = id;
        }
        SharedPreferences prefs = getSharedPreferences("shared_login_data",   Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("userIDSelec", userSelected);
        editor.commit();
        Intent intent = new Intent(this, EditarUser.class);
        String message = userSelected;
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
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