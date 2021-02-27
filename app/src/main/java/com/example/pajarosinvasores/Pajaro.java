package com.example.pajarosinvasores;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import static com.example.pajarosinvasores.GameView.ratioPantallaX;
import static com.example.pajarosinvasores.GameView.ratioPantallaY;

public class Pajaro {

    public int velocidad = 20;
    public boolean esDisparado = true;
    int x = 0, y, ancho, altura, contadorPajaros = 1;
    Bitmap pajaro1, pajaro2, pajaro3, pajaro4;

    Pajaro(Resources res) {

        //Obtenemos los sprites de un pájaro (todos van a tener el mismo aspecto)
        pajaro1 = BitmapFactory.decodeResource(res, R.drawable.pajaro1);
        pajaro2 = BitmapFactory.decodeResource(res, R.drawable.pajaro2);
        pajaro3 = BitmapFactory.decodeResource(res, R.drawable.pajaro3);
        pajaro4 = BitmapFactory.decodeResource(res, R.drawable.pajaro4);

        //Obtenemos el alto y ancho de los sprites (como tienen el mismo tamaño, he cogido el del primer sprite)
        ancho = pajaro1.getWidth();
        altura = pajaro1.getHeight();

        ancho /= 6;
        altura /= 6;

        ancho = (int) (ancho * ratioPantallaX);
        altura = (int) (altura * ratioPantallaY);

        pajaro1 = Bitmap.createScaledBitmap(pajaro1, ancho, altura, false);
        pajaro2 = Bitmap.createScaledBitmap(pajaro2, ancho, altura, false);
        pajaro3 = Bitmap.createScaledBitmap(pajaro3, ancho, altura, false);
        pajaro4 = Bitmap.createScaledBitmap(pajaro4, ancho, altura, false);

        //Los pájaros serán creados fuera de la pantalla justo al comenzar el juegi
        y = -altura;
    }

    Bitmap getPajaro () {

        //Creamos una secuencia con el contador para animar el movimiento de los pájaros
        //Es igual a lo que hicimos cuando la avioneta disparaba una bala
        if (contadorPajaros == 1) {
            contadorPajaros++;
            return pajaro1;
        }

        if (contadorPajaros == 2) {
            contadorPajaros++;
            return pajaro2;
        }

        if (contadorPajaros == 3) {
            contadorPajaros++;
            return pajaro3;
        }

        contadorPajaros = 1;

        return pajaro4;
    }

    //Definimos la hitbox del pájaro
    Rect getCollisionShape () {
        return new Rect(x, y, x + ancho, y + altura);
    }

}
