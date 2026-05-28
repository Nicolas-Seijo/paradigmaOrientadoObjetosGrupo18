package modelo;

import java.util.Random;

/**
 * Representa un barco enemigo que se mueve por la superficie y puede lanzar cargas.
 */
public class BarcoEnemigo {

    private int posicionX;
    private int direccion; // 1 = derecha, -1 = izquierda
    private double velocidad;
    private int ticksParaLanzar;

    public BarcoEnemigo(int posicionX, int direccion, double velocidad, int ticksParaLanzar) {
        this.posicionX = posicionX;
        this.direccion = direccion;
        this.velocidad = velocidad;
        this.ticksParaLanzar = ticksParaLanzar;
    }

    /**
     * Mueve el barco horizontalmente según su dirección.
     */
    public void mover() {
        posicionX += direccion * velocidad;
    }

    /**
     * Reduce el contador para lanzar una carga.
     */
    public void actualizarTemporizador() {
        if (ticksParaLanzar > 0) {
            ticksParaLanzar--;
        }
    }

    /**
     * Indica si el barco puede lanzar una carga en este tick.
     */
    public boolean puedeLanzar() {
        return ticksParaLanzar <= 0;
    }

    /**
     * Lanza una nueva carga de profundidad.
     * La profundidad de detonación se genera aleatoriamente entre 300 y 700.
     */
    public CargaProfundidad lanzarCarga(Random random, double velocidadCargas, int nivel) {
        int profundidadDetonacion = random.nextInt(401) + 300; // entre 300 y 700
        reiniciarTemporizador(random, nivel);

        return new CargaProfundidad(posicionX, 0, profundidadDetonacion, velocidadCargas);
    }

    /**
     * Reinicia el contador para el próximo lanzamiento.
     * En niveles mayores, el tiempo entre lanzamientos baja un poco.
     */
    public void reiniciarTemporizador(Random random, int nivel) {
        int minimo = Math.max(2, 8 - nivel);
        int maximo = Math.max(5, 15 - nivel);
        ticksParaLanzar = random.nextInt(maximo - minimo + 1) + minimo;
    }

    /**
     * Indica si el barco ya salió del área de juego.
     */
    public boolean salioDePantalla() {
        return posicionX < -50 || posicionX > Juego.ANCHO_MAPA + 50;
    }

    public int getPosicionX() {
        return posicionX;
    }

    public int getDireccion() {
        return direccion;
    }

    public double getVelocidad() {
        return velocidad;
    }

    public int getTicksParaLanzar() {
        return ticksParaLanzar;
    }
}
