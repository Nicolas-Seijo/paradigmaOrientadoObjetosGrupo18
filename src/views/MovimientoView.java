package views;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import controlador.Controlador;
import modelo.BarcoEnemigo;
import modelo.CargaProfundidad;
import modelo.Submarino;

public class MovimientoView extends JPanel implements KeyListener, ActionListener {

    private Controlador controlador;
    private Timer timer;
    private JLabel etiquetaDatosEstado;

    private List<ExplosionVisual> listaExplosionesVisuales;

    private static final int Y_SUPERFICIE_AGUA = 100;
    private static final Color COLOR_AGUA = new Color(28, 107, 160);
    private static final Color COLOR_SUPERFICIE_CIELO = new Color(135, 206, 235);
    private static final Color COLOR_BARCO = new Color(64, 64, 64);
    private static final Color COLOR_CARGA = new Color(178, 34, 34);
    private static final Color COLOR_SUBMARINO = Color.ORANGE;

    public MovimientoView(Controlador controlador) {
        this.controlador = controlador;
        this.listaExplosionesVisuales = new ArrayList<>();

        setLayout(null);
        setOpaque(true);
        setBackground(COLOR_AGUA);

        etiquetaDatosEstado = new JLabel("");
        etiquetaDatosEstado.setForeground(Color.WHITE);
        etiquetaDatosEstado.setBounds(10, 10, 800, 30);
        add(etiquetaDatosEstado);

        timer = new Timer(50, this);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!controlador.getJuego().estaTerminado()) {

            controlador.actualizarJuego();

            for (int[] coordenadas : controlador.getJuego().getExplosionesRecientes()) {
                listaExplosionesVisuales.add(new ExplosionVisual(coordenadas[0], coordenadas[1]));
            }

            actualizarAnimacionesExplosion();
            dibujarEstado();
        } else {
            timer.stop();
            etiquetaDatosEstado.setText("JUEGO TERMINADO - Puntaje Final: " + controlador.getJuego().getPuntaje());
            repaint();
        }
    }

    private void actualizarAnimacionesExplosion() {
        Iterator<ExplosionVisual> iteradorExplosiones = listaExplosionesVisuales.iterator();
        while (iteradorExplosiones.hasNext()) {
            ExplosionVisual explosion = iteradorExplosiones.next();
            explosion.decrementarTicks();
            if (explosion.estaTerminada()) {
                iteradorExplosiones.remove();
            }
        }
    }

    private void dibujarEstado() {
        String textoEstado = String.format("Nivel: %d | Puntaje: %d | Vidas: %d | Salud: %d",
                controlador.getJuego().getNivel(),
                controlador.getJuego().getPuntaje(),
                controlador.getJuego().getSubmarino().getVidas(),
                controlador.getJuego().getSubmarino().getSalud());
        etiquetaDatosEstado.setText(textoEstado);

        repaint();
    }

    @Override
    protected void paintComponent(Graphics graficosBase) {
        super.paintComponent(graficosBase);
        Graphics2D graficos2D = (Graphics2D) graficosBase;

        graficos2D.setColor(COLOR_SUPERFICIE_CIELO);
        graficos2D.fillRect(0, 0, getWidth(), Y_SUPERFICIE_AGUA);
        graficos2D.setColor(Color.WHITE);
        graficos2D.drawLine(0, Y_SUPERFICIE_AGUA, getWidth(), Y_SUPERFICIE_AGUA);

        graficos2D.setColor(COLOR_BARCO);
        for (BarcoEnemigo barco : controlador.getJuego().getBarcos()) {
            int posicionXBarco = barco.getPosicionX();
            int posicionYBarco = 70;
            graficos2D.fillRect(posicionXBarco + 15, posicionYBarco - 15, 20, 15);
            graficos2D.fillRect(posicionXBarco, posicionYBarco, 80, 30);
        }

        graficos2D.setColor(COLOR_CARGA);
        for (CargaProfundidad carga : controlador.getJuego().getCargas()) {
            graficos2D.fillOval(carga.getPosX(), carga.getPosY(), 20, 20);
        }

        Submarino submarino = controlador.getJuego().getSubmarino();
        if (submarino != null) {
            int posicionXSubmarino = submarino.getPosX();
            int posicionYSubmarino = submarino.getPosY();

            graficos2D.setColor(COLOR_SUBMARINO);
            graficos2D.fillRect(posicionXSubmarino + 20, posicionYSubmarino - 15, 20, 15);

            graficos2D.fillRoundRect(posicionXSubmarino, posicionYSubmarino, 60, 30, 15, 15);

            graficos2D.setColor(new Color(135, 206, 235));
            graficos2D.fillRoundRect(posicionXSubmarino + 15, posicionYSubmarino + 10, 10, 10, 4, 4);
            graficos2D.fillRoundRect(posicionXSubmarino + 35, posicionYSubmarino + 10, 10, 10, 4, 4);

            graficos2D.setColor(Color.RED);
            graficos2D.drawRect(posicionXSubmarino + 20, posicionYSubmarino - 15, 20, 15);
            graficos2D.drawRoundRect(posicionXSubmarino, posicionYSubmarino, 60, 30, 15, 15);
        }
    }

    @Override
    public void paint(Graphics graficosBase) {
        super.paint(graficosBase);
        Graphics2D graficos2D = (Graphics2D) graficosBase;
        for (ExplosionVisual explosion : listaExplosionesVisuales) {
            explosion.dibujar(graficos2D);
        }
    }

    @Override
    public void keyPressed(KeyEvent eventoTeclado) {
        int codigoTecla = eventoTeclado.getKeyCode();
        if (codigoTecla == KeyEvent.VK_UP || codigoTecla == KeyEvent.VK_W) controlador.moverArriba();
        else if (codigoTecla == KeyEvent.VK_DOWN || codigoTecla == KeyEvent.VK_S) controlador.moverAbajo();
        else if (codigoTecla == KeyEvent.VK_LEFT || codigoTecla == KeyEvent.VK_A) controlador.moverIzquierda();
        else if (codigoTecla == KeyEvent.VK_RIGHT || codigoTecla == KeyEvent.VK_D) controlador.moverDerecha();
    }

    @Override public void keyReleased(KeyEvent eventoTeclado) {}
    @Override public void keyTyped(KeyEvent eventoTeclado) {}

    private class ExplosionVisual {
        int centroX, centroY;
        int ticksRestantes;
        final int TICKS_TOTALES = 8;

        public ExplosionVisual(int centroX, int centroY) {
            this.centroX = centroX;
            this.centroY = centroY;
            this.ticksRestantes = TICKS_TOTALES;
        }

        public void decrementarTicks() {
            if (ticksRestantes > 0) {
                ticksRestantes--;
            }
        }

        public boolean estaTerminada() {
            return ticksRestantes <= 0;
        }

        public void dibujar(Graphics2D graficos2D) {
            int ticksTranscurridos = TICKS_TOTALES - ticksRestantes;
            int tamanoFinal = 60;
            int tamanoInicial = 10;

            float proporcionTranscurrida = (float) ticksTranscurridos / TICKS_TOTALES;
            int tamanoActual = tamanoInicial + (int) ((tamanoFinal - tamanoInicial) * proporcionTranscurrida);

            int intensidadRojo = 255;
            int intensidadVerde = (int)(255 * (1 - proporcionTranscurrida));
            int intensidadAzul = 0;
            int nivelTransparencia = (int)(255 * (1 - proporcionTranscurrida * proporcionTranscurrida));

            graficos2D.setColor(new Color(intensidadRojo, Math.max(0, intensidadVerde), intensidadAzul, Math.max(0, nivelTransparencia)));
            graficos2D.fillOval(centroX - tamanoActual/2, centroY - tamanoActual/2, tamanoActual, tamanoActual);

            graficos2D.setColor(new Color(255, 255, 255, nivelTransparencia/2));
            graficos2D.drawOval(centroX - tamanoActual/2, centroY - tamanoActual/2, tamanoActual, tamanoActual);
        }
    }
}