package com.example.anna.testapp;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Anna on 04-09-2017.
 */

public class AccelerometerActivity extends Activity implements SensorEventListener {

    private SensorManager sensorManager;
    private double ax, ay, az;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            ax = event.values[0];
            TextView xaxis = (TextView)this.findViewById(R.id.xaxis);
            xaxis.setText(Math.round(ax * 100.0) / 100.0 + "");
            ay = event.values[1];
            TextView yaxis = (TextView)this.findViewById(R.id.yaxis);
            yaxis.setText(Math.round(ay * 100.0) / 100.0 + "");
            az = event.values[2];
            TextView zaxis = (TextView)this.findViewById(R.id.zaxis);
            zaxis.setText(Math.round(ax * 100.0) / 100.0 + "");
        }
    }



}
