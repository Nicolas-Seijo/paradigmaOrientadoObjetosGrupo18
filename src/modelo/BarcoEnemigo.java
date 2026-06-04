package modelo; // Paquete "modelo": clases de la lógica del juego.

import java.util.Random; // Importa Random, usado para generar valores aleatorios (profundidad, tiempos).

/**
 * Representa un barco enemigo que se mueve por la superficie y puede lanzar cargas.
 */
public class BarcoEnemigo { // Declara la clase pública BarcoEnemigo.

    private int posicionX;             // Posición horizontal actual del barco.
    private final int posicionInicial; // Posición desde la que entró (no cambia: por eso es final).
    private int direccion;             // Sentido del movimiento: 1 = derecha, -1 = izquierda.
    private double velocidad;          // Cuánto se desplaza el barco en cada paso (tick).
    private int ticksParaLanzar;       // Cuántos ticks faltan para lanzar la próxima carga.

    // Constructor: recibe los datos del barco y los guarda.
    public BarcoEnemigo(int posicionX, int direccion, double velocidad, int ticksParaLanzar) {
        this.posicionX = posicionX;          // Guarda la posición horizontal inicial.
        this.posicionInicial = posicionX;    // Recuerda esa posición inicial para medir cuánto avanzó.
        this.direccion = direccion;          // Guarda la dirección de movimiento.
        this.velocidad = velocidad;          // Guarda la velocidad.
        this.ticksParaLanzar = ticksParaLanzar; // Guarda el contador inicial para lanzar.
    }

    /**
     * Distancia que recorrió el barco desde el borde por el que entró.
     * Sirve para no soltar un barco nuevo encima del anterior.
     */
    public int distanciaRecorrida() {                  // Devuelve cuánto se alejó del borde de entrada.
        return Math.abs(posicionX - posicionInicial);  // Diferencia (en valor absoluto) entre ahora y el inicio.
    }

    /**
     * Mueve el barco horizontalmente según su dirección.
     */
    public void mover() {                      // Avanza el barco un paso.
        posicionX += direccion * velocidad;    // Suma velocidad hacia la derecha (+) o izquierda (-).
    }

    /**
     * Reduce el contador para lanzar una carga.
     */
    public void actualizarTemporizador() {     // Descuenta el tiempo que falta para lanzar.
        if (ticksParaLanzar > 0) {             // Solo descuenta si todavía queda tiempo.
            ticksParaLanzar--;                 // Resta 1 al contador.
        }
    }

    /**
     * Indica si el barco puede lanzar una carga en este tick.
     */
    public boolean puedeLanzar() {             // Devuelve true si ya es momento de lanzar.
        return ticksParaLanzar <= 0;           // Es true cuando el contador llegó a 0 (o menos).
    }

    /**
     * Lanza una nueva carga de profundidad.
     * La profundidad de detonación se genera aleatoriamente entre 300 y 700.
     */
    public CargaProfundidad lanzarCarga(Random random, double velocidadCargas, int nivel) {
        int profundidadDetonacion = random.nextInt(401) + 300; // Número aleatorio entre 300 y 700.
        reiniciarTemporizador(random, nivel);                  // Programa cuándo será el próximo lanzamiento.

        return new CargaProfundidad(posicionX, 50, profundidadDetonacion, velocidadCargas); // Crea la carga en la posición del barco.
    }

    /**
     * Reinicia el contador para el próximo lanzamiento.
     * En niveles mayores, el tiempo entre lanzamientos baja un poco.
     */
    public void reiniciarTemporizador(Random random, int nivel) {
        int minimo = Math.max(2, 8 - nivel);                    // Tiempo mínimo de espera (baja con el nivel, nunca menos de 2).
        int maximo = Math.max(5, 15 - nivel);                   // Tiempo máximo de espera (baja con el nivel, nunca menos de 5).
        ticksParaLanzar = random.nextInt(maximo - minimo + 1) + minimo; // Elige un valor aleatorio entre mínimo y máximo.
    }

    /**
     * Indica si el barco ya salió del área de juego.
     */
    public boolean salioDePantalla() {                          // Devuelve true si el barco se fue del mapa.
        return posicionX < -50 || posicionX > Juego.ANCHO_MAPA + 50; // True si pasó el borde izquierdo o el derecho.
    }

    public int getPosicionX() {        // Getter: permite leer la posición horizontal.
        return posicionX;              // Devuelve posicionX.
    }

    public int getDireccion() {        // Getter: permite leer la dirección.
        return direccion;              // Devuelve direccion.
    }

    public double getVelocidad() {     // Getter: permite leer la velocidad.
        return velocidad;              // Devuelve velocidad.
    }

    public int getTicksParaLanzar() {  // Getter: permite leer el contador para lanzar.
        return ticksParaLanzar;        // Devuelve ticksParaLanzar.
    }
}
