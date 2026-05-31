package gui;

import controlador.Controlador;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JPanel;
import javax.swing.Timer;
import modelo.BarcoEnemigo;
import modelo.CargaProfundidad;
import modelo.Juego;
import modelo.Submarino;

/**
 * Panel principal del juego.
 * Dibuja el estado del juego en cada tick y reenvía las teclas al controlador.
 *
 * Sigue el patrón de la referencia (TestMovimiento): un Timer de Swing actúa
 * como game loop y un KeyListener captura las flechas del teclado.
 */
public class PanelJuego extends JPanel implements ActionListener, KeyListener {

    private static final long serialVersionUID = 1L;

    /** Escala para pasar de coordenadas del modelo (0..1000) a pixeles en pantalla. */
    public static final double ESCALA = 0.8;

    /** Intervalo del game loop en milisegundos. */
    private static final int INTERVALO_TICK = 120;

    /** Tamaño visual del submarino y de los barcos (en coordenadas del modelo). */
    private static final int ANCHO_SUB = 70;
    private static final int ALTO_SUB = 34;
    private static final int ANCHO_BARCO = 80;
    private static final int ALTO_BARCO = 30;
    private static final int RADIO_CARGA = 12;

    private final Controlador controlador;
    private final Timer gameLoop;

    public PanelJuego(Controlador controlador) {
        this.controlador = controlador;

        int ancho = escalar(Juego.ANCHO_MAPA);
        int alto = escalar(900);
        setPreferredSize(new java.awt.Dimension(ancho, alto));
        setBackground(new Color(2, 28, 64));
        setFocusable(true);
        addKeyListener(this);

        gameLoop = new Timer(INTERVALO_TICK, this);
        gameLoop.start();
    }

    private int escalar(int valorModelo) {
        return (int) Math.round(valorModelo * ESCALA);
    }

    // ----- Game loop -----

    @Override
    public void actionPerformed(ActionEvent e) {
        controlador.actualizarJuego();

        if (controlador.getJuego().estaTerminado()) {
            gameLoop.stop();
        }

        repaint();
    }

    // ----- Dibujado -----

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        dibujarFondo(g2);

        Juego juego = controlador.getJuego();
        if (juego.getSubmarino() == null) {
            return;
        }

        dibujarBarcos(g2, juego);
        dibujarCargas(g2, juego);
        dibujarSubmarino(g2, juego.getSubmarino());
        dibujarHud(g2, juego);

        if (juego.estaTerminado()) {
            dibujarGameOver(g2);
        }
    }

    private void dibujarFondo(Graphics2D g2) {
        // Franja de cielo + agua
        int superficie = escalar(60);
        g2.setColor(new Color(120, 190, 255));
        g2.fillRect(0, 0, getWidth(), superficie);

        g2.setColor(new Color(2, 28, 64));
        g2.fillRect(0, superficie, getWidth(), getHeight() - superficie);

        // Líneas que marcan el rango donde se puede mover el submarino
        g2.setColor(new Color(255, 255, 255, 40));
        int yMin = escalar(Juego.PROFUNDIDAD_MIN_SUBMARINO);
        int yMax = escalar(Juego.PROFUNDIDAD_MAX_SUBMARINO);
        g2.drawLine(0, yMin, getWidth(), yMin);
        g2.drawLine(0, yMax, getWidth(), yMax);
    }

    private void dibujarBarcos(Graphics2D g2, Juego juego) {
        int superficie = escalar(45);
        for (BarcoEnemigo barco : juego.getBarcos()) {
            int x = escalar(barco.getPosicionX());
            int w = escalar(ANCHO_BARCO);
            int h = escalar(ALTO_BARCO);

            g2.setColor(new Color(70, 200, 90));
            g2.fillRect(x - w / 2, superficie, w, h);

            // Pequeña "torre" del barco
            g2.setColor(new Color(40, 150, 60));
            g2.fillRect(x - w / 6, superficie - h / 2, w / 3, h / 2);
        }
    }

    private void dibujarCargas(Graphics2D g2, Juego juego) {
        for (CargaProfundidad carga : juego.getCargas()) {
            int x = escalar(carga.getPosX());
            int y = escalar(carga.getPosY());
            int r = escalar(RADIO_CARGA);

            g2.setColor(new Color(255, 90, 70));
            g2.fillOval(x - r / 2, y - r / 2, r, r);
        }
    }

    private void dibujarSubmarino(Graphics2D g2, Submarino sub) {
        int x = escalar(sub.getPosX());
        int y = escalar(sub.getPosY());
        int w = escalar(ANCHO_SUB);
        int h = escalar(ALTO_SUB);

        // Cuerpo
        g2.setColor(new Color(255, 210, 70));
        g2.fillRoundRect(x - w / 2, y - h / 2, w, h, h, h);

        // Periscopio
        g2.fillRect(x - 2, y - h, 4, h / 2);

        // Ventana
        g2.setColor(new Color(2, 28, 64));
        g2.fillOval(x + w / 6, y - h / 4, h / 2, h / 2);
    }

    private void dibujarHud(Graphics2D g2, Juego juego) {
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 16));

        Submarino sub = juego.getSubmarino();
        g2.drawString("Nivel: " + juego.getNivel(), 12, 22);
        g2.drawString("Puntaje: " + juego.getPuntaje(), 120, 22);
        g2.drawString("Vidas: " + sub.getVidas(), 260, 22);
        g2.drawString("Salud: " + sub.getSalud(), 360, 22);
    }

    private void dibujarGameOver(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 48));
        String texto = "GAME OVER";
        int ancho = g2.getFontMetrics().stringWidth(texto);
        g2.drawString(texto, (getWidth() - ancho) / 2, getHeight() / 2);
    }

    // ----- Teclado -----

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                controlador.moverIzquierda();
                break;
            case KeyEvent.VK_RIGHT:
                controlador.moverDerecha();
                break;
            case KeyEvent.VK_UP:
                controlador.moverArriba();
                break;
            case KeyEvent.VK_DOWN:
                controlador.moverAbajo();
                break;
            default:
                return;
        }
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) { }

    @Override
    public void keyTyped(KeyEvent e) { }
}
