package com.example.pajarosinvasores;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private boolean estaMuteado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Ponemos el menú en pantalla completa
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        //Ponemos un listener en el botón jugar para iniciar la Activity del juego
        findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, GameActivity.class));
            }
        });

        TextView maximaPuntuacionTV = findViewById(R.id.maximaPuntuacionTV);

        //Creamos las preferencias y les asignamos una puntuación máxima por defecto (0)
        final SharedPreferences prefs = getSharedPreferences("juego", MODE_PRIVATE);
        maximaPuntuacionTV.setText(getString(R.string.max_puntuacion) + ": " + prefs.getInt("maxpunt", 0));

        estaMuteado = prefs.getBoolean("estaMuteado", false);

        final ImageView volumeCtrl = findViewById(R.id.volumeCtrl);

        //Cambiamos la imagen del icono dependiendo de si está muteado o no
        if (estaMuteado)
            volumeCtrl.setImageResource(R.drawable.ic_volume_off_black_24dp);
        else
            volumeCtrl.setImageResource(R.drawable.ic_volume_up_black_24dp);

        //En el onClick cambiaremos el estado del sonido dependiendo de su estado anterior
        volumeCtrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Con la línea de abajo invertimos el estado del mute. Si antes NO estaba muteado y
                //pulsamos, ahora estará muteado y le asignaremos su icono correspondiente.
                estaMuteado = !estaMuteado;
                if (estaMuteado)
                    volumeCtrl.setImageResource(R.drawable.ic_volume_off_black_24dp);
                else
                    volumeCtrl.setImageResource(R.drawable.ic_volume_up_black_24dp);

                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("estaMuteado", estaMuteado);
                editor.apply();

            }
        });
    }
}
