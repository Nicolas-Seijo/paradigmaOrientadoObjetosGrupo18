package views;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.Border;

import controlador.Controlador;
import modelo.BarcoEnemigo;
import modelo.CargaProfundidad;
import modelo.Submarino;

public class MovimientoView extends JPanel implements KeyListener, ActionListener {

    private Controlador controlador;
    private Timer timer;
    private JLabel etiquetaDatosEstado;

    private List<ExplosionVisual> listaExplosionesVisuales;

    private static final Color COLOR_AGUA = new Color(28, 107, 160);
    private static final Color COLOR_CARGA_MILITAR = new Color(85, 107, 47);

    public MovimientoView(Controlador controlador) {
        this.controlador = controlador;
        this.listaExplosionesVisuales = new ArrayList<>();

        setLayout(null);
        setOpaque(true);
        setBackground(COLOR_AGUA);

        etiquetaDatosEstado = new JLabel("");
        etiquetaDatosEstado.setForeground(Color.WHITE);
        etiquetaDatosEstado.setBounds(10, 10, 800, 30);

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

            this.removeAll();
            this.add(etiquetaDatosEstado);
            this.revalidate();
            this.repaint();
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
        this.removeAll();

        String textoEstado = String.format("Nivel: %d | Puntaje: %d | Vidas: %d | Salud: %d",
                controlador.getJuego().getNivel(),
                controlador.getJuego().getPuntaje(),
                controlador.getJuego().getSubmarino().getVidas(),
                controlador.getJuego().getSubmarino().getSalud());
        etiquetaDatosEstado.setText(textoEstado);
        this.add(etiquetaDatosEstado);

        Submarino submarino = controlador.getJuego().getSubmarino();
        if (submarino != null) {
            JPanel panelSubmarino = new JPanel();
            panelSubmarino.setLayout(null);
            panelSubmarino.setOpaque(false);

            panelSubmarino.setBounds(submarino.getPosX(), submarino.getPosY() - 15, 90, 45);

            Border bordeRojo = BorderFactory.createLineBorder(Color.RED);

            // Punta izquierda modificada: menos ancha y centrada respecto al cuerpo
            JLabel punta = new JLabel();
            punta.setOpaque(true);
            punta.setBackground(Color.ORANGE);
            punta.setBorder(bordeRojo);
            punta.setBounds(4, 21, 11, 21);
            panelSubmarino.add(punta);

            JLabel ventana1 = new JLabel();
            ventana1.setOpaque(true);
            ventana1.setBackground(new Color(135, 206, 235));
            ventana1.setBounds(30, 25, 10, 10);
            panelSubmarino.add(ventana1);

            JLabel ventana2 = new JLabel();
            ventana2.setOpaque(true);
            ventana2.setBackground(new Color(135, 206, 235));
            ventana2.setBounds(50, 25, 10, 10);
            panelSubmarino.add(ventana2);

            JLabel heliceArriba = new JLabel();
            heliceArriba.setOpaque(true);
            heliceArriba.setBackground(Color.ORANGE);
            heliceArriba.setBorder(bordeRojo);
            heliceArriba.setBounds(75, 18, 10, 10);
            panelSubmarino.add(heliceArriba);

            JLabel heliceAbajo = new JLabel();
            heliceAbajo.setOpaque(true);
            heliceAbajo.setBackground(Color.ORANGE);
            heliceAbajo.setBorder(bordeRojo);
            heliceAbajo.setBounds(75, 32, 10, 10);
            panelSubmarino.add(heliceAbajo);

            JLabel torreta = new JLabel();
            torreta.setOpaque(true);
            torreta.setBackground(Color.ORANGE);
            torreta.setBorder(bordeRojo);
            torreta.setBounds(35, 0, 20, 15);
            panelSubmarino.add(torreta);

            JLabel cuerpo = new JLabel();
            cuerpo.setOpaque(true);
            cuerpo.setBackground(Color.ORANGE);
            cuerpo.setBorder(bordeRojo);
            cuerpo.setBounds(15, 15, 60, 30);
            panelSubmarino.add(cuerpo);

            this.add(panelSubmarino);
        }

        for (BarcoEnemigo barco : controlador.getJuego().getBarcos()) {
            JPanel panelBarco = new JPanel();
            panelBarco.setLayout(null);
            panelBarco.setOpaque(false);
            panelBarco.setBounds(barco.getPosicionX() - 10, 55, 100, 45);

            JLabel ventanaBarco = new JLabel();
            ventanaBarco.setOpaque(true);
            ventanaBarco.setBackground(Color.WHITE);
            ventanaBarco.setBounds(25, 20, 20, 10);
            panelBarco.add(ventanaBarco);

            JLabel cabina = new JLabel();
            cabina.setOpaque(true);
            cabina.setBackground(Color.DARK_GRAY);
            cabina.setBounds(20, 0, 30, 15);
            panelBarco.add(cabina);

            JLabel extensionIzquierda = new JLabel();
            extensionIzquierda.setOpaque(true);
            extensionIzquierda.setBackground(Color.DARK_GRAY);
            extensionIzquierda.setBounds(0, 15, 10, 20);
            panelBarco.add(extensionIzquierda);

            JLabel cuerpoBarco = new JLabel();
            cuerpoBarco.setOpaque(true);
            cuerpoBarco.setBackground(Color.DARK_GRAY);
            cuerpoBarco.setBounds(10, 15, 80, 30);
            panelBarco.add(cuerpoBarco);

            JLabel extensionDerecha = new JLabel();
            extensionDerecha.setOpaque(true);
            extensionDerecha.setBackground(Color.DARK_GRAY);
            extensionDerecha.setBounds(90, 15, 10, 20);
            panelBarco.add(extensionDerecha);

            this.add(panelBarco);
        }

        for (CargaProfundidad carga : controlador.getJuego().getCargas()) {
            JLabel etiquetaCarga = new JLabel();
            etiquetaCarga.setOpaque(true);
            etiquetaCarga.setBackground(COLOR_CARGA_MILITAR);
            etiquetaCarga.setBounds(carga.getPosX(), carga.getPosY(), 20, 20);
            this.add(etiquetaCarga);
        }

        for (ExplosionVisual explosion : listaExplosionesVisuales) {
            int ticksTranscurridos = ExplosionVisual.TICKS_TOTALES - explosion.ticksRestantes;
            int tamanoActual = 10 + (ticksTranscurridos * 6);

            JLabel etiquetaExplosion = new JLabel();
            etiquetaExplosion.setOpaque(true);
            etiquetaExplosion.setBackground(Color.YELLOW);
            etiquetaExplosion.setBounds(explosion.centroX - tamanoActual / 2, explosion.centroY - tamanoActual / 2, tamanoActual, tamanoActual);
            this.add(etiquetaExplosion);
        }

        JLabel etiquetaCielo = new JLabel();
        etiquetaCielo.setOpaque(true);
        etiquetaCielo.setBackground(new Color(135, 206, 235));
        etiquetaCielo.setBounds(0, 0, 1000, 100);
        this.add(etiquetaCielo);

        this.revalidate();
        this.repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int codigoTecla = e.getKeyCode();
        if (codigoTecla == KeyEvent.VK_UP || codigoTecla == KeyEvent.VK_W) controlador.moverArriba();
        else if (codigoTecla == KeyEvent.VK_DOWN || codigoTecla == KeyEvent.VK_S) controlador.moverAbajo();
        else if (codigoTecla == KeyEvent.VK_LEFT || codigoTecla == KeyEvent.VK_A) controlador.moverIzquierda();
        else if (codigoTecla == KeyEvent.VK_RIGHT || codigoTecla == KeyEvent.VK_D) controlador.moverDerecha();
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    private class ExplosionVisual {
        int centroX, centroY;
        int ticksRestantes;
        public static final int TICKS_TOTALES = 8;

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
    }
}