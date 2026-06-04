import controlador.Controlador; // Importa el Controlador, por el que se interactúa con el juego.

/**
 * Clase principal para probar el juego sin interfaz gráfica.
 * Sirve para ejecutar algunos "ticks" y ver por consola cómo evoluciona el estado.
 */
public class Main { // Declara la clase principal de la versión de consola.

    public static void main(String[] args) { // Punto de entrada: se ejecuta al correr Main.
        Controlador controlador = new Controlador(); // Crea el controlador (y con él, el juego).

        // Inicializa el juego
        controlador.iniciarJuego(); // Pone el juego en su estado inicial.

        System.out.println("=== INICIO DEL JUEGO ==="); // Imprime un encabezado.
        mostrarEstado(controlador);                     // Muestra el estado inicial por consola.

        // Simulación simple de algunos pasos del juego
        for (int i = 1; i <= 15; i++) { // Repite 15 ticks de simulación.

            // Movimientos de ejemplo para probar el submarino
            if (i == 2) {                     // En el tick 2...
                controlador.moverAbajo();     // ...el submarino baja.
            }

            if (i == 4) {                     // En el tick 4...
                controlador.moverDerecha();   // ...el submarino va a la derecha.
            }

            if (i == 6) {                     // En el tick 6...
                controlador.moverArriba();    // ...el submarino sube.
            }

            // Actualiza el estado del juego
            controlador.actualizarJuego();    // Avanza el juego un paso.

            System.out.println("\n--- Tick " + i + " ---"); // Imprime el número de tick.
            mostrarEstado(controlador);                     // Muestra el estado después de ese tick.
        }
    }

    /**
     * Muestra por consola los datos principales del juego.
     */
    private static void mostrarEstado(Controlador controlador) { // Imprime un resumen del estado.
        System.out.println("Nivel: " + controlador.getJuego().getNivel());                               // Nivel actual.
        System.out.println("Puntaje: " + controlador.getJuego().getPuntaje());                           // Puntaje acumulado.
        System.out.println("Vidas: " + controlador.getJuego().getSubmarino().getVidas());                // Vidas del submarino.
        System.out.println("Salud: " + controlador.getJuego().getSubmarino().getSalud());                // Salud del submarino.
        System.out.println("Posición X submarino: " + controlador.getJuego().getSubmarino().getPosX());  // Posición horizontal.
        System.out.println("Profundidad submarino: " + controlador.getJuego().getSubmarino().getPosY()); // Profundidad.
        System.out.println("Barcos activos: " + controlador.getJuego().getBarcos().size());              // Cantidad de barcos en pantalla.
        System.out.println("Cargas activas: " + controlador.getJuego().getCargas().size());              // Cantidad de cargas en pantalla.
    }
}
