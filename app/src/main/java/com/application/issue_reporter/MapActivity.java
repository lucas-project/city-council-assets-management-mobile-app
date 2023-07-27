package com.application.issue_reporter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {
    private final String[] PERMISSIONS_LOCATION = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
            , Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA};

    private final static int REQUEST_LOCATION = 100;
    private final static int REQUEST_CAMERA = 101;
    private final static int CHOOSE_REQUEST = 188;
    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private TextView locationinfo;
    private Location lastKnownLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationinfo = findViewById(R.id.locationinfo);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }
    LatLng latLngtemp ;
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.setMinZoomPreference(6.0f);
        map.setMaxZoomPreference(20.0f);

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                latLngtemp = latLng;
                type = 2;
                map.clear();
                map.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("Select this address")
                        .draggable(true)
                );
                map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                List<Address> addresses;
                Geocoder geocoder = new Geocoder(MapActivity.this, Locale.getDefault());
                try {
                    addresses = geocoder.getFromLocation( latLng.latitude,  latLng.longitude, 1);
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String zipCode = addresses.get(0).getPostalCode();
                    String country = addresses.get(0).getCountryCode();
                    locationinfo.setText(addresses.get(0).getAddressLine(0));
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        enableMyLocation();


    }



    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {

        PermissionUtils.onRequestMultiplePermissionsResult(this, PERMISSIONS_LOCATION, new PermissionUtils.OnPermissionListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onPermissionGranted() {
                map.setOnMyLocationButtonClickListener(MapActivity.this);
                map.setOnMyLocationClickListener(MapActivity.this);
                map.setMyLocationEnabled(true);
                // Construct a PlacesClient
                Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
                Places.createClient(MapActivity.this);

                // Construct a FusedLocationProviderClient.
                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapActivity.this);
                try {
                        Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                        locationResult.addOnCompleteListener(MapActivity.this, new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                if (task.isSuccessful()) {
                                    // Set the map's camera position to the current location of the device.
                                    lastKnownLocation = task.getResult();
                                    if (lastKnownLocation != null) {
                                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                                new LatLng(lastKnownLocation.getLatitude(),
                                                        lastKnownLocation.getLongitude()), 18.0f));
                                        List<Address> addresses;
                                        Geocoder geocoder = new Geocoder(MapActivity.this, Locale.getDefault());
                                        try {
                                            addresses = geocoder.getFromLocation(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), 1);
                                            String city = addresses.get(0).getLocality();
                                            String state = addresses.get(0).getAdminArea();
                                            String zipCode = addresses.get(0).getPostalCode();
                                            String country = addresses.get(0).getCountryCode();

                                            for (int i = 0; i < addresses.size(); i++) {
                                                Log.e("====>",addresses.get(i).getAddressLine(0));
                                            }

                                            locationinfo.setText(addresses.get(0).getAddressLine(0));
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } else {
                                    map.moveCamera(CameraUpdateFactory
                                            .newLatLngZoom(new LatLng(0.0,
                                                    0.0), 10f));
                                    map.getUiSettings().setMyLocationButtonEnabled(false);
                                }
                            }
                        });
                } catch (SecurityException e)  {
                    Log.e("Exception: %s", e.getMessage(), e);
                }
            }

            @Override
            public void onPermissionDenied(String... permission) {
                showNoticeDialog(REQUEST_LOCATION);
            }

            @Override
            public void alwaysDenied(String... permission) {
                PermissionUtils.goToAppSetting(MapActivity.this, "Permission");
            }


        });
    }
    private void showNoticeDialog(final int type) {
        final String temp = "Permission notice";
        String tips = null;
        if (type == REQUEST_LOCATION) {
            tips = String.format(temp, "Location");
        } else if (type == CHOOSE_REQUEST) {
            tips = String.format(temp, "EXTERNAL STORAGE");
        }else if (type == REQUEST_CAMERA) {
            tips = String.format(temp, "CAMERA");
        }
        if (!TextUtils.isEmpty(tips)) {
            new android.app.AlertDialog.Builder(MapActivity.this)
                    .setTitle(PermissionUtils.TITLE)
                    .setMessage(tips)
                    .setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (type == REQUEST_LOCATION) {
                                PermissionUtils.requestMultiplePermissions(MapActivity.this, PERMISSIONS_LOCATION, REQUEST_LOCATION);
                            } else if (type == CHOOSE_REQUEST) {
                                PermissionUtils.requestPermission(MapActivity.this, PermissionUtils.PERMISSION_SD, CHOOSE_REQUEST);
                            }else if (type == REQUEST_CAMERA) {
                                PermissionUtils.requestPermission(MapActivity.this, PermissionUtils.PERMISSION_SD, REQUEST_CAMERA);
                            }

                        }
                    }).setNegativeButton("cancel", null)
                    .show();
        }
    }


    private int type = 1;
    @Override
    public boolean onMyLocationButtonClick() {
        type = 1;
        Location myLocation = map.getMyLocation();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(),myLocation.getLongitude()),
                18.0f));
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    public void bac(View view) {
        finish();
    }





    public void doNext(View view) {
        SPUtils.put(this,"address",locationinfo.getText().toString());
        SPUtils.put(this,"lat",type ==1?lastKnownLocation.getLatitude()+"":latLngtemp.latitude+"");
        SPUtils.put(this,"lon",type ==1?lastKnownLocation.getLongitude()+"":latLngtemp.longitude+"");
        startActivity(new Intent(MapActivity.this,DistanceActivity.class));
    }
}