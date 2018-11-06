package com.example1;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example1.Model.Organismo;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,GoogleMap.OnInfoWindowClickListener {

    ArrayList<Organismo> listPerson = new ArrayList<Organismo>();

    ImageButton newMarker ;
    private GoogleMap mMap;
    View mapView;
    String organismo;
    String familia;
    String descripcion;
    String cantidad;
    String fecha ;
    final String[] pos = new String[1];
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    Marker EventMarker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
         SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapView = mapFragment.getView();
        mapFragment.getMapAsync(this);

        inicializarFirebase();

        listPerson = new ArrayList<>();

        newMarker = (ImageButton) findViewById(R.id.btn_draw_State);

        newMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });

    }

    public void inicializarFirebase(){

        firebaseDatabase = FirebaseDatabase.getInstance();

        databaseReference = firebaseDatabase.getReference();

    }

    private void setMapLongClick(final GoogleMap map) {
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                final String snippet = String.format(Locale.getDefault(),
                        "%1$.5f,%2$.5f",
                        latLng.latitude,
                        latLng.longitude);

                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(MapsActivity.this);
                dialogo1.setTitle("Agregar Organismo");
                dialogo1.setMessage("¿Esta seguro que deseas agregar un organismo aquí?");
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        Intent x = new Intent(MapsActivity.this, agregar.class);
                        x.putExtra("location", snippet);
                        startActivity(x);
                    }
                });
                dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        Toast.makeText(MapsActivity.this, "Cancelado", Toast.LENGTH_SHORT).show();
                    }
                });
                dialogo1.show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Change the map type based on the user's selection.
        switch (item.getItemId()) {
            case R.id.normal_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case R.id.hybrid_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            case R.id.satellite_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;
            case R.id.terrain_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public String getLocation(){
        final double[] lat = new double[1];
        final double[] lang = new double[1];
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                lat[0] =location.getLatitude();
                lang[0] = location.getLongitude();

                pos[0] = String.valueOf(lat[0]) + ","+ String.valueOf(lang[0]);

                Intent i = new Intent(MapsActivity.this, agregar.class);
                i.putExtra("location", pos[0]);
                startActivity(i);
            }
        });
        return null;
    }

    public void moveMarker(){
        if (mapView != null &&
                mapView.findViewById(Integer.parseInt("1")) != null) {
            // Get the button view
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 30, 30);
        }
    }


    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(this, "Info window clicked",
                Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);
        moveMarker();

        databaseReference.child("Organismo").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final Map<String, String> value = (Map<String, String>)dataSnapshot.getValue();
                String getslat = value.get("lat");
                String getslong = value.get("lang");


                double getlat = Double.parseDouble(getslat);
                double getlong = Double.parseDouble(getslong);

                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(Marker marker) {
                        final Map<String, String> value = (Map<String, String>)marker.getTag();
                        final String titulo = value.get("nombre");
                        final String arg1 = value.get("familia");
                        final String arg2 = value.get("descripcion");
                        final String arg3 = value.get("cantidad");
                        final String arg4 = value.get("fecha");


                        View v = getLayoutInflater().inflate(R.layout.custom_infowindow, null);
                        final TextView nombre = (TextView) v.findViewById(R.id.organismo);
                        final TextView Familia = (TextView) v.findViewById(R.id.familia);
                        final TextView Descipcion = (TextView) v.findViewById(R.id.descipcion);
                        final TextView Cantidad = (TextView) v.findViewById(R.id.cantidad);
                        final TextView Fecha = (TextView) v.findViewById(R.id.fecha);
                        nombre.setText("Organismo: " + titulo);
                        Familia.setText("Familia: " + arg1);
                        Descipcion.setText("Descripción: " + arg2);
                        Cantidad.setText("Cantidad: " + arg3);
                        Fecha.setText("Fecha: " + arg4);
                        return v;
                    }
                    @Override
                    public View getInfoContents(Marker marker) {
                        return null;
                    }
                });
                EventMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(getlat, getlong)));
                EventMarker.setTag(value);
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        setMapLongClick(mMap);
    }

}
