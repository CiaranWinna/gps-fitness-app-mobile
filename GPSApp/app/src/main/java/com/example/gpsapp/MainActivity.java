package com.example.gpsapp;

/**
 * importing the modules needed for the project
 */

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;
import java.util.ArrayList;

/**
 * main activity class that extends the AppCompatActivity. This class will be used as the main loading screen and will allow the app
 * to start recording the various metrics
 */
public class MainActivity extends AppCompatActivity {

    /**
     * private variables used in the class
     */
    private TextView header, status, lat,lng;
    private Button start_button, stop_button;
    private LocationManager lm;
    private MainActivity thisActivity = this;
    //private ArrayList<Float> latitudes;
    //private ArrayList<Float> longitudes;
    private ArrayList<Float> lat_lng;
    private ArrayList<Float> speeds;
    private ArrayList<Double> altitudes;
    private double total_distance;
    private boolean start_is_clicked = false;
    //private int pointer = 0;

    /**
     * Method which is called on the creation of the activity, this method will:
     *  - Retrieve the views from the activity_main xml file
     *  - Generate a new location manager
     *  - Initialize the metrics arrays that will store the captured data
     *  - dictate the behaviour of the app once either the start or stop button is pressed
     * @param savedInstanceState
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * gathering the views from the xml file
         */
        header = findViewById(R.id.header);
        status = findViewById(R.id.status);
        start_button = findViewById(R.id.start_button);
        stop_button = findViewById(R.id.stop_button);
        lat = findViewById(R.id.lat);
        lng = findViewById(R.id.lng);
        /**
         * intializg the location manager which will be used to gather information such as longitude and latitude
         */
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        /**
         * intialing the arrays that will store the gathered data
         */
        lat_lng = new ArrayList<Float>();
        speeds = new ArrayList<Float>();
        altitudes = new ArrayList<Double>();


        /**
         * start button on click method will start recording the metrics
         */
        // add a click listener to the button
        start_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                /**
                 * informing the user that the app is recording
                 */
                if(start_is_clicked == false){
                    status.setText("Recording!");
                    // add in the location listener and zeroize the data
                    speeds.clear();
                    altitudes.clear();
                    lat_lng.clear();
                    total_distance = 0;
                    addLocationListener();
                    // set the variable that the user has clicked the button
                    start_is_clicked = true;
                }
                // if the user clicks the start button again before clicking the stop button
                else{
                    Toast.makeText(getApplicationContext(),"Already recording" ,Toast.LENGTH_LONG).show();
                }
            }
        });

        // add a click listener to the button
        stop_button.setOnClickListener(new View.OnClickListener() {
                // overridden method to handle a button click
                public void onClick(View v) {
                    // if a lat and lng entry has already been placed into the lat_lng array
                    if(lat_lng.size() > 1 || (start_is_clicked == true)) {
                        // set up the intent to the ReportActivity
                        Intent intent = new Intent(MainActivity.this, ReportActivity.class);
                        // set up a bundle to transfer the multiple datasets in the intent
                        Bundle mBundle = new Bundle();

                        // adding the total distance to the bundle
                        mBundle.putDouble("total_distance", total_distance);

                        // adding the recorded speeds to the bundle
                        float[] array = new float[speeds.size()];
                        for (int i = 0; i < speeds.size(); i++) {
                            array[i] = speeds.get(i);
                        }

                        // adding the speeds array to the bundle
                        mBundle.putFloatArray("speeds", array);

                        // adding the altitudes array to the bundle
                        double[] array_alt = new double[altitudes.size()];
                        for (int i = 0; i < altitudes.size(); i++) {
                            array_alt[i] = altitudes.get(i);
                        }
                        mBundle.putDoubleArray("altitudes", array_alt);

                        // adding the bundle to the intent
                        intent.putExtras(mBundle);

                        // launching the intent to the report activity
                        startActivityForResult(intent, 16);
                    }
                    // if the user has clicked on the stop button but the start button has not been pressed prior
                    else if(!start_is_clicked){
                        Toast.makeText(getApplicationContext(), "No data recorded!", Toast.LENGTH_LONG).show();
                    }
                }
        });
    }

    /**
     * Method which is used to collect metrics related to the app such as latitude, longitude, speed, distance. The method will also
     * place the data into their respective arrays which will be sent to the report activity through an intent. The method will also ask for permission
     * from the user to use their location and check if that permission is maintained through the app cycle.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void addLocationListener() {
        // checking if the user has given permission for the app to use the user's location, if not then the app will ask the user
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // request the required permission
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        // locaton manger which will gather the location of the user every second or if their new distance has changed more that 1m from the previous location check
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, new LocationListener() {

            /**
             * methed which is called when the user's location has changed. This method will update the relevent arrays that store the user's data.
             * @param location
             */
            @Override
            public void onLocationChanged(Location location) {
                // the location of the device has changed so update the
                // textviews to reflect this
                lat.setText("Latitude: " + (double) location.getLatitude());
                lng.setText("Longitude: " + (double) location.getLongitude());

                // If the user's location has not been recorded yet
                if(lat_lng.size() > 0){

                    // generate a location variable from the previous stored location
                    Location loc1 = new Location("");
                    // get the latitude and longitude of the previous location
                    loc1.setLatitude(lat_lng.get(0));
                    loc1.setLongitude(lat_lng.get(1));

                    // adding the new distance to the array
                    total_distance = total_distance + loc1.distanceTo(location);

                    //lat_lng.clear();

                    // adding the new location to the array
                    lat_lng.add((float)location.getLatitude());
                    lat_lng.add((float)location.getLongitude());

                    // adding the recorded speed to the array
                    speeds.add(location.getSpeed());
                    // adding the altitude to the array
                    altitudes.add(location.getAltitude());
                    // information the user of the current metrics
                    status.setText("Total distance: " + (double)total_distance + "m"+ " \nSpeed: " + location.getSpeed() + "km/h" + " \nAltitude: " + (double)location.getAltitude()+"m");
                }
                // if the location of the user has not been taken yet
                else if (lat_lng.size() == 0){
                    // adding the data to the arrays
                    lat_lng.add((float)location.getLatitude());
                    lat_lng.add((float)location.getLongitude());
                    speeds.add(location.getSpeed());
                }
            }

            /**
             * method called when the gps permission is is disabled, the method will ask the user to allow for gps tracking while using the app
             * @param provider
             */
            @Override
            public void onProviderDisabled(String provider) {
                // if GPS has been disabled then update the textviews to reflect
                // this
                if (provider == LocationManager.GPS_PROVIDER) {
                    status.setText("Please allow GPS Location tracking!");
                }
            }

            /**
             * method called to check if the permission has been granted in the manifest file for gps tracking. If not then the activity will request
             * permission from the user
             * @param provider
             */
            @Override
            public void onProviderEnabled(String provider) {
                if (provider == LocationManager.GPS_PROVIDER) {
                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // request the required permission
                        ActivityCompat.requestPermissions(thisActivity,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                        return;
                    }
                    // if there is a last known location then set it on the textviews
                    /*Location l = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (l != null) {
                        lat.setText("Latitude: " + l.getLatitude());
                        lng.setText("Longitude: " + l.getLongitude());
                    }*/
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
        });
    }

    /**
     * method called on the outcome of asking the user to track their location
     * @param requestCode code that indicates the outcome of the request
     * @param permissions array of permissions that the user accepted
     * @param grantResults array showing if the user granted permission to the request based on the int value
     */
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                    Log.i("onRequestPermissionsResult","premission was not granted");

                }
                return;
            }
            // other 'case' lines to check for other permissions this app might request
        }
    }

    // overridden method that will be called whenever an intent has been returned from
    // an activity that was started by this activity.
    protected void onActivityResult(int request, int result, Intent data) {

        // check the request code for the intent and if the result was ok. if both
        // are good then take a copy of the updated count variable
        if(request == 16 && result == RESULT_OK) {
            // clearing the stored data
            speeds.clear();
            total_distance = 0;
            altitudes.clear();
            lat_lng.clear();
            // changing the text of the status view
            status.setText("Press START to begin recording and press STOP to  see results:");
            // setting the start button back to false
            start_is_clicked = false;
            //
            lat.setText("Latitude:");
            lng.setText("Longitude:");
        }
    }
}

