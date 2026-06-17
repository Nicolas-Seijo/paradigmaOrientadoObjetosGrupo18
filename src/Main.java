import controlador.Controlador;
import gui.VentanaPrincipal;

public class Main {

    public static void main(String[] args) {
        Controlador controlador = new Controlador();
        controlador.iniciarJuego();

        VentanaPrincipal ventana = new VentanaPrincipal(controlador);
        ventana.setVisible(true);
    }
}