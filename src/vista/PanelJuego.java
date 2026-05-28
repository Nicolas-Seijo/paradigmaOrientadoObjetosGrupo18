package vista;

import controlador.Controlador;
import modelo.BarcoEnemigo;
import modelo.CargaProfundidad;
import modelo.Juego;
import modelo.Submarino;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PanelJuego extends JPanel implements ActionListener {
    private Controlador controlador;
    private Timer timer;

    // --- NUEVO: Clase interna para manejar el efecto visual de explosión ---
    private class ExplosionVisual {
        int x, y, radio;
        int maxRadio = 60; // Tamaño máximo que alcanzará la explosión

        public ExplosionVisual(int x, int y) {
            this.x = x;
            this.y = y;
            this.radio = 10; // Empieza pequeña
        }
    }
    private List<ExplosionVisual> explosionesVisuales = new ArrayList<>();
    // -----------------------------------------------------------------------

    public PanelJuego(Controlador controlador) {
        this.controlador = controlador;
        this.controlador.iniciarJuego();

        setFocusable(true);
        setBackground(new Color(0, 105, 148));
        setPreferredSize(new Dimension(Juego.ANCHO_MAPA, Juego.PROFUNDIDAD_MAX_SUBMARINO + 100));

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W) controlador.moverArriba();
                else if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) controlador.moverAbajo();
                else if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) controlador.moverIzquierda();
                else if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) controlador.moverDerecha();

                repaint(); // Para que el submarino se mueva instantáneamente al pulsar
            }
        });

        // --- CAMBIO: 1 tick por segundo (1000 milisegundos) ---
        timer = new Timer(100, this);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Juego juego = controlador.getJuego();
        if (!juego.estaTerminado()) {

            // --- NUEVO: Detectar qué cargas explotaron en este tick ---
            List<CargaProfundidad> cargasAntes = new ArrayList<>(juego.getCargas());

            controlador.actualizarJuego(); // Avanzamos 1 tick en el modelo

            List<CargaProfundidad> cargasDespues = juego.getCargas();

            // Si una carga estaba antes y ahora no, es porque detonó
            // Si una carga estaba antes y ahora no, es porque detonó o salió del mapa
            for (CargaProfundidad carga : cargasAntes) {
                if (!cargasDespues.contains(carga)) {

                    // NUEVO: Solo dibujamos la explosión si la bomba NO salió del mapa
                    // (Es decir, si desapareció mientras estaba en la zona de juego, chocó con el submarino)
                    if (carga.getPosY() <= Juego.PROFUNDIDAD_MAX_SUBMARINO + 50) {

                        // NUEVO: Usamos carga.getPosY() en lugar de getProfundidadDetonacion()
                        explosionesVisuales.add(new ExplosionVisual((int)carga.getPosX(), (int)carga.getPosY()));
                    }
                }
            }
        }

        // --- NUEVO: Hacer crecer las explosiones y borrar las que ya terminaron ---
        Iterator<ExplosionVisual> it = explosionesVisuales.iterator();
        while (it.hasNext()) {
            ExplosionVisual ex = it.next();
            ex.radio += 15; // La explosión crece 15 píxeles por frame
            if (ex.radio > ex.maxRadio) {
                it.remove(); // Desaparece cuando alcanza su tamaño máximo
            }
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Juego juego = controlador.getJuego();

        // 1. Cielo y superficie
        g.setColor(new Color(135, 206, 235));
        g.fillRect(0, 0, Juego.ANCHO_MAPA, 50);
        g.setColor(Color.BLUE);
        g.drawLine(0, 50, Juego.ANCHO_MAPA, 50);

        // 2. Submarino
        Submarino sub = juego.getSubmarino();
        if (sub != null) {
            g.setColor(Color.YELLOW);
            g.fillRect(sub.getPosX() - 20, sub.getPosY() - 10, 40, 20);
        }

        // 3. Barcos Enemigos
        g.setColor(Color.RED);
        for (BarcoEnemigo barco : juego.getBarcos()) {
            // Se dibujan en Y=30 para que queden justo sobre la línea de flotación
            g.fillRect(barco.getPosicionX() - 15, 30, 30, 20);
        }

        // 4. Cargas de Profundidad
        g.setColor(Color.BLACK);
        for (CargaProfundidad carga : juego.getCargas()) {
            // NOTA: Asumo que CargaProfundidad tiene un método getPosY() para saber por dónde va cayendo
            g.fillOval((int)carga.getPosX() - 5, (int)carga.getPosY() - 5, 10, 10);
        }

        // 5. --- NUEVO: Dibujar las explosiones ---
        g.setColor(Color.ORANGE);
        for (ExplosionVisual ex : explosionesVisuales) {
            g.fillOval(ex.x - (ex.radio / 2), ex.y - (ex.radio / 2), ex.radio, ex.radio);
        }

        // 6. HUD (Textos en pantalla)
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        if (sub != null) {
            g.drawString("Salud: " + sub.getSalud() + "%", 20, 20);
            g.drawString("Vidas: " + sub.getVidas(), 150, 20);
        }
        g.drawString("Nivel: " + juego.getNivel(), 300, 20);
        g.drawString("Puntaje: " + juego.getPuntaje(), 450, 20);

        if (juego.estaTerminado()) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.drawString("GAME OVER", Juego.ANCHO_MAPA / 2 - 150, Juego.PROFUNDIDAD_MAX_SUBMARINO / 2);
        }
    }
}