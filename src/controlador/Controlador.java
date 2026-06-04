package controlador; // Paquete "controlador": intermediario entre la vista y el modelo.
import modelo.Juego;  // Importa la clase Juego, que tiene toda la lógica.

/**
 * Controlador principal del juego.
 * Su responsabilidad es recibir acciones y delegarlas al objeto Juego.
 */
public class Controlador { // Declara la clase pública Controlador.

    private Juego juego; // El juego que este controlador administra.

    // Constructor: crea el juego que va a controlar.
    public Controlador() {
        this.juego = new Juego(); // Instancia un nuevo Juego.
    }

    /**
     * Inicia el juego desde cero.
     */
    public void iniciarJuego() { // Pide al juego que se inicialice.
        juego.iniciar();         // Delega en Juego.iniciar().
    }

    /**
     * Actualiza un paso del juego.
     */
    public void actualizarJuego() { // Pide al juego que avance un tick.
        juego.actualizarJuego();    // Delega en Juego.actualizarJuego().
    }

    /**
     * Mueve el submarino hacia arriba.
     */
    public void moverArriba() {        // Acción: subir.
        juego.getSubmarino().subir();  // Pide al submarino del juego que suba.
    }

    /**
     * Mueve el submarino hacia abajo.
     */
    public void moverAbajo() {         // Acción: bajar.
        juego.getSubmarino().bajar();  // Pide al submarino del juego que baje.
    }

    /**
     * Mueve el submarino a la izquierda.
     */
    public void moverIzquierda() {              // Acción: izquierda.
        juego.getSubmarino().moverIzquierda();  // Pide al submarino que se mueva a la izquierda.
    }

    /**
     * Mueve el submarino a la derecha.
     */
    public void moverDerecha() {              // Acción: derecha.
        juego.getSubmarino().moverDerecha();  // Pide al submarino que se mueva a la derecha.
    }

    public Juego getJuego() { // Getter: permite a la vista leer el estado del juego.
        return juego;         // Devuelve el objeto Juego.
    }
}
