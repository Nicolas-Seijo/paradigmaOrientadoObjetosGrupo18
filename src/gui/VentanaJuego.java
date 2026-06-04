package gui; // Paquete "gui": clases de la interfaz gráfica.

import controlador.Controlador;     // El controlador, que conecta la ventana con la lógica.
import javax.swing.JFrame;          // JFrame: la ventana de una aplicación Swing.
import javax.swing.SwingUtilities;  // Utilidades de Swing (para ejecutar en el hilo de la interfaz).

/**
 * Ventana principal del juego (interfaz gráfica con Swing).
 * Crea el controlador, inicia el juego y muestra el PanelJuego.
 *
 * Sigue el patrón de la referencia TestMovimiento: un JFrame que contiene
 * la vista y delega toda la lógica en el Controlador.
 */
public class VentanaJuego extends JFrame { // La ventana hereda de JFrame.

    private static final long serialVersionUID = 1L; // Identificador de versión (lo pide JFrame al ser Serializable).

    // Constructor: arma la ventana y arranca el juego.
    public VentanaJuego() {
        Controlador controlador = new Controlador(); // Crea el controlador (y el juego).
        controlador.iniciarJuego();                  // Inicializa el juego.

        PanelJuego panel = new PanelJuego(controlador); // Crea el panel que dibuja el juego.
        add(panel);                                     // Agrega el panel a la ventana.

        setTitle("Submarino - Cargas de Profundidad"); // Pone el título de la ventana.
        setDefaultCloseOperation(EXIT_ON_CLOSE);       // Al cerrar la ventana, termina el programa.
        pack();                                        // Ajusta el tamaño de la ventana al del panel.
        setLocationRelativeTo(null);                   // Centra la ventana en la pantalla.
        setResizable(false);                           // Impide que el usuario cambie el tamaño.
        setVisible(true);                              // Hace visible la ventana.

        // Asegura que el panel reciba los eventos de teclado
        panel.requestFocusInWindow();                  // Le da el foco al panel para que escuche las flechas.
    }

    public static void main(String[] args) {           // Punto de entrada de la versión gráfica.
        SwingUtilities.invokeLater(VentanaJuego::new); // Crea la ventana en el hilo de eventos de Swing (lo correcto en Swing).
    }
}
