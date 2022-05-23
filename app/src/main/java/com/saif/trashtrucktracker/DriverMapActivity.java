package com.saif.trashtrucktracker;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.saif.trashtrucktracker.databinding.ActivityDriverMapBinding;

public class DriverMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityDriverMapBinding binding;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;

    private Marker DriverLocationMarker;
    Marker UserLocationMarker;

    private int ACESSES_LOCATIN_REQUEST=10001;

    private Circle userLocationAccuracyCircle;

    String DriverKey="Driver Availibal";
    String UserKey="User Online";

    DatabaseReference UserLocationRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDriverMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
        locationRequest=LocationRequest.create();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        UserLocationRef= FirebaseDatabase.getInstance().getReference().child(UserKey);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.setMaxZoomPreference(21);
        mMap.setMinZoomPreference(14);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){


        }else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},ACESSES_LOCATIN_REQUEST);
            }else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},ACESSES_LOCATIN_REQUEST);
            }
        }
    }
    LocationCallback locationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);

            if (mMap!=null){
                setDriverLocationMarker(locationResult.getLastLocation());
                UserLocationMarker();
            }

        }
    };

    private void startLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    private void stopLocationUpdate(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);

    }

    private void setDriverLocationMarker(Location location){
        LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());

        if (DriverLocationMarker==null){
            //Create A New Marker
            MarkerOptions markerOptions=new MarkerOptions();
            markerOptions.position(latLng).title("My Location");
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon1));
            markerOptions.rotation(location.getBearing());
//            markerOptions.anchor((float) 0.5,(float) 0.5);
            DriverLocationMarker=mMap.addMarker(markerOptions);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,mMap.getCameraPosition().zoom));
        }else {
            //Use Previously Created Marker
            DriverLocationMarker.setPosition(latLng);
            DriverLocationMarker.setRotation(location.getBearing());

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,mMap.getCameraPosition().zoom));
        }
        String UserId= FirebaseAuth.getInstance().getCurrentUser().toString();

        DatabaseReference DriverAvailibiltyRef = FirebaseDatabase.getInstance().getReference().child(DriverKey);
        GeoFire geoFire = new GeoFire(DriverAvailibiltyRef);
        geoFire.setLocation(DriverKey,new GeoLocation(location.getLatitude(),location.getLongitude()));

        if (userLocationAccuracyCircle==null){
            CircleOptions circleOptions = new CircleOptions();
            circleOptions.center(latLng);
            circleOptions.strokeWidth(0);
            circleOptions.strokeColor(Color.argb(0,0,0,0));
            circleOptions.fillColor(Color.argb(0,0,0,0));
            circleOptions.radius(0);
            userLocationAccuracyCircle=mMap.addCircle(circleOptions);
        }else {
            userLocationAccuracyCircle.setCenter(latLng);
            userLocationAccuracyCircle.setRadius(0);
        }
    }

    private void UserLocationMarker(){
        String UserId= FirebaseAuth.getInstance().getCurrentUser().toString();
        GeoFire geoFireDriver=new GeoFire(UserLocationRef);
        geoFireDriver.getLocation(UserKey, new com.firebase.geofire.LocationCallback() {
            @Override
            public void onLocationResult(String key, GeoLocation location) {
                if (location!=null){
                    LatLng DriverLatlng=new LatLng(location.latitude,location.longitude);

                    if (UserLocationMarker==null){
                        //Create A New Marker
                        MarkerOptions markerOptions=new MarkerOptions();
                        markerOptions.position(DriverLatlng).title("User Location");
                        UserLocationMarker = mMap.addMarker(markerOptions);
//                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(DriverLatlng,17));
                    }else {
                        //Use Previously Created Marker
                        UserLocationMarker.setPosition(DriverLatlng);
//                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DriverLatlng,17));
                    }

                }else {
                    Toast.makeText(getApplicationContext(),"No User Avalible", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if (requestCode==ACESSES_LOCATIN_REQUEST){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                enableUserLocation();
                zoomToUserLocation();
            }else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},ACESSES_LOCATIN_REQUEST);
                }else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},ACESSES_LOCATIN_REQUEST);
                }

            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
            startLocationUpdate();
        }else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACESSES_LOCATIN_REQUEST);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdate();
        String DriverId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference DriverAvailibiltyRef = FirebaseDatabase.getInstance().getReference().child(DriverKey);
        GeoFire geoFireU = new GeoFire(DriverAvailibiltyRef);
        geoFireU.removeLocation(DriverKey);

    }

    private void enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    private void zoomToUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));
            }
        });
    }
    }