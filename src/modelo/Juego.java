package modelo; // Paquete "modelo": clases de la lógica del juego.

import java.util.ArrayList; // Lista dinámica, para guardar barcos y cargas.
import java.util.Iterator;  // Permite recorrer una lista y eliminar elementos mientras se recorre.
import java.util.List;      // Tipo "lista" (interfaz) que implementa ArrayList.
import java.util.Random;    // Generador de números aleatorios.

/**
 * Clase principal del negocio.
 * Administra el submarino, los barcos, las cargas, el puntaje y el nivel.
 */
public class Juego { // Declara la clase pública Juego (el "motor" del juego).

    public static final int ANCHO_MAPA = 1000;             // Ancho del escenario (límite horizontal).
    public static final int PROFUNDIDAD_MIN_SUBMARINO = 300; // Profundidad mínima a la que puede ir el submarino.
    public static final int PROFUNDIDAD_MAX_SUBMARINO = 800; // Profundidad máxima a la que puede ir el submarino.

    private static final int MAX_BARCOS_ACTIVOS = 3;   // Máximo de barcos en pantalla a la vez.
    private static final int BARCOS_POR_NIVEL = 12;    // Cantidad de barcos que forman una serie (un nivel).

    /** Distancia mínima que debe recorrer el último barco antes de soltar el siguiente. */
    private static final int SEPARACION_MINIMA_BARCOS = 120; // Separación para que los barcos no se superpongan.

    private static final double VELOCIDAD_INICIAL_BARCOS = 10.0; // Velocidad de los barcos en el nivel 1.
    private static final double VELOCIDAD_INICIAL_CARGAS = 15.0; // Velocidad de caída de las cargas en el nivel 1.

    private Submarino submarino;            // El submarino del jugador.
    private List<BarcoEnemigo> barcos;      // Lista de barcos enemigos activos.
    private List<CargaProfundidad> cargas;  // Lista de cargas de profundidad activas.

    private int nivel;             // Nivel actual.
    private double velocidadBarcos; // Velocidad actual de los barcos (aumenta por nivel).
    private double velocidadCargas; // Velocidad actual de las cargas (aumenta por nivel).
    private int puntaje;           // Puntaje acumulado del jugador.

    private int barcosGenerados;        // Cuántos barcos se crearon en el nivel actual.
    private int barcosQuePasaron;       // Cuántos barcos ya salieron de pantalla en el nivel actual.
    private int proximoPuntajeVidaExtra; // Puntaje al que se otorga la próxima vida extra.

    /** Último barco que se soltó; se usa para mantener la separación entre barcos. */
    private BarcoEnemigo ultimoBarcoGenerado; // Referencia al barco generado más recientemente.

    private Random random; // Generador aleatorio usado en todo el juego.

    // Constructor: prepara las estructuras básicas (todavía sin empezar la partida).
    public Juego() {
        this.random = new Random();      // Crea el generador de aleatorios.
        this.barcos = new ArrayList<>(); // Inicializa la lista de barcos vacía.
        this.cargas = new ArrayList<>(); // Inicializa la lista de cargas vacía.
    }

    /**
     * Inicializa el juego en estado base.
     */
    public void iniciar() {
        submarino = new Submarino(ANCHO_MAPA / 2, 500); // Crea el submarino en el centro horizontal y a 500 de profundidad.
        barcos = new ArrayList<>();                     // Vacía/recrea la lista de barcos.
        cargas = new ArrayList<>();                     // Vacía/recrea la lista de cargas.

        nivel = 1;                                      // Empieza en el nivel 1.
        velocidadBarcos = VELOCIDAD_INICIAL_BARCOS;     // Velocidad inicial de barcos.
        velocidadCargas = VELOCIDAD_INICIAL_CARGAS;     // Velocidad inicial de cargas.
        puntaje = 0;                                    // Puntaje en 0.

        barcosGenerados = 0;                            // Aún no se generó ningún barco.
        barcosQuePasaron = 0;                           // Aún no pasó ningún barco.
        proximoPuntajeVidaExtra = 500;                  // La primera vida extra se da a los 500 puntos.
        ultimoBarcoGenerado = null;                     // Todavía no hay un "último barco".
    }

    /**
     * Actualiza un paso del juego.
     */
    public void actualizarJuego() {
        if (estaTerminado()) {                  // Si el juego terminó (sin vidas)...
            return;                             // ...no hace nada más.
        }

        generarBarcos();                        // Intenta soltar un barco nuevo si corresponde.
        moverBarcosYLanzarCargas();             // Mueve los barcos y lanza cargas.
        moverCargasYProcesarExplosiones();      // Hace caer las cargas y resuelve las explosiones.
        verificarCambioDeNivel();               // Revisa si hay que pasar de nivel.
    }

    /**
     * Genera barcos enemigos de a uno por vez.
     * Solo suelta un barco nuevo si hay menos de 3 activos, todavía no se llegó a 12 en el
     * nivel, y el último barco ya avanzó lo suficiente como para que no se superpongan.
     */
    public void generarBarcos() {
        if (barcos.size() >= MAX_BARCOS_ACTIVOS || barcosGenerados >= BARCOS_POR_NIVEL) { // Si ya hay 3 activos o ya se crearon los 12...
            return;                                                                       // ...no genera nada.
        }

        if (!hayEspacioParaNuevoBarco()) {      // Si el último barco todavía está muy cerca del borde...
            return;                             // ...espera (no suelta otro encima).
        }

        boolean entraPorIzquierda = random.nextBoolean(); // Elige al azar si entra por la izquierda o la derecha.

        int posicionInicial; // Dónde aparece el barco.
        int direccion;       // Hacia dónde se mueve.

        if (entraPorIzquierda) {            // Si entra por la izquierda...
            posicionInicial = -30;          // ...aparece apenas fuera del borde izquierdo...
            direccion = 1;                  // ...y se mueve hacia la derecha.
        } else {                            // Si entra por la derecha...
            posicionInicial = ANCHO_MAPA + 30; // ...aparece apenas fuera del borde derecho...
            direccion = -1;                 // ...y se mueve hacia la izquierda.
        }

        // El barco tarda unos ticks antes de lanzar su primera carga
        int ticksParaLanzar = random.nextInt(6) + 3; // Entre 3 y 8 ticks de espera para el primer lanzamiento.

        BarcoEnemigo barco = new BarcoEnemigo(posicionInicial, direccion, velocidadBarcos, ticksParaLanzar); // Crea el barco.
        barcos.add(barco);              // Lo agrega a la lista de barcos activos.
        barcosGenerados++;              // Cuenta un barco más generado en el nivel.
        ultimoBarcoGenerado = barco;    // Recuerda este barco como el último generado.
    }

    /**
     * Indica si el último barco generado ya se alejó lo suficiente del borde
     * como para soltar el siguiente sin que se superpongan.
     */
    private boolean hayEspacioParaNuevoBarco() {
        if (ultimoBarcoGenerado == null) {    // Si todavía no se generó ningún barco...
            return true;                      // ...hay espacio (se puede soltar el primero).
        }
        return ultimoBarcoGenerado.distanciaRecorrida() >= SEPARACION_MINIMA_BARCOS; // True si el último ya avanzó lo suficiente.
    }

    /**
     * Mueve los barcos, actualiza sus temporizadores y lanza cargas si corresponde.
     */
    private void moverBarcosYLanzarCargas() {
        Iterator<BarcoEnemigo> iterator = barcos.iterator(); // Recorre la lista de barcos (permite borrar mientras recorre).

        while (iterator.hasNext()) {            // Mientras queden barcos por procesar...
            BarcoEnemigo barco = iterator.next(); // Toma el siguiente barco.

            barco.mover();                      // Lo desplaza un paso.
            barco.actualizarTemporizador();     // Descuenta su contador para lanzar.

            if (barco.puedeLanzar()) {          // Si llegó el momento de lanzar...
                CargaProfundidad carga = barco.lanzarCarga(random, velocidadCargas, nivel); // ...crea una carga...
                cargas.add(carga);              // ...y la agrega a la lista de cargas.
            }

            if (barco.salioDePantalla()) {      // Si el barco ya salió del mapa...
                iterator.remove();              // ...lo quita de la lista...
                barcosQuePasaron++;             // ...y cuenta que un barco más completó su recorrido.
            }
        }
    }

    /**
     * Mueve las cargas y procesa las explosiones.
     */
    private void moverCargasYProcesarExplosiones() {
        Iterator<CargaProfundidad> iterator = cargas.iterator(); // Recorre la lista de cargas.

        while (iterator.hasNext()) {                  // Mientras queden cargas...
            CargaProfundidad carga = iterator.next(); // Toma la siguiente carga.

            carga.caer();                             // La hace bajar un paso.

            if (carga.debeExplotar()) {               // Si llegó a su profundidad de detonación...
                procesarExplosion(carga);             // ...calcula el efecto de la explosión...
                iterator.remove();                    // ...y la quita de la lista (ya explotó).
            }
        }
    }

    /**
     * Calcula la distancia entre la explosión y el submarino para determinar daño y puntaje.
     */
    private void procesarExplosion(CargaProfundidad carga) {
        double dx = carga.getPosX() - submarino.getPosX();                      // Diferencia horizontal entre explosión y submarino.
        double dy = carga.getProfundidadDetonacion() - submarino.getPosY();     // Diferencia vertical entre explosión y submarino.

        double distancia = Math.sqrt(dx * dx + dy * dy);                        // Distancia real (teorema de Pitágoras).

        int puntosGanados = submarino.recibirImpacto(distancia);               // El submarino recibe el impacto y devuelve puntos.
        sumarPuntos(puntosGanados);                                            // Suma esos puntos al puntaje total.
    }

    /**
     * Verifica si ya terminó el nivel actual.
     * Para pasar de nivel tienen que haber pasado los 12 barcos y no quedar cargas activas.
     */
    private void verificarCambioDeNivel() {
        boolean terminoNivel = barcosQuePasaron >= BARCOS_POR_NIVEL // Pasaron los 12 barcos...
                && barcos.isEmpty()                                 // ...no queda ningún barco...
                && cargas.isEmpty();                                // ...y no queda ninguna carga.

        if (terminoNivel) {     // Si se cumplen las tres condiciones...
            avanzarNivel();     // ...se pasa al siguiente nivel.
        }
    }

    /**
     * Avanza al siguiente nivel.
     * Cada nivel aumenta 20% las velocidades y suma 200 puntos.
     */
    public void avanzarNivel() {
        nivel++;                                  // Sube el número de nivel.
        velocidadBarcos = velocidadBarcos * 1.2;  // Aumenta 20% la velocidad de los barcos.
        velocidadCargas = velocidadCargas * 1.2;  // Aumenta 20% la velocidad de las cargas.

        barcosGenerados = 0;        // Reinicia el conteo de barcos generados.
        barcosQuePasaron = 0;       // Reinicia el conteo de barcos que pasaron.
        ultimoBarcoGenerado = null; // Olvida el último barco (para que el primero del nivel salga enseguida).

        sumarPuntos(200);           // Otorga 200 puntos por avanzar de nivel.
    }

    /**
     * Suma puntos y agrega una vida extra cada vez que se alcanzan 500 puntos acumulados.
     */
    public void sumarPuntos(int puntos) {
        puntaje += puntos;                              // Suma los puntos recibidos al total.

        while (puntaje >= proximoPuntajeVidaExtra) {    // Mientras se haya alcanzado el próximo umbral de vida extra...
            submarino.agregarVida();                    // ...da una vida extra...
            proximoPuntajeVidaExtra += 500;             // ...y mueve el umbral 500 puntos más arriba.
        }
    }

    /**
     * Indica si el juego terminó.
     */
    public boolean estaTerminado() {                       // Devuelve true si la partida terminó.
        return submarino != null && submarino.getVidas() <= 0; // Termina cuando el submarino existe y se quedó sin vidas.
    }

    public Submarino getSubmarino() {      // Getter: el submarino.
        return submarino;                  // Devuelve el submarino.
    }

    public List<BarcoEnemigo> getBarcos() { // Getter: la lista de barcos.
        return barcos;                      // Devuelve la lista de barcos.
    }

    public List<CargaProfundidad> getCargas() { // Getter: la lista de cargas.
        return cargas;                          // Devuelve la lista de cargas.
    }

    public int getNivel() {        // Getter: nivel actual.
        return nivel;              // Devuelve nivel.
    }

    public double getVelocidadBarcos() { // Getter: velocidad actual de barcos.
        return velocidadBarcos;          // Devuelve velocidadBarcos.
    }

    public double getVelocidadCargas() { // Getter: velocidad actual de cargas.
        return velocidadCargas;          // Devuelve velocidadCargas.
    }

    public int getPuntaje() {      // Getter: puntaje actual.
        return puntaje;            // Devuelve puntaje.
    }
}
