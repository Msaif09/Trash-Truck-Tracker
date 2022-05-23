package com.saif.trashtrucktracker;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.saif.trashtrucktracker.databinding.ActivityUserMapBinding;

public class UserMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityUserMapBinding binding;

    private Geocoder geocoder;
    private static final String TAG = "MapsActivity";
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;

    String UserKey = "User Online";
    String DriverKey = "Driver Availibal";

    private DatabaseReference DriverLocationRef;

    Marker userLocationMarker;
    Marker driverLocationMarker;
    private Circle userLocationAccuracyCircle;

    private int ACESSES_LOCATION_REQUEST = 10001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUserMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        geocoder = new Geocoder(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(500);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        DriverLocationRef = FirebaseDatabase.getInstance().getReference().child(DriverKey);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.setMaxZoomPreference(21);
        mMap.setMinZoomPreference(14);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            enableUserLocation();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat
                        .requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACESSES_LOCATION_REQUEST);
            } else {
                ActivityCompat
                        .requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACESSES_LOCATION_REQUEST);

            }
        }

    }

    private void enableUserLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    LocationCallback locationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);

            if (mMap != null) {
                setUserLocationMarker(locationResult.getLastLocation());
                DriverLocationMarker();
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

    private void stopLocationUpdate() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);

    }

    private void setUserLocationMarker(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (userLocationMarker == null) {
            //Create A New Marker
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng).title("My Location");
            userLocationMarker = mMap.addMarker(markerOptions);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, mMap.getCameraPosition().zoom));
        } else {
            //Use Previously Created Marker
            userLocationMarker.setPosition(latLng);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, mMap.getCameraPosition().zoom));
        }
        String UserId = FirebaseAuth.getInstance().getCurrentUser().toString();
        DatabaseReference UserAvailibiltyRef = FirebaseDatabase.getInstance().getReference().child(UserKey);
        GeoFire geoFire = new GeoFire(UserAvailibiltyRef);
        geoFire.setLocation(UserKey, new GeoLocation(location.getLatitude(), location.getLongitude()));

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

    private void DriverLocationMarker() {
        String UserId = FirebaseAuth.getInstance().getCurrentUser().toString();
        GeoFire geoFireDriver = new GeoFire(DriverLocationRef);
        geoFireDriver.getLocation(DriverKey, new com.firebase.geofire.LocationCallback() {
            @Override
            public void onLocationResult(String key, GeoLocation location) {

                if (location!=null){
                    LatLng DriverLatlng=new LatLng(location.latitude,location.longitude);

                    if (driverLocationMarker==null){
                        //Create A New Marker
                        MarkerOptions markerOptions=new MarkerOptions();
                        markerOptions.position(DriverLatlng).title("Driver Location");
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon1));
                        markerOptions.rotation(5);
                        driverLocationMarker =  mMap.addMarker(markerOptions);
//                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(DriverLatlng,17));
                    }else {
                        //Use Previously Created Marker
                        driverLocationMarker.setPosition(DriverLatlng);
//                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DriverLatlng,17));
                    }

                }else {
                    Toast.makeText(getApplicationContext(),"No Driver Avalible", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(getApplicationContext(),databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
            startLocationUpdate();
        }else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACESSES_LOCATION_REQUEST);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdate();

        String UserId= FirebaseAuth.getInstance().getCurrentUser().toString();
        DatabaseReference UserAvailibiltyRef = FirebaseDatabase.getInstance().getReference().child(UserKey);
        GeoFire geoFireD = new GeoFire(UserAvailibiltyRef);
        geoFireD.removeLocation(UserKey);

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
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,18));
                mMap.addMarker(new MarkerOptions().position(latLng).title(latLng.toString()));
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if (requestCode==ACESSES_LOCATION_REQUEST){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                enableUserLocation();
                zoomToUserLocation();
            }else {

            }
        }

    }
}