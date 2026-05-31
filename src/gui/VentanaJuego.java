package gui;

import controlador.Controlador;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Ventana principal del juego (interfaz gráfica con Swing).
 * Crea el controlador, inicia el juego y muestra el PanelJuego.
 *
 * Sigue el patrón de la referencia TestMovimiento: un JFrame que contiene
 * la vista y delega toda la lógica en el Controlador.
 */
public class VentanaJuego extends JFrame {

    private static final long serialVersionUID = 1L;

    public VentanaJuego() {
        Controlador controlador = new Controlador();
        controlador.iniciarJuego();

        PanelJuego panel = new PanelJuego(controlador);
        add(panel);

        setTitle("Submarino - Cargas de Profundidad");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);

        // Asegura que el panel reciba los eventos de teclado
        panel.requestFocusInWindow();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(VentanaJuego::new);
    }
}
