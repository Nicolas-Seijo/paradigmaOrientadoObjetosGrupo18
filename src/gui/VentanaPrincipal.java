package gui;

import javax.swing.JFrame;
import controlador.Controlador;
import views.MovimientoView;

public class VentanaPrincipal extends JFrame {

    public VentanaPrincipal(Controlador controlador) {
        setTitle("Juego de Submarinos");
        setSize(1000, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Agrega la vista (JPanel) a la ventana
        MovimientoView vista = new MovimientoView(controlador);
        add(vista);

        // El JFrame escucha el teclado y se lo pasa a la vista
        addKeyListener(vista);
    }
}