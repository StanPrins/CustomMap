package com.stan.createcustommap;

import android.Manifest;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerDragListener{
    private GoogleMap mMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Toast.makeText(MainActivity.this,
                "onMapClick:\n" + latLng.latitude + ":" +
                        latLng.longitude , Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Toast.makeText(MainActivity.this, "onMapLongClick :\n" +
                        latLng.latitude +":" + latLng.longitude,
                Toast.LENGTH_LONG).show();
        MarkerOptions markerOptions =
                new MarkerOptions().position(latLng).title(latLng.toString());
        markerOptions.draggable(true);
        mMap.addMarker(markerOptions);

    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        marker.setTitle(marker.getPosition().toString());
        marker.showInfoWindow();
        marker.setAlpha(0.5f);
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        marker.setTitle(marker.getPosition().toString());
        marker.showInfoWindow();
        marker.setAlpha(0.5f);
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        marker.setTitle(marker.getPosition().toString());
        marker.showInfoWindow();
        marker.setAlpha(1.0f);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET
                }, 10);

                return;
            }
        }
        actAfterPermissioned();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    actAfterPermissioned();
        }
    }

    public void actAfterPermissioned() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        mMap.setMyLocationEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerDragListener(this);
//        map:mapType = "satellite"
//        map:cameraBearing="112.5"
//        map:cameraTargetLat="40.725"
//        map:cameraTargetLng="150.922433"
//        map:cameraTilt="30"
//        map:cameraZoom="13"
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//        LatLng amsterdam = new LatLng(57.37344281, 4.89446);
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(amsterdam, 13));
//        mMap.addMarker(new MarkerOptions()
//                .title("Amsterdam")
//                .snippet("The capital in Netherlands.")
//                .position(amsterdam));
        LatLng latLng = new LatLng(-33.847094158830894, 151.063409820199);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
        mMap.addMarker(new MarkerOptions()
                .title("Sydney")
                .snippet(latLng.toString())
                .position(latLng));
    }

    private void addMarker(){
        if(mMap != null)
        {
            LinearLayout layout = new LinearLayout(MainActivity.this);
            layout.setLayoutParams(new ActionBar.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            layout.setOrientation(LinearLayout.VERTICAL);

            final EditText titleField = new EditText(MainActivity.this);
            titleField.setHint("Title");

            final  EditText latField = new EditText(MainActivity.this);
            latField.setHint("Latitude");
            latField.setInputType(InputType.TYPE_CLASS_NUMBER
                    | InputType.TYPE_NUMBER_FLAG_DECIMAL
                    | InputType.TYPE_NUMBER_FLAG_SIGNED);
            final EditText longField = new EditText(MainActivity.this);
            longField.setHint("Longitude");
            longField.setInputType(InputType.TYPE_CLASS_NUMBER
                    | InputType.TYPE_NUMBER_FLAG_DECIMAL
                    | InputType.TYPE_NUMBER_FLAG_SIGNED);

            layout.addView(titleField);
            layout.addView(latField);
            layout.addView(longField);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Add Marker");
            builder.setView(layout);
            AlertDialog alertDialog = builder.create();
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    boolean parsable = true;
                    Double lat =null, lon = null;
                    String strLat = latField.getText().toString();
                    String strLon = longField.getText().toString();
                    String strTitle = titleField.getText().toString();
                    try{
                        lat = Double.parseDouble(strLat);

                    }catch (NumberFormatException ex)
                    {
                        parsable = false;
                        Toast.makeText(MainActivity.this,
                                "Latitude does not contain a parsable double",
                                Toast.LENGTH_LONG).show();
                    }
                    try{
                        lon = Double.parseDouble(strLon);
                    }catch (NumberFormatException ex){
                        parsable =false;
                        Toast.makeText(MainActivity.this,
                                "Longitude does not contain a parsable double",
                                Toast.LENGTH_LONG);
                    }
                    if(parsable){
                        LatLng targetLatLng = new LatLng(lat, lon);
                        MarkerOptions markerOptions = new MarkerOptions().position(targetLatLng).title(strTitle);

                        markerOptions.draggable(true);
                        mMap.addMarker(markerOptions);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(targetLatLng));
                    }

                }
            });
            builder.setNegativeButton("Cancel", null);
            builder.show();
        }else {
            Toast.makeText(MainActivity.this, "Map not ready", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.menu_addmarkers:
                addMarker();
                return true;
            case R.id.maptypeHYBRID:
                if(mMap != null)
                {
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    return true;
                }

            case R.id.maptypeNORMAL:
                if(mMap != null)
                {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    return true;
                }
            case R.id.maptypeSATELITE:
                if(mMap != null)
                {
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    return true;
                }
            case R.id.maptypeTERRAIN:
                if(mMap != null)
                {
                    mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                    return true;
                }
            case R.id.maptypeNONE:
                if(mMap != null)
                {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                    return true;
                }
            case R.id.menu_legalnotices:
                String LicenseInfo = GoogleApiAvailability.getInstance()
                        .getOpenSourceSoftwareLicenseInfo(MainActivity.this);
                AlertDialog.Builder LicenseDialog = new AlertDialog.Builder(MainActivity.this);
                LicenseDialog.setTitle(R.string.menu_legalnotices);
                LicenseDialog.setMessage(LicenseInfo);
                LicenseDialog.show();
                return true;
            case R.id.menu_about:
                AlertDialog.Builder aboutDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                aboutDialogBuilder.setTitle(R.string.menu_about).setMessage("http://android-er.blogspot.com");
                aboutDialogBuilder.setPositiveButton("visit",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String url = "http://android-er.blogspot.com";
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(url));
                                startActivity(i);
                            }
                        });

                aboutDialogBuilder.setNegativeButton("Dismiss",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog aboutDialog = aboutDialogBuilder.create();
                aboutDialog.show();
                return true;


        }
        return super.onOptionsItemSelected(item);
    }



}
