package com.example.hrcontrolapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

public class Perfil extends AppCompatActivity {
    private static int OK_RESULT_CODE = 1;
    private static String URL_API_signin = "http://35.175.173.253:3000/api/employee/";
    private String perf;
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    private Bundle savedInstanceStateP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        savedInstanceStateP = savedInstanceState;
        super.onCreate(savedInstanceStateP);
        setContentView(R.layout.activity_perfil);
        System.out.println("Estoy en el perfil");
        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle("HRControlApp");

        SharedPreferences prefs = getSharedPreferences("shared_login_data",   Context.MODE_PRIVATE);
        String userId = prefs.getString("userID", "");

        perf = datosUser(userId);
        TextView textView = findViewById(R.id.textView2);
        TextView areaText = findViewById(R.id.area);
        TextView nombreText = findViewById(R.id.nombre);
        TextView fecAlta = findViewById(R.id.fecAlta);
        TextView tlfFijo = findViewById(R.id.tlfFijo);
        TextView tlfMovil = findViewById(R.id.tlfMovil);
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
            String tlfF = ((Integer) jsonObject.get("tlfFijo")).toString();
            String tlfM = ((Integer) jsonObject.get("tlfMovil")).toString();
            tlfFijo.setText((String) tlfF);
            tlfMovil.setText((String) tlfM);
            JSONArray areas = jsonObject.getJSONArray("area");
            String areasList =null;
            for(int i=0; i< areas.length(); i++){
                if(!(areasList == null)){
                    areasList += (String) areas.get(i);
                } else {
                    areasList = (String) areas.get(i);
                }
            }

            areaText.setText("Administración");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public String datosUser(String id){
        Toast.makeText(getApplicationContext(), id, Toast.LENGTH_SHORT).show();
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
                i = new Intent(this, Perfil.class);
                startActivity(i);
                break;
            case R.id.nav_settings:
                System.out.println("Fichaje");
             //   Toast.makeText(this, "Fichaje", Toast.LENGTH_SHORT).show();
                i = new Intent(this, Fichar.class);
                startActivity(i);
                break;
            case R.id.nav_tareas:
                System.out.println("Tareas");
              //  Toast.makeText(this, "Tareas", Toast.LENGTH_SHORT).show();
                i = new Intent(this, Tareas.class);
                startActivity(i);
                break;
            case R.id.users:
                System.out.println("Usuarios");
              //  Toast.makeText(this, "Usuarios", Toast.LENGTH_SHORT).show();
                i = new Intent(this, Usuarios.class);
                startActivity(i);
                break;
            case R.id.new_user:
                System.out.println("Nuevo usuario");
               // Toast.makeText(this, "Nuevo usuario", Toast.LENGTH_SHORT).show();
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