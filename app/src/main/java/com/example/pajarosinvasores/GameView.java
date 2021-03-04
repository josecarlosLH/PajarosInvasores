package com.example.pajarosinvasores;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.view.MotionEvent;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//En esta clase controlamos all el contenido que se va a mostar en la pantalla durante el juego
public class GameView extends SurfaceView implements Runnable {

    private Thread thread;
    private boolean estaJugando, haPerdido = false;
    private final int pantallaY, pantallaX;
    private final int velocidadFondo = 10;;
    private int puntuacion = 0;
    //Con estas dos variables, controlamos que el juego vaya bien en pantallas con distintas resoluciones
    public static float ratioPantallaX, ratioPantallaY;
    private final Paint paint;
    private final Pajaro[] pajaros;
    private final SharedPreferences prefs;
    private final Random random;
    private final SoundPool soundPool;
    private final List<Disparo> disparos;
    private final int sonido;
    private final Avioneta avioneta;
    private final GameActivity gameActivity;
    private final MediaPlayer mediaPlayer;
    //Añadimos dos fondos para poder dar la sensación de movimiento
    private final Fondo fondo1, fondo2;

    public GameView(Context context, GameActivity gameActivity, int pantallaX, int pantallaY) {
        super(gameActivity);
        this.gameActivity = gameActivity;

        //Obtenemos las preferencias
        prefs = gameActivity.getSharedPreferences("juego", Context.MODE_PRIVATE);

        //Cargamos el sonido al disparar de una forma u otra dependiendo de la versión de Android
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(audioAttributes)
                    .build();

        } else
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);

        sonido = soundPool.load(gameActivity, R.raw.shoot, 1);

        //Cargamos la música mientras estamos jugando
        mediaPlayer = MediaPlayer.create(context, R.raw.fived);
        if (!prefs.getBoolean("estaMuteado", false)) {
            mediaPlayer.start();
            mediaPlayer.setLooping(true);
        }

        this.pantallaX = pantallaX;
        this.pantallaY = pantallaY;
        //He puesto el ratio de mi pantalla como referencia
        ratioPantallaX = 2340f / pantallaX;
        ratioPantallaY = 1080f / pantallaY;

        fondo1 = new Fondo(pantallaX, pantallaY, getResources());
        fondo2 = new Fondo(pantallaX, pantallaY, getResources());
        //Posicionamos el segundo fondo (no visible desde primer momento) justo cuando termine la posición del eje X de la pantalla
        fondo2.x = pantallaX;

        //Dibujamos la avioneta
        avioneta = new Avioneta(this, pantallaY, getResources());

        disparos = new ArrayList<>();

        //Creamos el paint para dibujar el número de puntuación
        paint = new Paint();
        paint.setTextSize(128);
        paint.setColor(Color.RED);

        //Inicializamos el array de pájaros a 4 (número máximo de pájaros en pantalla)
        pajaros = new Pajaro[4];

        //Llenamos la lista de pajaros con la clase Pajaro
        int i;
        for (i = 0; i < 4; i++) {
            Pajaro pajaro = new Pajaro(getResources());
            pajaros[i] = pajaro;
        }

        random = new Random();
    }

    @Override
    public void run() {
        while (estaJugando) {
            refrescarPantalla();
            dibujar();
            sleep();
        }
    }

    //Aquí definimos el movimiento/posición de los sprites para refrescar la pantalla
    private void refrescarPantalla() {
        //Por cada llamada al método, iremos avanzando 10 posiciones en el eje X, por lo que los fondos se moverán 10 posiciones a la izquierda
        fondo1.x -= velocidadFondo;
        fondo2.x -= velocidadFondo;

        //Controlamos si el fondo 1 sigue mostrándose en pantalla o no. En caso de que no esté, lo posicionamos al final del eje X de la pantalla
        if (fondo1.x + fondo1.fondo.getWidth() < 0) {
            fondo1.x = pantallaX;
        }

        //Lo mismo de arriba con el fondo 2
        if (fondo2.x + fondo2.fondo.getWidth() < 0) {
            fondo2.x = pantallaX;
        }

        //Definimos el movimiento de la avioneta con los valores del acelerómetro
        //IMPORTANTE: Los ejes del sensor están invertidos porque el juego, al estar en horizontal, el eje X de vertical pasa a ser el Y en horizontal.
        avioneta.x = (int) (avioneta.x + (gameActivity.sensorY * 10));
        avioneta.y = (int) (avioneta.y + (gameActivity.sensorX * 10));

        //Controlamos los límites de los bordes
        if (avioneta.y < 0) //borde superior
            avioneta.y = 0;

        if (avioneta.y >= pantallaY - avioneta.alto) //borde inferior
            avioneta.y = pantallaY - avioneta.alto;

        if (avioneta.x < 0)  //borde izquierdo
            avioneta.x = 0;

        if (avioneta.x >= pantallaX - avioneta.ancho)  //borde derecho
            avioneta.x = pantallaX - avioneta.ancho;

        //Creamos un ArrayList de basura en el que almacenaremos los disparos
        List<Disparo> basura = new ArrayList<>();

        for (Disparo disparo : disparos) {
            //Si el disparo no alcanza a ningún objetivo, lo añadimos a la basura
            if (disparo.x > pantallaX)
                basura.add(disparo);

            disparo.x += 50 * ratioPantallaX;

            for (Pajaro pajaro : pajaros) {
                //Si la bala toca al pájaro
                if (Rect.intersects(pajaro.getCollisionShape(), disparo.getCollisionShape())) {
                    //Añadimos la puntuación
                    puntuacion++;
                    //Ponemos al pájaro fuera de la pantalla
                    pajaro.x = -10000;
                    //Posicionamos al disparo fuera de la pantalla para que pueda ser recolectado por nuestra lista de basura
                    disparo.x = pantallaX + 500;
                    pajaro.esDisparado = true;
                }
            }
        }

        //Vaciamos la lista de basura
        for (Disparo disparo : basura)
            disparos.remove(disparo);

        //Recorremos la lista de pajaros
        for (Pajaro pajaro : pajaros) {

            //Hacemos que el pájaro se mueva hacia la izquierda con la velocidad indicada en la clase de Pajaro (por defecto es 20)
            pajaro.x -= pajaro.velocidad;

            //Esta condición comprueba si el pájaro ha salido por el lado izquierdo de la pantalla
            if (pajaro.x + pajaro.ancho < 0) {
                //Comprobamos si el pájaro está vivo
                if (!pajaro.esDisparado) {
                    //Como está vivo, perdemos la partida
                    haPerdido = true;
                    return;
                }

                //Si el pájaro sale de la pantalla, incrementamos su velocidad de forma aleatoria (lo estaríamos "reviviendo")
                if (!prefs.getBoolean("esDificil", false)) {
                    int limite = (int) (20 * ratioPantallaX);
                    pajaro.velocidad = random.nextInt(limite);

                    //Establecemos una velocidad mínima a la que irá el pájaro
                    if (pajaro.velocidad < 20 * ratioPantallaX)
                        pajaro.velocidad = (int) (20 * ratioPantallaX);
                } else {
                    int limite = (int) (30 * ratioPantallaX);
                    pajaro.velocidad = random.nextInt(limite);

                    //Establecemos una velocidad mínima a la que irá el pájaro
                    if (pajaro.velocidad < 30 * ratioPantallaX)
                        pajaro.velocidad = (int) (30 * ratioPantallaX);
                }


                //Posicionamos al pájaro al fondo de la pantalla en el eje X
                pajaro.x = pantallaX;
                //Posicionamos al pájaro en una posición aleatoria en el eje Y
                pajaro.y = random.nextInt(pantallaY - pajaro.altura);

                pajaro.esDisparado = false;
            }

            //Si la avioneta choca contra un pájaro, perderemos
            if (Rect.intersects(pajaro.getCollisionShape(), avioneta.getCollisionShape())) {
                haPerdido = true;
                return;
            }
        }

    }

    //Es importante el orden en el que dibujamos los bitmap porque si dibujásemos el fondo lo último,
    //el resto de sprites quedarían superpuestos por éste.
    private void dibujar() {

        //Comprobamos si el Canvas sobre el que dibujamos es válido
        if (getHolder().getSurface().isValid()) {

            //Obtenemos el Canvas y dibujamos sobre él justo en el instante que lo obtenemos
            Canvas canvas = getHolder().lockCanvas();
            //Dibujamos los fondos
            canvas.drawBitmap(fondo1.fondo, fondo1.x, fondo1.y, paint);
            canvas.drawBitmap(fondo2.fondo, fondo2.x, fondo2.y, paint);

            //Dibujamos los pájaros
            for (Pajaro pajaro : pajaros)
                canvas.drawBitmap(pajaro.getPajaro(), pajaro.x, pajaro.y, paint);

            //Dibujamos la puntuación
            canvas.drawText(puntuacion + "", pantallaX / 2f, 164, paint);

            //Si el jugado ha perdido:
            if (haPerdido) {
                //Pausamos el thread del juego
                estaJugando = false;
                //Dibujamos el sprite de la avioneta estrellada
                canvas.drawBitmap(avioneta.getMuerto(), avioneta.x, avioneta.y, paint);
                //Lo dibujamos en el canvas
                getHolder().unlockCanvasAndPost(canvas);
                //Comprobamos si la puntuación es la nueva puntuación máxima
                comprobarPuntuacion();
                //Pausamos la música
                mediaPlayer.pause();
                //Salimos al menú
                esperarAntesDeSalir();
                return;
            }

            //Dibujamos la avioneta
            canvas.drawBitmap(avioneta.getAvioneta(), avioneta.x, avioneta.y, paint);

            for (Disparo disparo : disparos)
                canvas.drawBitmap(disparo.disparo, disparo.x, disparo.y, paint);

            //Desbloqueamos el Canvas del SurfaceView y le pasamos el Canvas que acabamos de pintar
            getHolder().unlockCanvasAndPost(canvas);

        }

    }

    private void esperarAntesDeSalir() {
        try {
            //Pausamos el hilo del juego 3 segundos
            Thread.sleep(3000);
            //Llevamos al usuario al menú principal
            gameActivity.startActivity(new Intent(gameActivity, MainActivity.class));
            //Cerramos la actividad del juego
            gameActivity.finish();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void comprobarPuntuacion() {
        //Guardamos la puntuación si es mayor a la máxima puntuación actual
        if (prefs.getInt("maxpunt", 0) < puntuacion) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("maxpunt", puntuacion);
            editor.apply();
        }
    }

    //1000 milisegundos / 17 milisegundos ≈ 60 refrescos tendrá la pantalla por segundo para actualizar la posición de los sprites del juego
    private void sleep () {
        try {
            Thread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //Llamaremos a este método cuando empecemos o volvamos a abrir el juego
    public void resume () {
        //Creamos el hilo, el cual ejecutará el método run() y cambiamos el boolean
        estaJugando = true;
        thread = new Thread(this);
        thread.start();
        if (!prefs.getBoolean("estaMuteado", false)) {
            mediaPlayer.start();
            mediaPlayer.setLooping(true);
        }
    }

    //Este método pausará el juego
    public void pause () {
        //Pausamos el hilo y cambiamos el boolean
        try {
            estaJugando = false;
            mediaPlayer.pause();
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    //Cuando pulsemos la pantalla, dispararemos
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            avioneta.disparar++;
        }
        return true;
    }

    public void nuevoDisparo() {

        if (!prefs.getBoolean("estaMuteado", false))
            soundPool.play(sonido, 1, 1, 0, 0, 1);

        Disparo disparo = new Disparo(getResources());
        disparo.x = avioneta.x + avioneta.ancho;
        disparo.y = avioneta.y + (avioneta.alto / 2);
        disparos.add(disparo);

    }
}
