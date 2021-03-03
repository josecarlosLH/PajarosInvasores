package com.example.pajarosinvasores;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import static com.example.pajarosinvasores.GameView.ratioPantallaX;
import static com.example.pajarosinvasores.GameView.ratioPantallaY;

public class Avioneta {

    int disparar = 0;
    int x, y, ancho, alto, contHelice = 0, contadorDisparos = 1;
    Bitmap avioneta1, avioneta2, disparo1, disparo2, disparo3, disparo4, disparo5, muerto;
    private final GameView gameView;

    Avioneta(GameView gameView, int pantallaY, Resources res) {
        this.gameView = gameView;

        //Obtenemos los dos sprites de la avioneta
        avioneta1 = BitmapFactory.decodeResource(res, R.drawable.avioneta1);
        avioneta2 = BitmapFactory.decodeResource(res, R.drawable.avioneta2);

        //Obtenemos el alto y ancho de los sprites (como tienen el mismo tamaño, he cogido el del primer sprite)
        ancho = avioneta1.getWidth();
        alto = avioneta1.getHeight();

        //Reducimos el alto y ancho de los sprites
        ancho /= 4;
        alto /= 4;

        //Los adaptamos al ratio de la pantalla del dispositivo
        ancho = (int) (ancho * ratioPantallaX);
        alto = (int) (alto * ratioPantallaY);

        //Creamos los sprites redimensionados
        avioneta1 = Bitmap.createScaledBitmap(avioneta1, ancho, alto, false);
        avioneta2 = Bitmap.createScaledBitmap(avioneta2, ancho, alto, false);

        disparo1 = BitmapFactory.decodeResource(res, R.drawable.disparo1);
        disparo2 = BitmapFactory.decodeResource(res, R.drawable.disparo2);
        disparo3 = BitmapFactory.decodeResource(res, R.drawable.disparo3);
        disparo4 = BitmapFactory.decodeResource(res, R.drawable.disparo4);
        disparo5 = BitmapFactory.decodeResource(res, R.drawable.disparo5);

        disparo1 = Bitmap.createScaledBitmap(disparo1, ancho, alto, false);
        disparo2 = Bitmap.createScaledBitmap(disparo2, ancho, alto, false);
        disparo3 = Bitmap.createScaledBitmap(disparo3, ancho, alto, false);
        disparo4 = Bitmap.createScaledBitmap(disparo4, ancho, alto, false);
        disparo5 = Bitmap.createScaledBitmap(disparo5, ancho, alto, false);

        muerto = BitmapFactory.decodeResource(res, R.drawable.muerto);
        muerto = Bitmap.createScaledBitmap(muerto, ancho, alto, false);

        //Asignamos una posición predeterminada a la avioneta
        y = pantallaY / 2;
        x = (int) (64 * ratioPantallaX);
    }

    Bitmap getAvioneta () {

        //Dependiendo del valor de disparar, mostraremos un sprite distinto de la avioneta disparando desde
        //el momento en el que lo inicia hasta que se apaga el cañón
        if (disparar != 0) {

            if (contadorDisparos == 1) {
                contadorDisparos++;
                return disparo1;
            }

            if (contadorDisparos == 2) {
                contadorDisparos++;
                return disparo2;
            }

            if (contadorDisparos == 3) {
                contadorDisparos++;
                return disparo3;
            }

            if (contadorDisparos == 4) {
                contadorDisparos++;
                return disparo4;
            }

            //Reiniciamos el contador de disparos, quitamos un disparo disponible y pintamos la bala
            contadorDisparos = 1;
            disparar--;
            gameView.nuevoDisparo();

            return disparo5;
        }

        //Vamos controlando el sprite de la avioneta para dar sensación de movimiento en la hélice.
        //Su cont. será igual a 0 ó 1, así que por cada dos refrescos, la imagen se actualizará dos veces.
        if (contHelice == 0) {
            contHelice++;
            return avioneta1;
        }
        contHelice--;

        return avioneta2;
    }

    //Definimos la hitbox de la avioneta
    Rect getCollisionShape () {
        return new Rect(x, y, x + ancho, y + alto);
    }

    Bitmap getMuerto() {
        return muerto;
    }

}
