/* 
 * Sensor detection code based on:
 * http://stackoverflow.com/a/1016941/555544
 */

package com.usc.ss12.falldetectionapp;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    
    private Display mDisplay;
    private final int MAX_RECORDS = 30;
    private int numRecords = 0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mDisplay = getWindowManager().getDefaultDisplay();
    }

    public MainActivity() {
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    	
    }

    public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
			return;
		float mSensorX, mSensorY, mSensorZ;
		
		mSensorX = event.values[0];
		mSensorY = event.values[1];
		mSensorZ = event.values[2];
		
		float accelValue = (mSensorX*mSensorX + mSensorY*mSensorY + mSensorZ*mSensorZ)
				/ (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
		
		TextView tv = (TextView) findViewById(R.id.accelerometer_values);
		if(numRecords >= MAX_RECORDS) {
			tv.setText("");
			numRecords = 0;
		}
		
		tv.setText(tv.getText() + "\n" + mSensorX + ' ' + mSensorY + ' ' + mSensorZ);
		numRecords++;
    }
}
