package com.java.micarro;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney1 = new LatLng(-0.1922741215331904, -78.49325701442152);
        LatLng sydney2 = new LatLng(-0.2693466688566604, -78.52778290425032);
        LatLng sydney3 = new LatLng(-0.2802190886015348, -78.5475547195924);

        //mMap.addMarker(new MarkerOptions().position(sydney1).title("Taller Chevrolet Vallejo Araujo"));


        mMap.addMarker(new MarkerOptions().position(sydney1).title(getString(R.string.textview_talleres_taller1)));
        mMap.addMarker(new MarkerOptions().position(sydney2).title(getString(R.string.textview_talleres_taller2)));
        mMap.addMarker(new MarkerOptions().position(sydney3).title(getString(R.string.textview_talleres_taller3)));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney1));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney2));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney3));
    }
}