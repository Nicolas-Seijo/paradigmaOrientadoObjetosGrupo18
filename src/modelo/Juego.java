package modelo;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Clase principal del negocio.
 * Administra el submarino, los barcos, las cargas, el puntaje y el nivel.
 */
public class Juego {

    public static final int ANCHO_MAPA = 1000;
    public static final int PROFUNDIDAD_MIN_SUBMARINO = 300;
    public static final int PROFUNDIDAD_MAX_SUBMARINO = 800;

    private static final int MAX_BARCOS_ACTIVOS = 3;
    private static final int BARCOS_POR_NIVEL = 12;

    private static final double VELOCIDAD_INICIAL_BARCOS = 10.0;
    private static final double VELOCIDAD_INICIAL_CARGAS = 15.0;

    private Submarino submarino;
    private List<BarcoEnemigo> barcos;
    private List<CargaProfundidad> cargas;
    private List<int[]> explosionesRecientes;

    private int nivel;
    private double velocidadBarcos;
    private double velocidadCargas;
    private int puntaje;

    private int barcosGenerados;
    private int barcosQuePasaron;
    private int proximoPuntajeVidaExtra;

    private Random random;

    public Juego() {
        this.random = new Random();
        //this.barcos = new ArrayList<>();
        //this.cargas = new ArrayList<>();
    }

    /**
     * Inicializa el juego en estado base.
     */
    public void iniciar() {
        submarino = new Submarino(ANCHO_MAPA / 2, 500);
        barcos = new ArrayList<>();
        cargas = new ArrayList<>();
        explosionesRecientes = new ArrayList<>();

        nivel = 1;
        velocidadBarcos = VELOCIDAD_INICIAL_BARCOS;
        velocidadCargas = VELOCIDAD_INICIAL_CARGAS;
        puntaje = 0;

        barcosGenerados = 0;
        barcosQuePasaron = 0;
        proximoPuntajeVidaExtra = 500;
    }

    /**
     * Actualiza un paso del juego.
     */
    public void actualizarJuego() {
        if (estaTerminado()) {
            return;
        }

        explosionesRecientes.clear();

        generarBarcos();
        moverBarcosYLanzarCargas();
        moverCargasYProcesarExplosiones();
        verificarCambioDeNivel();
    }

    /**
     * Genera barcos enemigos si faltan barcos activos y todavía no se llegó a 12 en el nivel.
     */
    public void generarBarcos() {
        if (barcos.size() < MAX_BARCOS_ACTIVOS && barcosGenerados < BARCOS_POR_NIVEL) {

            if (random.nextDouble() < 0.05) {
                boolean entraPorIzquierda = random.nextBoolean();

                int posicionInicial;
                int direccion;

                if (entraPorIzquierda) {
                    posicionInicial = -30;
                    direccion = 1;
                } else {
                    posicionInicial = ANCHO_MAPA + 30;
                    direccion = -1;
                }

                // El barco tarda unos ticks antes de lanzar su primera carga
                int ticksParaLanzar = random.nextInt(6) + 3;

                BarcoEnemigo barco = new BarcoEnemigo(posicionInicial, direccion, velocidadBarcos, ticksParaLanzar);
                barcos.add(barco);
                barcosGenerados++;
            }
        }
    }

    /**
     * Mueve los barcos, actualiza sus temporizadores y lanza cargas si corresponde.
     */
    private void moverBarcosYLanzarCargas() {
        Iterator<BarcoEnemigo> iterator = barcos.iterator();

        while (iterator.hasNext()) {
            BarcoEnemigo barco = iterator.next();

            barco.mover();
            barco.actualizarTemporizador();

            if (barco.puedeLanzar()) {
                CargaProfundidad carga = barco.lanzarCarga(random, velocidadCargas, nivel);
                cargas.add(carga);
            }

            if (barco.salioDePantalla()) {
                iterator.remove();
                barcosQuePasaron++;
            }
        }
    }

    /**
     * Mueve las cargas y procesa las explosiones.
     */
    private void moverCargasYProcesarExplosiones() {
        Iterator<CargaProfundidad> iterator = cargas.iterator();

        while (iterator.hasNext()) {
            CargaProfundidad carga = iterator.next();

            carga.caer();

            if (carga.debeExplotar()) {
                procesarExplosion(carga);
                iterator.remove();
            }
        }
    }

    /**
     * Calcula la distancia entre la explosión y el submarino para determinar daño y puntaje.
     */
    private void procesarExplosion(CargaProfundidad carga) {
        explosionesRecientes.add(new int[]{carga.getPosX() + 10, carga.getProfundidadDetonacion() + 10});

        double dx = carga.getPosX() - submarino.getPosX();
        double dy = carga.getProfundidadDetonacion() - submarino.getPosY();

        double distancia = Math.sqrt(dx * dx + dy * dy);

        int puntosGanados = submarino.recibirImpacto(distancia);
        sumarPuntos(puntosGanados);
    }

    /**
     * Verifica si ya terminó el nivel actual.
     * Para pasar de nivel tienen que haber pasado los 12 barcos y no quedar cargas activas.
     */
    private void verificarCambioDeNivel() {
        boolean terminoNivel = barcosQuePasaron >= BARCOS_POR_NIVEL
                && barcos.isEmpty()
                && cargas.isEmpty();

        if (terminoNivel) {
            avanzarNivel();
        }
    }

    /**
     * Avanza al siguiente nivel.
     * Cada nivel aumenta 20% las velocidades y suma 200 puntos.
     */
    public void avanzarNivel() {
        nivel++;
        velocidadBarcos = velocidadBarcos * 1.2;
        velocidadCargas = velocidadCargas * 1.2;

        barcosGenerados = 0;
        barcosQuePasaron = 0;

        sumarPuntos(200);
    }

    /**
     * Suma puntos y agrega una vida extra cada vez que se alcanzan 500 puntos acumulados.
     */
    public void sumarPuntos(int puntos) {
        puntaje += puntos;

        while (puntaje >= proximoPuntajeVidaExtra) {
            submarino.agregarVida();
            proximoPuntajeVidaExtra += 500;
        }
    }

    /**
     * Indica si el juego terminó.
     */
    public boolean estaTerminado() {
        return submarino != null && submarino.getVidas() <= 0;
    }

    public Submarino getSubmarino() {
        return submarino;
    }

    public List<BarcoEnemigo> getBarcos() {
        return barcos;
    }

    public List<CargaProfundidad> getCargas() {
        return cargas;
    }

    public List<int[]> getExplosionesRecientes() {
        return explosionesRecientes;
    }

    public int getNivel() {
        return nivel;
    }

    public double getVelocidadBarcos() {
        return velocidadBarcos;
    }

    public double getVelocidadCargas() {
        return velocidadCargas;
    }

    public int getPuntaje() {
        return puntaje;
    }
}