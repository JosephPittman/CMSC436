package com.example.joey.collabart;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
//Base code provided by the Android Studio Google Maps template
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private Button mGallery, mPicture;
    private Criteria mCriteria;
    private Location currentBest;
    private File photo;
    private static final int CAMERA_REQUEST = 1888;
    //These values are since we are emulating, and not running on a device
    //If one were running this on an actual device, replace TEST_LAT and TEST_LONG in the code with currentBest.getlatitude and currentBest.getLongitude
    private final Double TEST_LAT = 38.989994, TEST_LONG = -76.936120;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mGallery = findViewById(R.id.gallerybutton);
        mGallery.bringToFront();
        mPicture = findViewById(R.id.picturebutton);
        mPicture.bringToFront();
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (currentBest == null || location.getAccuracy() <= currentBest.getAccuracy()) {
                    currentBest = location;
                    //TODO update display
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        mCriteria = new Criteria();
        mCriteria.setAccuracy(Criteria.ACCURACY_LOW);

        mGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (MapsActivity.this, MyActivity.class);
                intent.putExtra("Lat", TEST_LAT);
                intent.putExtra("Long", TEST_LONG);
                startActivity(intent);
            }
        });

        mPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(MapsActivity.this, CameraActivity.class);
                intent.putExtra("Lat", 38.989994);
                intent.putExtra("Long",-76.936120);
                startActivity(intent);*/
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                File photofile = null;
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "JPEG_" + timeStamp + "_";
                File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                try {
                    File image = File.createTempFile(
                            imageFileName,  /* prefix */
                            ".jpg",         /* suffix */
                            storageDir      /* directory */
                    );
                    photofile = image;
                    photo = photofile;
                }
                catch (Exception ex) {
                    System.out.println(ex);
                    // Error occurred while creating the File
                }
                Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), "com.example.android.fileprovider", photofile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });



        // Register for network location updates
        /*if (null != mLocationManager
                .getProvider(LocationManager.NETWORK_PROVIDER)) {

            int res1 = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            int rest2 = PackageManager.PERMISSION_GRANTED;
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                mLocationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, mLocationListener, null);
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 10.0f, mLocationListener);
            }
                //mLocationManager.requestLocationUpdates(
                 //       LocationManager.NETWORK_PROVIDER, 10000,
                  //      10.0f, mLocationListener);
        }*/

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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            //Bitmap photo =  (Bitmap) data.getExtras().get("data");
            Intent caption = new Intent(MapsActivity.this, CaptionActivity.class);
            //caption.putExtra("image", photo);
            caption.putExtra("Lat", TEST_LAT);
            caption.putExtra("Long", TEST_LONG);
            caption.putExtra("File", photo);
            startActivity(caption);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                //LatLng sydney = new LatLng(currentBest.getLatitude(), currentBest.getLongitude());
                LatLng CSIC = new LatLng(TEST_LAT, TEST_LONG);
                mMap.addMarker(new MarkerOptions().position(CSIC).title("Marker in CSIC"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CSIC, 14.0f));
            }
        }, 7000);
        // Add a marker in Sydney and move the camera

        //LatLng sydney = new LatLng(currentBest.getLatitude(), currentBest.getLongitude());
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
