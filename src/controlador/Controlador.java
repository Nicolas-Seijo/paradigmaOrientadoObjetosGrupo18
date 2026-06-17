package controlador;
import modelo.Juego;

/**
 * Controlador principal del juego.
 * Su responsabilidad es recibir acciones y delegarlas al objeto Juego.
 */
public class Controlador {

    private Juego juego;

    public Controlador() {
        this.juego = new Juego();
    }

    /**
     * Inicia el juego desde cero.
     */
    public void iniciarJuego() {
        juego.iniciar();
    }

    /**
     * Actualiza un paso del juego.
     */
    public void actualizarJuego() {
        juego.actualizarJuego();
    }

    /**
     * Mueve el submarino hacia arriba.
     */
    public void moverArriba() {
        juego.getSubmarino().subir();
    }

    /**
     * Mueve el submarino hacia abajo.
     */
    public void moverAbajo() {
        juego.getSubmarino().bajar();
    }

    /**
     * Mueve el submarino a la izquierda.
     */
    public void moverIzquierda() {
        juego.getSubmarino().moverIzquierda();
    }

    /**
     * Mueve el submarino a la derecha.
     */
    public void moverDerecha() {
        juego.getSubmarino().moverDerecha();
    }

    public Juego getJuego() {
        return juego;
    }
}