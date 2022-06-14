package com.example.hrcontrolapp;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


public class Reportes extends AppCompatActivity {
    Button generatePDFbtn, openPDF;
    int pageHeight = 1120;
    private Intent i;
    int pagewidth = 792;
    private static final int PERMISSION_REQUEST_CODE = 200;
    Bitmap bmp, scaledbmp;
    private Bundle savedInstanceState;
    private File file = null;
    private JSONArray users = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceStated) {
        savedInstanceState = savedInstanceStated;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reportes);

        generatePDFbtn = findViewById(R.id.idBtnGeneratePDF);
        openPDF = findViewById(R.id.idBtnOpenPDF);
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.hrcntrollogo);
        scaledbmp = Bitmap.createScaledBitmap(bmp, 140, 140, false);

        // comprobamos los permisos de la app
        if (checkPermission()) {
            //Toast.makeText(this, "tenemos permiso", Toast.LENGTH_SHORT).show();
        } else {
            requestPermission();
        }

        generatePDFbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generatePDF();
            }
        });
        openPDF.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mostrarPDF();
            }
        });

    }

    private void generatePDF() {
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        Paint title = new Paint();

        //damos formato de página al documento a crear
        PdfDocument.PageInfo mypageInfo = new PdfDocument.PageInfo.Builder(pagewidth, pageHeight, 1).create();

        //indicamos donde comienza la página
        PdfDocument.Page myPage = pdfDocument.startPage(mypageInfo);

        // creamos el canvas en la página del pdf
        Canvas canvas = myPage.getCanvas();

        // Encabezado del pdf (logo, título y subtitulo)
        canvas.drawBitmap(scaledbmp, 56, 40, paint);
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        title.setTextSize(22);
        title.setColor(ContextCompat.getColor(this, R.color.black));
        canvas.drawText("HRControlAPP", 209, 100, title);
        canvas.drawText("Reporte de Usuarios", 209, 80, title);

        // damos formato a los textos.
        title.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        title.setColor(ContextCompat.getColor(this, R.color.black));
        title.setTextSize(15);

        // centramos el texto en la página
        title.setTextAlign(Paint.Align.LEFT);
        title.setUnderlineText(true);
        title.setFakeBoldText(true);
        String usuarios="Usuarios";
        canvas.drawText("", 3, 260, title);
        canvas.drawText(usuarios , 26, 260, title);
        canvas.drawText("Email", 196, 260, title);
        canvas.drawText("Teléfono", 396, 260, title);
        canvas.drawText("Móvil", 516, 260, title);
        canvas.drawText("Fecha de Alta", 606, 260, title);
        try {
            users = datosUser();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        title.setUnderlineText(false);
        title.setFakeBoldText(false);
        Integer yTable = 260;
        int numPag = (users.length()/18)+1;
        try {
            for (int i = 0; i < users.length(); i++){
                if(i>1){
                    if((17%i)==0 && numPag>0){
                        numPag= numPag-1;
                        //finalizamos la página
                        pdfDocument.finishPage(myPage);
                        //creo una nueva página
                        myPage = pdfDocument.startPage(mypageInfo);
                        canvas = myPage.getCanvas();
                        // Encabezado del pdf (logo, título y subtitulo)
                        canvas.drawBitmap(scaledbmp, 56, 40, paint);
                        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                        title.setTextSize(22);
                        title.setColor(ContextCompat.getColor(this, R.color.black));
                        canvas.drawText("HRControlAPP", 209, 100, title);
                        canvas.drawText("Reporte de Usuarios", 209, 80, title);

                        // damos formato a los textos.
                        title.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                        title.setColor(ContextCompat.getColor(this, R.color.black));
                        title.setTextSize(15);

                        // centramos el texto en la página
                        title.setTextAlign(Paint.Align.LEFT);
                        title.setUnderlineText(true);
                        title.setFakeBoldText(true);
                        usuarios="Usuarios";
                        canvas.drawText("", 3, 260, title);
                        canvas.drawText(usuarios , 26, 260, title);
                        canvas.drawText("Email", 196, 260, title);
                        canvas.drawText("Teléfono", 396, 260, title);
                        canvas.drawText("Móvil", 516, 260, title);
                        canvas.drawText("Fecha de Alta", 606, 260, title);
                        title.setUnderlineText(false);
                        title.setFakeBoldText(false);
                        yTable = 260;

                    }
                }
                yTable = yTable + 50;
                JSONObject object = users.getJSONObject(i);
                canvas.drawText(String.valueOf((i+1)), 3, yTable, title);
                if(object.has("name")){
                    canvas.drawText(object.getString("name") , 26, yTable, title);
                }
                if(object.has("email")){
                    canvas.drawText(object.getString("email") , 196, yTable, title);
                }
                if(object.has("tlfFijo")){
                    canvas.drawText(object.getString("tlfFijo") , 396, yTable, title);
                }
                if(object.has("tlfMovil")){
                    canvas.drawText(object.getString("tlfMovil") , 516, yTable, title);
                }
                if(object.has("fechaAlta")){
                    canvas.drawText(object.getString("fechaAlta") , 606, yTable, title);
                }
            }
            //finalizamos la página
            pdfDocument.finishPage(myPage);
        } catch (JSONException e) {
            e.printStackTrace();
        }



        // damos el nombre del archivo que vamos a generar

        String directorio = null;
        try{
            directorio = (Environment.getExternalStorageDirectory() + "/ReporteHRControl.pdf");
            file = new File(directorio);
        } catch (Exception e){
            e.printStackTrace();
        }

        try {
            pdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(Reportes.this, "PDF creado.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Finalizado el guardado cerrarmos el pdf
        pdfDocument.close();
    }

    private void mostrarPDF(){
        String url = "/storage/emulated/0/ReporteHRControl.pdf";
        File files = new File(url);
        files.setReadable(true, false);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri pdfUri = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", files);
        intent.setDataAndType(pdfUri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        List<ResolveInfo> resInfoList = getApplicationContext().getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            getApplicationContext().grantUriPermission(packageName, pdfUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        Intent in = Intent.createChooser(intent, "Open File");
        startActivity(in);
    }


    private boolean checkPermission() {
        // comprobamos los permisos de la app
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        // solicitamos permisos si no están otorgados
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //comprobamos si nos han dado permisos
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                //comprobamos si tenemos permiso de escritura y almacenamiento
                if (writeStorage && readStorage) {
                    //Toast.makeText(this, "Permisos concecidos..", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Revise los permisos de la aplicación.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
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
