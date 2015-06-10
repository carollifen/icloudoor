package com.icloudoor.clouddoor;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ShakeEventManager implements SensorEventListener {

    private float lastX;
    private float lastY;
    private float lastZ;
    private long lastUpdateTime;
    private static final int SPEED_SHRESHOLD = 45;
    private static final int UPTATE_INTERVAL_TIME = 50;

    private SensorManager sensorManager;

    private Sensor sensor;

    private OnShakeListener onShakeListener;

    private Context mContext;


    public ShakeEventManager(Context c) {

        mContext = c;
        start();
    }


    public void start() {

        sensorManager = (SensorManager) mContext
                .getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {

            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        if (sensor != null) {
            sensorManager.registerListener(this, sensor,
                    SensorManager.SENSOR_DELAY_GAME);
        }

    }


    public void stop() {
        sensorManager.unregisterListener(this);
    }


    public void setOnShakeListener(OnShakeListener listener) {
        onShakeListener = listener;
    }


    public void onSensorChanged(SensorEvent event) {

        long currentUpdateTime = System.currentTimeMillis();
        long timeInterval = currentUpdateTime - lastUpdateTime;
        if (timeInterval < UPTATE_INTERVAL_TIME) {
            return;
        }
        lastUpdateTime = currentUpdateTime;

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float deltaX = x - lastX;
        float deltaY = y - lastY;
        float deltaZ = z - lastZ;

        lastX = x;
        lastY = y;
        lastZ = z;

        double speed = (Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ
                * deltaZ) / timeInterval) * 100;
        if (speed >= SPEED_SHRESHOLD) {

            onShakeListener.onShake();
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    public interface OnShakeListener {
        public void onShake();
    }
}
