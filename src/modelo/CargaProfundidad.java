package modelo; // Paquete "modelo": agrupa las clases de la lógica del juego.

/**
 * Representa una carga de profundidad lanzada por un barco enemigo.
 * Cae en línea recta y explota al llegar a cierta profundidad.
 */
public class CargaProfundidad { // Declara la clase pública CargaProfundidad.

    private int posX;                  // Posición horizontal de la carga (fija mientras cae).
    private int posY;                  // Profundidad actual de la carga (aumenta al caer).
    private int profundidadDetonacion; // Profundidad a la que la carga debe explotar.
    private double velocidad;          // Cuánto baja la carga en cada paso (tick).

    // Constructor: recibe los valores iniciales y los guarda en el objeto.
    public CargaProfundidad(int posX, int posY, int profundidadDetonacion, double velocidad) {
        this.posX = posX;                                   // Guarda la posición horizontal recibida.
        this.posY = posY;                                   // Guarda la profundidad inicial recibida.
        this.profundidadDetonacion = profundidadDetonacion; // Guarda la profundidad de detonación.
        this.velocidad = velocidad;                         // Guarda la velocidad de caída.
    }

    /**
     * Hace caer la carga hacia mayor profundidad.
     */
    public void caer() {           // Método que avanza la carga un paso hacia abajo.
        posY += velocidad;         // Suma la velocidad a la profundidad (la carga baja).
    }

    /**
     * Indica si ya llegó a la profundidad donde debe explotar.
     */
    public boolean debeExplotar() {                 // Devuelve true si la carga debe detonar.
        return posY >= profundidadDetonacion;       // Es true cuando alcanzó (o pasó) su profundidad.
    }

    public int getPosX() {         // Getter: permite leer la posición horizontal desde afuera.
        return posX;               // Devuelve el valor de posX.
    }

    public int getPosY() {         // Getter: permite leer la profundidad actual desde afuera.
        return posY;               // Devuelve el valor de posY.
    }

    public int getProfundidadDetonacion() { // Getter: permite leer la profundidad de detonación.
        return profundidadDetonacion;       // Devuelve el valor de profundidadDetonacion.
    }

    public double getVelocidad() { // Getter: permite leer la velocidad de caída.
        return velocidad;          // Devuelve el valor de velocidad.
    }
}
