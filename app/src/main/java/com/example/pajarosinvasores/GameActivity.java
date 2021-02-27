package com.example.pajarosinvasores;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.util.List;


public class GameActivity extends AppCompatActivity implements SensorEventListener {

    private GameView gameView;
    public float sensorX, sensorY;
    private SensorManager sensorManager;
    private Sensor acelerometro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Ponemos el juego en pantalla completa
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Obtenemos la resolución de la pantalla
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        //Se la pasamos al GameView para que determine el tamaño del Canvas sobre el que vamos a dibujar
        gameView = new GameView(this, this, point.x, point.y);
        setContentView(gameView);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        //Inicializamos el sensor
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        acelerometro = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, acelerometro,
                SensorManager.SENSOR_DELAY_GAME);
        gameView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        gameView.pause();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        synchronized (this) {
            //Se recogen los valores del acelerómetro
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                sensorX = event.values[0];
                //Se controlan las pequeñas variaciones del acelerómetro para que cuando el dispositivo esté encima de la mesa, la avioneta no se mueva
                if (sensorX >= -0.4 && sensorX <= 0.4) {
                    sensorX = 0;
                }

                sensorY = event.values[1];
                //Se controlan las pequeñas variaciones del acelerómetro para que cuando el dispositivo esté encima de la mesa, la bola no se mueva
                if (sensorY >= -0.4 && sensorY <= 0.4) {
                    sensorY = 0;
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }
}
