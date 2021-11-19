package com.example.compastest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements SensorEventListener {


    private ImageView compassImage;
    private float rotateDegree = 0f;
    private SensorManager sensorManager;
    TextView angle;
    TextView tvAccuracy;
    int azimuth = 0;

    Sensor rotationVector;
    Sensor magneticField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        angle = findViewById(R.id.tvAgree);
        compassImage = findViewById(R.id.imageView);
        tvAccuracy = findViewById(R.id.tvAccuracy);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        rotationVector = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (rotationVector != null)
            sensorManager.registerListener(this, rotationVector, SensorManager.SENSOR_DELAY_UI);
        if (magneticField != null)
            sensorManager.registerListener(this, magneticField, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }


    float[] matrixRotate  = new float[9];
    float[] resultVal = new float[3];
    float[] values = new float[3];
    float[] g = new float[4];
    List<Integer> azimuthsList = new ArrayList<>();

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {

            /*g = event.values.clone();

            double norm = Math.sqrt(g[0] * g[0] + g[1] * g[1] + g[2] * g[2] + g[3] * g[3]);

            double x = g[0] / norm;
            double y = g[1] / norm;
            double z = g[2] / norm;
            double w = g[3] / norm;

            double sinP = 2.0 * (w * x + y * z);
            double cosP = 1.0 - 2.0 * (x * x + y * y);
            double pitch = Math.atan2(sinP, cosP) * (180 / Math.PI);

            double sinT = 2.0 * (w * z - z * x);
            double tilt;
            if (Math.abs(sinT) >= 1)
                tilt = Math.copySign(Math.PI / 2, sinT) * (180 / Math.PI);
            else
                tilt = Math.asin(sinT) * (180 / Math.PI);

            double sinA = 2.0 * (w * z + x * y);
            double cosA = 1.0 - 2.0 * (y * y + z * z);
            double az = Math.atan2(sinA, cosA) * (180 / Math.PI);
            

            if (pitch > 40) Toast.makeText(this, "Телефон смотрит на вас", Toast.LENGTH_LONG).show();*/

            SensorManager.getRotationMatrixFromVector(matrixRotate, event.values.clone());
            SensorManager.remapCoordinateSystem(matrixRotate, SensorManager.AXIS_X, SensorManager.AXIS_Y, matrixRotate);
            SensorManager.getOrientation(matrixRotate, resultVal);

            azimuth = (int) Math.round(Math.toDegrees(resultVal[0]));

            if (azimuth < 0) azimuth += 360;

            azimuthsList.add(azimuth);

            int az = 0;

            if (azimuthsList.size() > n) {
                az = sma(azimuthsList);
            }

            angle.setText(String.valueOf(az));

            RotateAnimation rotateAnimation = new RotateAnimation(rotateDegree, -az,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setDuration(200);
            rotateAnimation.setFillAfter(true);
            compassImage.startAnimation(rotateAnimation);

            rotateDegree = -az;
        }

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {

            values = event.values.clone();

            int field = (int) Math.sqrt((values[0] * values[0]) + (values[1] * values[1]) + (values[2] * values[2]));

            /*if (field > 65 || field < 25) tvAccuracy.setText("Нужна калибровка");
            else tvAccuracy.setText("ok");*/

            tvAccuracy.setText(String.valueOf(field));
        }
    }

    int n = 10;
    private int sma(List<Integer> f) {
        int res = 0;
        for (int i = f.size() - 1; i >= f.size() - n; i--) {
            res += f.get(i);
        }

        return Math.round(res/n);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}