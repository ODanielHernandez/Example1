package com.example1;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example1.Model.Organismo;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.UUID;

public class agregar extends AppCompatActivity implements Serializable {

    Calendar calendario = Calendar.getInstance();
    EditText nomP, appP,correoP,passwordP,cantiO,fechaO;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    Button agregarUbi;
    private int MAP = 2;
    double lat;
    double lang;

    String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar);

        nomP = findViewById(R.id.txt_nombrePersona);
        appP = findViewById(R.id.txt_appPersona);
        correoP = findViewById(R.id.txt_correoPersona);
        passwordP = findViewById(R.id.txt_passwordPersona);
        cantiO = findViewById(R.id.txt_Cantidad);
        fechaO = findViewById(R.id.txt_Fecha);
        agregarUbi = findViewById(R.id.agregarUbi);

        location = getIntent().getExtras().getString("location");
        inicializarFirebase();

        fechaO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(agregar.this, date, calendario
                        .get(Calendar.YEAR), calendario.get(Calendar.MONTH),
                        calendario.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        agregarUbi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),InsertMapsActivity.class);
                startActivityForResult(intent, MAP);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_nuevo,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        location = getIntent().getExtras().getString("location");

        StringTokenizer token = new StringTokenizer(location,",");
        String lat = token.nextToken();
        String lang = token.nextToken();

        String nombre = nomP.getText().toString();
        String correo = correoP.getText().toString();
        String password = passwordP.getText().toString();
        String app = appP.getText().toString();
        String canti = cantiO.getText().toString();
        String fecha= fechaO.getText().toString();
        switch (item.getItemId()) {

            case R.id.icon_nuevo: {
                if (nombre.equals("") || correo.equals("") || password.equals("") || app.equals("") || canti.equals("")) {
                    validacion();

                } else {
                        Organismo objeto = new Organismo();
                        objeto.setUid(UUID.randomUUID().toString());
                        objeto.setNombre(nombre);
                        objeto.setDescripcion(app);
                        objeto.setFamilia(correo);
                        objeto.setLugar(password);
                        objeto.setCantidad(canti);
                        objeto.setFecha(fecha);
                        objeto.setLat(lat);
                        objeto.setLang(lang);
                        databaseReference.child("Organismo").child(objeto.getUid()).setValue(objeto);

                        Toast.makeText(this, "Agregado", Toast.LENGTH_LONG).show();


                        Intent i = new Intent(agregar.this, MapsActivity.class);
                        startActivity(i);
                }
                break;
            }
            case R.id.icon_cancelar: {
                Intent i = new Intent(agregar.this, MapsActivity.class);
                startActivity(i);
            }
            break;

        }
        return true;
    }



    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    private void limpiarCajas() {
        nomP.setText("");
        correoP.setText("");
        passwordP.setText("");
        appP.setText("");
        cantiO.setText("");
        fechaO.setText("");

    }


    private void validacion() {
        String nombre = nomP.getText().toString();
        String correo = correoP.getText().toString();
        String password = passwordP.getText().toString();
        String app = appP.getText().toString();
        String canti= cantiO.getText().toString();
        String fecha= fechaO.getText().toString();
        if (nombre.equals("")){
            nomP.setError("Required");
        }
        else if (app.equals("")){
            appP.setError("Required");
        }
        else if (correo.equals("")){
            correoP.setError("Required");
        }
        else if (password.equals("")){
            passwordP.setError("Required");
        }
        else if (canti.equals("")){
            passwordP.setError("Required");
        }
        else if (fecha.equals("")){
            passwordP.setError("Required");
        }
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            calendario.set(Calendar.YEAR, year);
            calendario.set(Calendar.MONTH, monthOfYear);
            calendario.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            actualizarInput();
        }

    };

    private void actualizarInput() {
        String formatoDeFecha = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(formatoDeFecha, Locale.US);

        fechaO.setText(sdf.format(calendario.getTime()));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK)
        {

        }

        if(requestCode == MAP)
        {
            lat = (double) data.getExtras().get("location");
        }
    }
}
