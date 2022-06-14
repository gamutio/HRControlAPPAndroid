package com.example.hrcontrolapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Tareas extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.hrcontrolapp.Tareas";
    private ListView lista;
    private String userId;
    private Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tareas_pendientes);
        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle("HRControlApp");
        FloatingActionButton mas = findViewById(R.id.floatingActionButton3);

        mas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickNuevaTarea(v);
            }
        });

        SharedPreferences prefs = getSharedPreferences("shared_login_data", Context.MODE_PRIVATE);
        userId = prefs.getString("userID", "");

        ArrayList<Lista_entrada> datos = new ArrayList<Lista_entrada>();
        try {
            JSONArray jarray = datosTareas();
            if(jarray != null){
                for(int i=0; i<jarray.length(); i++){
                    String datosArray = jarray.getString(i);
                    String titul = datosArray.split(",")[0];
                    String desc = datosArray.split(",")[1];
                    String fecha = datosArray.split(",")[2].split("T")[0];
                    String dateI = fecha.split("-")[2] + "/" + fecha.split("-")[1] + "/" + fecha.split("-")[0];
                    String dateF = datosArray.split(",")[3];
                    dateF = dateF.replace("\\", "");
                    datos.add(new Lista_entrada(titul, desc,dateI,dateF));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        lista = (ListView) findViewById(R.id.ListView_listado);
        lista.setAdapter(new Lista_adaptador(this, R.layout.entrada_tarea, datos){
           @Override
            public void onEntrada(Object entrada, View view) {
                if (entrada != null) {
                    TextView texto_superior_entrada = (TextView) view.findViewById(R.id.textView_superior);
                    if (texto_superior_entrada != null)
                        texto_superior_entrada.setText(((Lista_entrada) entrada).get_textoEncima());

                    TextView texto_inferior_entrada = (TextView) view.findViewById(R.id.textView_inferior);
                    if (texto_inferior_entrada != null)
                        texto_inferior_entrada.setText(((Lista_entrada) entrada).get_textoDebajo());

                    TextView fechaInicio_entrada = (TextView) view.findViewById(R.id.fechaInicio);
                    if (fechaInicio_entrada != null)
                        fechaInicio_entrada.setText(((Lista_entrada) entrada).get_fechaInicio());

                    TextView fechaFin_entrada = (TextView) view.findViewById(R.id.fechaFin);
                    if (fechaFin_entrada != null)
                        fechaFin_entrada.setText(((Lista_entrada) entrada).get_fechaFin());

                    ImageView imagen_entrada = (ImageView) view.findViewById(R.id.imageView_imagen);
                    if (imagen_entrada != null)
                        imagen_entrada.setImageResource(((Lista_entrada) entrada).get_idImagen());
                }
            }
        });

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> pariente, View view, int posicion, long id) {
                Lista_entrada elegido = (Lista_entrada) pariente.getItemAtPosition(posicion);

                CharSequence texto = "Seleccionado: " + elegido.get_textoDebajo();
                Toast toast = Toast.makeText(Tareas.this, texto, Toast.LENGTH_LONG);
                toast.show();
            }
        });

    }

    public JSONArray datosTareas() throws JSONException {
        Toast.makeText(getApplicationContext(), "Conectando", Toast.LENGTH_SHORT).show();
        Conexion con = new Conexion();
        String api = "tareas";
        String resultado= con.ConexionGetByID(api, "GET");
        //Paso el string que me devuelve el API a un JSONArray que recorreré para el filtrado
        JSONArray json = new JSONArray(resultado);
        //Creo un JsonArray que llenaré con las tareas del usuario actual
        JSONArray res = new JSONArray();
        //recorro json para obtener los valores del usuario
        for (int i = 0; i < json.length(); i++) {
            try {
                //convierto el primer elemento del array en un JSONObject y compruebo que para el usuario actual
                JSONObject jsonObject = json.getJSONObject(i);
                if (jsonObject.has("idUser") && jsonObject.getString("idUser").equals(userId)) {
                    //meto el valor en el resultado de la consulta para luego meterlo en la tabla
                    String tarea = jsonObject.getString("titulo")+","+jsonObject.getString("description")+","+jsonObject.getString("fechaInicio")+","+jsonObject.getString("fechaFin");
                    res.put(tarea);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    public void clickNuevaTarea(View v){
        SharedPreferences prefs = getSharedPreferences("shared_login_data",   Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("userID", userId);
        editor.commit();
        i = new Intent(this, newTarea.class);
        String message = userId;
        i.putExtra(EXTRA_MESSAGE, message);
        startActivity(i);
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
               // Toast.makeText(this, "Tareas", Toast.LENGTH_SHORT).show();
                i = new Intent(this, Tareas.class);
                startActivity(i);
                break;
            case R.id.nav_admin:
                System.out.println("Admin");
                //Toast.makeText(this, "Admin", Toast.LENGTH_SHORT).show();
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
