import controlador.Controlador;

/**
 * Clase principal para probar el juego sin interfaz gráfica.
 * Sirve para ejecutar algunos "ticks" y ver por consola cómo evoluciona el estado.
 */
public class Main {

    public static void main(String[] args) {
        Controlador controlador = new Controlador();

        // Inicializa el juego
        controlador.iniciarJuego();

        System.out.println("=== INICIO DEL JUEGO ===");
        mostrarEstado(controlador);

        // Simulación simple de algunos pasos del juego
        for (int i = 1; i <= 15; i++) {

            // Movimientos de ejemplo para probar el submarino
            if (i == 2) {
                controlador.moverAbajo();
            }

            if (i == 4) {
                controlador.moverDerecha();
            }

            if (i == 6) {
                controlador.moverArriba();
            }

            // Actualiza el estado del juego
            controlador.actualizarJuego();

            System.out.println("\n--- Tick " + i + " ---");
            mostrarEstado(controlador);
        }
    }

    /**
     * Muestra por consola los datos principales del juego.
     */
    private static void mostrarEstado(Controlador controlador) {
        System.out.println("Nivel: " + controlador.getJuego().getNivel());
        System.out.println("Puntaje: " + controlador.getJuego().getPuntaje());
        System.out.println("Vidas: " + controlador.getJuego().getSubmarino().getVidas());
        System.out.println("Salud: " + controlador.getJuego().getSubmarino().getSalud());
        System.out.println("Posición X submarino: " + controlador.getJuego().getSubmarino().getPosX());
        System.out.println("Profundidad submarino: " + controlador.getJuego().getSubmarino().getPosY());
        System.out.println("Barcos activos: " + controlador.getJuego().getBarcos().size());
        System.out.println("Cargas activas: " + controlador.getJuego().getCargas().size());
    }
}

