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

    // N: Establece las variables de todo el juego
    // N: Dimensión del mapa
    public static final int ANCHO_MAPA = 1000;
    public static final int PROFUNDIDAD_MIN_SUBMARINO = 300;
    public static final int PROFUNDIDAD_MAX_SUBMARINO = 800;

    // N: Parámetros de los barcos
    private static final int MAX_BARCOS_ACTIVOS = 3;
    private static final int BARCOS_POR_NIVEL = 12;

    // N: Parámetros de velocidad
    private static final double VELOCIDAD_INICIAL_BARCOS = 10.0;
    private static final double VELOCIDAD_INICIAL_CARGAS = 15.0;

    // N: Declara al submarino, la lista de barcos y cargas para la clase, que después instanciará
    private Submarino submarino;
    private List<BarcoEnemigo> barcos;
    private List<CargaProfundidad> cargas;

    // N: Declara las variables de la propia clase juego
    private int nivel;
    private double velocidadBarcos;
    private double velocidadCargas;
    private int puntaje;

    private int barcosGenerados;
    private int barcosQuePasaron;
    private int proximoPuntajeVidaExtra;

    private Random random;

    // N: Constructor de juego, instanciando la variable random y las listas.
    public Juego() {
        this.random = new Random();
        this.barcos = new ArrayList<>();
        this.cargas = new ArrayList<>();
    }

    /**
     * Inicializa el juego en estado base.
     */
    // N: Inicializa el juego en su estado "base", instanciando las entidades.
    public void iniciar() {
        submarino = new Submarino(ANCHO_MAPA / 2, 500);
        barcos = new ArrayList<>();
        cargas = new ArrayList<>();

        // N: Además inicia las variables del juego en su estado inicial
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
    // N: Actualiza el juego, es la función principal que mueve todo
    public void actualizarJuego() {
        // N: Revisa que el juego no haya terminado primero
        if (estaTerminado()) {
            return;
        }

        // N: Si el juego está en marcha llama a las funciones que definen las mecánicas del juego
        generarBarcos(); // N: Genera los barcos si hay menos de 3
        moverBarcosYLanzarCargas(); // N: Mueve los barcos y lanza las cargas
        moverCargasYProcesarExplosiones(); // N: Mueve las cargas en pantalla y procesa las explosiones para asignar puntos y/o quitar vida
        verificarCambioDeNivel(); // N: Verifica el cambio de nivel para aumentar la velocidad
    }

    /**
     * Genera barcos enemigos si faltan barcos activos y todavía no se llegó a 12 en el nivel.
     */
    public void generarBarcos() {
        while (barcos.size() < MAX_BARCOS_ACTIVOS && barcosGenerados < BARCOS_POR_NIVEL) { // N: Mientras la cant. de barcos sea menor al máximo de activos por nivel
            boolean entraPorIzquierda = random.nextBoolean(); // N: Con esto define si entra o no por izquierda, con el random devuelve "True" o "False" siendo falso entrar por derecha

            // N: Declara las variables iniciales para después asignarlas según el valor booleano de entraPorIzquierda
            int posicionInicial;
            int direccion;

            // N: Si entra por izquierda su pos. inicial es -30 y su dirección es positiva (a la derecha)
            if (entraPorIzquierda) {
                posicionInicial = -30;
                direccion = 1;
            } else {
                // N: Caso contrario entra 30px más a la derecha que el ancho del mapa y su dirección es negativa (izquierda)
                posicionInicial = ANCHO_MAPA + 30;
                direccion = -1;
            }

            // El barco tarda unos ticks antes de lanzar su primera carga
            int ticksParaLanzar = random.nextInt(6) + 3;

            // N: Con toda la información definida se instancia el barco y se agrega a la lista
            BarcoEnemigo barco = new BarcoEnemigo(posicionInicial, direccion, velocidadBarcos, ticksParaLanzar);
            barcos.add(barco);
            barcosGenerados++; // N: También se lleva un conteo de barcos para no generar de más por nivel
        }
    }

    /**
     * Mueve los barcos, actualiza sus temporizadores y lanza cargas si corresponde.
     */
    private void moverBarcosYLanzarCargas() {
        Iterator<BarcoEnemigo> iterator = barcos.iterator(); // N: Instancia el Barco como un "Iterator" una clase para iterar sobre objetos elemento por elemento

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
