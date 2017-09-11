package com.example.anna.testapp;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.animation.RotateAnimation;
import android.view.animation.Animation;
import android.Manifest;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.text.Text;

import java.util.Date;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private double ax, ay, az;
    private ImageView image;
    private TextView compassAngle;
    private Sensor compass;
    private float currentDegree = 0f;
    private FusedLocationProviderClient fusedLocationClient;
    private String _tag = "DebugLog";

    public final int COARSE_LOCATION_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        } else {
            updateLocationData();
        }
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

        image = (ImageView) findViewById(R.id.imageViewCompass);
        compassAngle = (TextView) findViewById(R.id.angle);
        compass = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        if (compass != null) {
            sensorManager.registerListener(this, compass, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void checkPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                ){//Can add more as per requirement

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                    COARSE_LOCATION_REQUEST);
        } else {
            updateLocationData();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case COARSE_LOCATION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateLocationData();

                }
                return;
            }

        }

    }

    private void updateLocationData() {
        try {
            Log.i(_tag, "1");
            Task<Location> task = fusedLocationClient.getLastLocation();
            Log.i(_tag, "1,5");
            OnSuccessListener<Location> listener = new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    Log.i(_tag, "2");
                    if (location != null) {
                        Log.i(_tag, "3");
                        TextView lat = (TextView)findViewById(R.id.latitude);
                        TextView lon = (TextView)findViewById(R.id.longitude);
                        TextView time = (TextView)findViewById(R.id.time);
                        lat.setText(String.valueOf(location.getLatitude()));
                        lon.setText(String.valueOf(location.getLongitude()));
                        Date last = new Date(location.getTime());
                        time.setText(last.toString());
                    }
                    Log.i(_tag, "4");
                }
            };
            Log.i(_tag, "4,5");
            task.addOnSuccessListener(this, listener);
            Log.i(_tag, "5");
        } catch (SecurityException ex) {
            Log.i(_tag, ex.getMessage());
        }
    }

    protected void createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            ax = event.values[0];
            TextView xaxis = (TextView) this.findViewById(R.id.xaxis);
            xaxis.setText(Math.round(ax * 1000.0) / 1000.0 + "");
            ay = event.values[1];
            TextView yaxis = (TextView) this.findViewById(R.id.yaxis);
            yaxis.setText(Math.round(ay * 1000.0) / 1000.0 + "");
            az = event.values[2];
            TextView zaxis = (TextView) this.findViewById(R.id.zaxis);
            zaxis.setText(Math.round(az * 1000.0) / 1000.0 + "");
        }
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            float degree = Math.round(event.values[0]);
            compassAngle.setText("Heading: " + Float.toString(degree) + " degrees");
            // create a rotation animation (reverse turn degree degrees)
            RotateAnimation ra = new RotateAnimation(currentDegree, degree, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            // how long the animation will take place
            ra.setDuration(210);
            // set the animation after the end of the reservation status
            ra.setFillAfter(true);
            // Start the animation
            image.startAnimation(ra);
            currentDegree = degree;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
