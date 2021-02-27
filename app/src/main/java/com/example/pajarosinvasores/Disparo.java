package com.example.pajarosinvasores;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import static com.example.pajarosinvasores.GameView.ratioPantallaX;
import static com.example.pajarosinvasores.GameView.ratioPantallaY;

public class Disparo {

    int x, y, ancho, altura;
    Bitmap disparo;

    Disparo(Resources res) {

        //Obtenemos el sprite de la bala
        disparo = BitmapFactory.decodeResource(res, R.drawable.disparo);

        //Obtenemos el ancho y alto del sprite
        ancho = disparo.getWidth();
        altura = disparo.getHeight();

        //Lo redimensionamos
        ancho /= 4;
        altura /= 4;

        ancho = (int) (ancho * ratioPantallaX);
        altura = (int) (altura * ratioPantallaY);

        //Lo creamos
        disparo = Bitmap.createScaledBitmap(disparo, ancho, altura, false);

    }

    //Definimos la hitbox del disparo
    Rect getCollisionShape () {
        return new Rect(x, y, x + ancho, y + altura);
    }

}
