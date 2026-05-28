import controlador.Controlador;
import vista.PanelJuego;

import javax.swing.JFrame;

/**
 * Clase principal que lanza la interfaz gráfica.
 */
public class Main {
    public static void main(String[] args) {
        // 1. Crear el controlador general
        Controlador controlador = new Controlador();

        // 2. Crear la ventana principal del juego
        JFrame ventana = new JFrame("Submarine Attack");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setResizable(false);

        // 3. Añadir el panel de juego (que contiene toda la vista)
        PanelJuego panel = new PanelJuego(controlador);
        ventana.add(panel);

        // 4. Ajustar el tamaño de la ventana y mostrarla
        ventana.pack();
        ventana.setLocationRelativeTo(null); // Centrar en la pantalla
        ventana.setVisible(true);
    }
}