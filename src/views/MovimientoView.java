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
    private JLabel lblDatos;

    private List<ExplosionVisual> visualesExplosiones;

    private static final int Y_SUPERFICIE_AGUA = 100;
    private static final Color COLOR_AGUA = new Color(28, 107, 160);
    private static final Color COLOR_SUPERFICIE_CIELO = new Color(135, 206, 235);
    private static final Color COLOR_BARCO = new Color(64, 64, 64);
    private static final Color COLOR_CARGA = new Color(178, 34, 34);
    private static final Color COLOR_SUBMARINO = Color.ORANGE;

    public MovimientoView(Controlador controlador) {
        this.controlador = controlador;
        this.visualesExplosiones = new ArrayList<>();

        setLayout(null);
        setOpaque(true);
        setBackground(COLOR_AGUA);

        lblDatos = new JLabel("");
        lblDatos.setForeground(Color.WHITE);
        lblDatos.setBounds(10, 10, 800, 30);
        add(lblDatos);

        timer = new Timer(50, this);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!controlador.getJuego().estaTerminado()) {
            List<CargaProfundidad> cargasPrevias = new ArrayList<>(controlador.getJuego().getCargas());

            controlador.actualizarJuego();

            for (CargaProfundidad cargaPrevia : cargasPrevias) {
                if (cargaPrevia.debeExplotar()) {
                    visualesExplosiones.add(new ExplosionVisual(
                            cargaPrevia.getPosX() + 10,
                            cargaPrevia.getProfundidadDetonacion() + 10
                    ));
                }
            }

            actualizarAnimacionesExplosion();
            dibujarEstado();
        } else {
            timer.stop();
            lblDatos.setText("JUEGO TERMINADO - Puntaje Final: " + controlador.getJuego().getPuntaje());
            repaint();
        }
    }

    private void actualizarAnimacionesExplosion() {
        Iterator<ExplosionVisual> iterator = visualesExplosiones.iterator();
        while (iterator.hasNext()) {
            ExplosionVisual explosion = iterator.next();
            explosion.decrementarTicks();
            if (explosion.estaTerminada()) {
                iterator.remove();
            }
        }
    }

    private void dibujarEstado() {
        String texto = String.format("Nivel: %d | Puntaje: %d | Vidas: %d | Salud: %d",
                controlador.getJuego().getNivel(),
                controlador.getJuego().getPuntaje(),
                controlador.getJuego().getSubmarino().getVidas(),
                controlador.getJuego().getSubmarino().getSalud());
        lblDatos.setText(texto);

        // Al usar dibujo directo, solo necesitamos repintar el panel
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // 1. Cielo y Agua
        g2d.setColor(COLOR_SUPERFICIE_CIELO);
        g2d.fillRect(0, 0, getWidth(), Y_SUPERFICIE_AGUA);
        g2d.setColor(Color.WHITE);
        g2d.drawLine(0, Y_SUPERFICIE_AGUA, getWidth(), Y_SUPERFICIE_AGUA);

        // 2. Barcos
        g2d.setColor(COLOR_BARCO);
        for (BarcoEnemigo barco : controlador.getJuego().getBarcos()) {
            int bx = barco.getPosicionX();
            int by = 70;
            // Cabina desplazada 15 píxeles a la derecha
            g2d.fillRect(bx + 15, by - 15, 20, 15);
            // Cuerpo del barco
            g2d.fillRect(bx, by, 80, 30);
        }

        // 3. Cargas de profundidad
        g2d.setColor(COLOR_CARGA);
        for (CargaProfundidad carga : controlador.getJuego().getCargas()) {
            g2d.fillOval(carga.getPosX(), carga.getPosY(), 20, 20);
        }

        // 4. Submarino
        Submarino sub = controlador.getJuego().getSubmarino();
        if (sub != null) {
            int sx = sub.getPosX();
            int sy = sub.getPosY();

            // Torreta
            g2d.setColor(COLOR_SUBMARINO);
            g2d.fillRect(sx + 20, sy - 15, 20, 15);

            // Cuerpo principal
            g2d.fillRoundRect(sx, sy, 60, 30, 15, 15);

            // Ventanas celestes y redondeadas
            g2d.setColor(new Color(135, 206, 235));
            g2d.fillRoundRect(sx + 15, sy + 10, 10, 10, 4, 4);
            g2d.fillRoundRect(sx + 35, sy + 10, 10, 10, 4, 4);

            // Bordes Rojos
            g2d.setColor(Color.RED);
            g2d.drawRect(sx + 20, sy - 15, 20, 15); // Borde torreta
            g2d.drawRoundRect(sx, sy, 60, 30, 15, 15); // Borde cuerpo
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        for (ExplosionVisual explosion : visualesExplosiones) {
            explosion.dibujar(g2d);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int tecla = e.getKeyCode();
        if (tecla == KeyEvent.VK_UP || tecla == KeyEvent.VK_W) controlador.moverArriba();
        else if (tecla == KeyEvent.VK_DOWN || tecla == KeyEvent.VK_S) controlador.moverAbajo();
        else if (tecla == KeyEvent.VK_LEFT || tecla == KeyEvent.VK_A) controlador.moverIzquierda();
        else if (tecla == KeyEvent.VK_RIGHT || tecla == KeyEvent.VK_D) controlador.moverDerecha();
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

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

        public void dibujar(Graphics2D g2d) {
            int ticksTranscurridos = TICKS_TOTALES - ticksRestantes;
            int finalSize = 60;
            int initialSize = 10;

            float ratio = (float) ticksTranscurridos / TICKS_TOTALES;
            int currentSize = initialSize + (int) ((finalSize - initialSize) * ratio);

            int r = 255;
            int g = (int)(255 * (1 - ratio));
            int b = 0;
            int alpha = (int)(255 * (1 - ratio * ratio));

            g2d.setColor(new Color(r, Math.max(0, g), b, Math.max(0, alpha)));
            g2d.fillOval(centroX - currentSize/2, centroY - currentSize/2, currentSize, currentSize);

            g2d.setColor(new Color(255, 255, 255, alpha/2));
            g2d.drawOval(centroX - currentSize/2, centroY - currentSize/2, currentSize, currentSize);
        }
    }
}