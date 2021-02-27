package com.example.pajarosinvasores;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Fondo {

    int x = 0, y = 0;
    Bitmap fondo;

    Fondo(int pantallaX, int pantallaY, Resources res) {
        //Añadimos el fondo de pantalla
        fondo = BitmapFactory.decodeResource(res, R.drawable.fondo);
        //Redimensionamos el fondo para que ocupe toda la pantalla
        fondo = Bitmap.createScaledBitmap(fondo, pantallaX, pantallaY, false);
    }

}
