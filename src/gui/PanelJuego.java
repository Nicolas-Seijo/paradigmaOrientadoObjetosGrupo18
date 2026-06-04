package gui; // Paquete "gui": clases de la interfaz gráfica.

import controlador.Controlador;       // El controlador: por él la vista pide acciones y lee el estado.
import java.awt.Color;                // Para definir colores.
import java.awt.Font;                 // Para definir tipografías (texto del HUD y carteles).
import java.awt.Graphics;             // Objeto base de dibujo que entrega Swing.
import java.awt.Graphics2D;           // Versión más completa de Graphics (antialiasing, etc.).
import java.awt.RenderingHints;       // Opciones de calidad de dibujo (suavizado de bordes).
import java.awt.event.ActionEvent;    // Evento que dispara el Timer en cada tick.
import java.awt.event.ActionListener; // Interfaz para escuchar los eventos del Timer.
import java.awt.event.KeyEvent;       // Evento de teclado (qué tecla se presionó).
import java.awt.event.KeyListener;    // Interfaz para escuchar el teclado.
import java.util.ArrayList;           // Lista dinámica.
import java.util.Iterator;            // Para recorrer y eliminar elementos de una lista.
import java.util.List;                // Tipo "lista".
import javax.swing.JPanel;            // Panel de dibujo de Swing.
import javax.swing.Timer;             // Temporizador de Swing: el "reloj" del juego.
import modelo.BarcoEnemigo;           // Clase del modelo: barco enemigo.
import modelo.CargaProfundidad;       // Clase del modelo: carga de profundidad.
import modelo.Juego;                  // Clase del modelo: el juego completo.
import modelo.Submarino;              // Clase del modelo: submarino.

/**
 * Panel principal del juego.
 * Dibuja el estado del juego en cada tick y reenvía las teclas al controlador.
 *
 * Sigue el patrón de la referencia (TestMovimiento): un Timer de Swing actúa
 * como game loop y un KeyListener captura las flechas del teclado.
 */
public class PanelJuego extends JPanel implements ActionListener, KeyListener { // Panel que además escucha el Timer y el teclado.

    private static final long serialVersionUID = 1L; // Identificador de versión (lo pide JPanel/Serializable).

    /** Escala para pasar de coordenadas del modelo (0..1000) a pixeles en pantalla. */
    public static final double ESCALA = 0.8; // Factor para achicar el mapa del modelo al tamaño de la ventana.

    /** Intervalo del game loop en milisegundos. */
    private static final int INTERVALO_TICK = 120; // Cada cuántos ms avanza el juego (y se redibuja).

    /** Tamaño visual del submarino y de los barcos (en coordenadas del modelo). */
    private static final int ANCHO_SUB = 70;    // Ancho del submarino dibujado.
    private static final int ALTO_SUB = 34;     // Alto del submarino dibujado.
    private static final int ANCHO_BARCO = 80;  // Ancho de los barcos dibujados.
    private static final int ALTO_BARCO = 30;   // Alto de los barcos dibujados.
    private static final int RADIO_CARGA = 12;  // Tamaño de las cargas dibujadas.

    /** Cantidad de ticks que dura visible la animación de "LEVEL UP". */
    private static final int DURACION_LEVEL_UP = 18; // Duración (en ticks) del cartel LEVEL UP.

    /** Cantidad de ticks que dura la animación de explosión de una carga. */
    private static final int DURACION_EXPLOSION = 8; // Duración (en ticks) de cada explosión.

    private final Controlador controlador; // Referencia al controlador (no cambia: final).
    private final Timer gameLoop;          // El temporizador que dispara cada tick.

    /** Nivel mostrado en el tick anterior; sirve para detectar el cambio de nivel. */
    private int nivelAnterior; // Guarda el nivel del tick pasado para compararlo.

    /** Ticks que le quedan a la animación de "LEVEL UP" (0 = no se muestra). */
    private int framesLevelUp = 0; // Contador de la animación LEVEL UP (0 = apagada).

    /** Cargas vistas en el tick anterior; si una desaparece, es que detonó. */
    private List<CargaProfundidad> cargasPrevias = new ArrayList<>(); // Copia de las cargas del tick anterior.

    /** Explosiones en curso que se están animando. */
    private final List<Explosion> explosiones = new ArrayList<>(); // Lista de explosiones activas en pantalla.

    // Constructor: configura el panel y arranca el game loop.
    public PanelJuego(Controlador controlador) {
        this.controlador = controlador;                          // Guarda el controlador recibido.
        this.nivelAnterior = controlador.getJuego().getNivel();  // Recuerda el nivel inicial (1).

        int ancho = escalar(Juego.ANCHO_MAPA);                   // Ancho del panel = ancho del mapa escalado.
        int alto = escalar(900);                                 // Alto del panel = 900 del modelo escalado.
        setPreferredSize(new java.awt.Dimension(ancho, alto));   // Define el tamaño preferido del panel.
        setBackground(new Color(2, 28, 64));                     // Color de fondo (azul oscuro, agua profunda).
        setFocusable(true);                                      // Permite que el panel reciba foco de teclado.
        addKeyListener(this);                                    // Se registra a sí mismo para escuchar el teclado.

        gameLoop = new Timer(INTERVALO_TICK, this);              // Crea el Timer que avisa cada INTERVALO_TICK ms.
        gameLoop.start();                                        // Arranca el game loop.
    }

    // Convierte una coordenada del modelo a pixeles de pantalla.
    private int escalar(int valorModelo) {
        return (int) Math.round(valorModelo * ESCALA); // Multiplica por la escala y redondea.
    }

    // ----- Game loop -----

    @Override
    public void actionPerformed(ActionEvent e) { // Se ejecuta en cada tick del Timer.
        controlador.actualizarJuego();           // Avanza la lógica del juego un paso.

        if (controlador.getJuego().estaTerminado()) { // Si el juego terminó...
            gameLoop.stop();                          // ...detiene el game loop.
        }

        detectarDetonaciones(); // Revisa qué cargas detonaron para crear explosiones.
        avanzarExplosiones();   // Avanza/limpia las animaciones de explosión.

        // Si subió de nivel, dispara la animación de "LEVEL UP"
        int nivelActual = controlador.getJuego().getNivel(); // Lee el nivel actual.
        if (nivelActual > nivelAnterior) {                   // Si es mayor que el del tick anterior...
            framesLevelUp = DURACION_LEVEL_UP;               // ...arranca la animación LEVEL UP.
        }
        nivelAnterior = nivelActual;                         // Guarda el nivel actual para el próximo tick.

        if (framesLevelUp > 0) {  // Si la animación LEVEL UP está activa...
            framesLevelUp--;      // ...descuenta un tick.
        }

        repaint(); // Pide a Swing que vuelva a dibujar el panel (llama a paintComponent).
    }

    /**
     * Compara las cargas del tick anterior con las actuales: la que desapareció
     * detonó, así que arranca una explosión en su última posición.
     */
    private void detectarDetonaciones() {
        List<CargaProfundidad> cargasActuales = controlador.getJuego().getCargas(); // Lista de cargas que siguen vivas.

        for (CargaProfundidad carga : cargasPrevias) {        // Recorre las cargas que había antes...
            if (!cargasActuales.contains(carga)) {            // ...si alguna ya no está, es que detonó...
                explosiones.add(new Explosion(carga.getPosX(), carga.getPosY())); // ...crea una explosión en su posición.
            }
        }

        cargasPrevias = new ArrayList<>(cargasActuales); // Guarda una copia de las cargas actuales para el próximo tick.
    }

    /**
     * Avanza un paso cada explosión y descarta las que ya terminaron.
     */
    private void avanzarExplosiones() {
        Iterator<Explosion> it = explosiones.iterator(); // Recorre la lista de explosiones.
        while (it.hasNext()) {                           // Mientras queden explosiones...
            Explosion ex = it.next();                    // Toma la siguiente.
            ex.frame++;                                  // Avanza un paso su animación.
            if (ex.frame > DURACION_EXPLOSION) {         // Si ya pasó su duración...
                it.remove();                             // ...la elimina.
            }
        }
    }

    // ----- Dibujado -----

    @Override
    protected void paintComponent(Graphics g) { // Swing llama a este método para dibujar el panel.
        super.paintComponent(g);                // Dibuja el fondo base (limpia el panel).

        Graphics2D g2 = (Graphics2D) g;         // Convierte a Graphics2D para tener más opciones.
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // Activa el suavizado de bordes.

        dibujarFondo(g2); // Dibuja el cielo, el agua y las líneas de límite.

        Juego juego = controlador.getJuego(); // Obtiene el estado del juego.
        if (juego.getSubmarino() == null) {   // Si el juego todavía no se inició (sin submarino)...
            return;                           // ...no dibuja entidades.
        }

        dibujarBarcos(g2, juego);                  // Dibuja los barcos.
        dibujarCargas(g2, juego);                  // Dibuja las cargas que caen.
        dibujarSubmarino(g2, juego.getSubmarino()); // Dibuja el submarino.
        dibujarExplosiones(g2);                    // Dibuja las explosiones en curso.
        dibujarHud(g2, juego);                     // Dibuja los datos (nivel, puntaje, vidas, salud).

        if (framesLevelUp > 0) {  // Si la animación LEVEL UP está activa...
            dibujarLevelUp(g2);   // ...dibuja el cartel.
        }

        if (juego.estaTerminado()) { // Si el juego terminó...
            dibujarGameOver(g2);     // ...dibuja la pantalla de GAME OVER.
        }
    }

    /**
     * Dibuja el cartel "LEVEL UP" con un efecto de zoom que crece mientras se desvanece.
     */
    private void dibujarLevelUp(Graphics2D g2) {
        // progreso va de 1.0 (recién aparece) a 0.0 (se apaga)
        double progreso = framesLevelUp / (double) DURACION_LEVEL_UP;        // Qué tan "fresca" está la animación.
        float alpha = (float) Math.max(0.0, Math.min(1.0, progreso));        // Transparencia (entre 0 y 1).

        int tamanioBase = 56;                                                // Tamaño base de la fuente.
        int tamanio = (int) (tamanioBase * (1.0 + (1.0 - progreso) * 0.6));  // El texto crece a medida que se desvanece.
        g2.setFont(new Font("SansSerif", Font.BOLD, tamanio));               // Aplica la fuente con ese tamaño.

        String texto = "LEVEL UP";                                           // El texto a mostrar.
        int anchoTexto = g2.getFontMetrics().stringWidth(texto);             // Mide el ancho del texto.
        int x = (getWidth() - anchoTexto) / 2;                               // Posición X para centrarlo horizontalmente.
        int y = getHeight() / 2;                                             // Posición Y (mitad de la pantalla).

        // Sombra
        g2.setColor(new Color(0, 0, 0, (int) (alpha * 160)));                // Color negro semitransparente para la sombra.
        g2.drawString(texto, x + 3, y + 3);                                  // Dibuja la sombra un poco corrida.

        // Texto dorado
        g2.setColor(new Color(255, 215, 0, (int) (alpha * 255)));            // Color dorado con la transparencia calculada.
        g2.drawString(texto, x, y);                                          // Dibuja el texto principal.
    }

    private void dibujarFondo(Graphics2D g2) {
        // Franja de cielo + agua
        int superficie = escalar(60);                          // Altura (en pixeles) donde termina el cielo.
        g2.setColor(new Color(120, 190, 255));                 // Celeste para el cielo.
        g2.fillRect(0, 0, getWidth(), superficie);             // Pinta la franja del cielo.

        g2.setColor(new Color(2, 28, 64));                     // Azul oscuro para el agua.
        g2.fillRect(0, superficie, getWidth(), getHeight() - superficie); // Pinta el agua debajo del cielo.

        // Líneas que marcan el rango donde se puede mover el submarino
        g2.setColor(new Color(255, 255, 255, 40));             // Blanco muy transparente.
        int yMin = escalar(Juego.PROFUNDIDAD_MIN_SUBMARINO);   // Y de la profundidad mínima del submarino.
        int yMax = escalar(Juego.PROFUNDIDAD_MAX_SUBMARINO);   // Y de la profundidad máxima del submarino.
        g2.drawLine(0, yMin, getWidth(), yMin);                // Línea del límite superior.
        g2.drawLine(0, yMax, getWidth(), yMax);                // Línea del límite inferior.
    }

    private void dibujarBarcos(Graphics2D g2, Juego juego) {
        int superficie = escalar(45);                          // Altura donde se apoyan los barcos.
        for (BarcoEnemigo barco : juego.getBarcos()) {         // Recorre cada barco activo.
            int x = escalar(barco.getPosicionX());             // Posición X del barco en pixeles.
            int w = escalar(ANCHO_BARCO);                      // Ancho del barco en pixeles.
            int h = escalar(ALTO_BARCO);                       // Alto del barco en pixeles.

            g2.setColor(new Color(70, 200, 90));               // Verde para el casco del barco.
            g2.fillRect(x - w / 2, superficie, w, h);          // Dibuja el cuerpo del barco (centrado en x).

            // Pequeña "torre" del barco
            g2.setColor(new Color(40, 150, 60));               // Verde más oscuro para la torre.
            g2.fillRect(x - w / 6, superficie - h / 2, w / 3, h / 2); // Dibuja una torre arriba del casco.
        }
    }

    private void dibujarCargas(Graphics2D g2, Juego juego) {
        for (CargaProfundidad carga : juego.getCargas()) {     // Recorre cada carga activa.
            int x = escalar(carga.getPosX());                  // Posición X de la carga en pixeles.
            int y = escalar(carga.getPosY());                  // Profundidad de la carga en pixeles.
            int r = escalar(RADIO_CARGA);                      // Tamaño de la carga en pixeles.

            g2.setColor(new Color(255, 90, 70));               // Rojo para la carga.
            g2.fillOval(x - r / 2, y - r / 2, r, r);           // Dibuja la carga como un círculo.
        }
    }

    /**
     * Dibuja cada explosión como un círculo que crece y se desvanece:
     * un resplandor naranja exterior y un núcleo amarillo más brillante.
     */
    private void dibujarExplosiones(Graphics2D g2) {
        for (Explosion ex : explosiones) {                          // Recorre cada explosión activa.
            double progreso = ex.frame / (double) DURACION_EXPLOSION; // Avance de la explosión: 0 -> 1.
            int x = escalar(ex.x);                                  // Posición X de la explosión en pixeles.
            int y = escalar(ex.y);                                  // Posición Y de la explosión en pixeles.

            int radio = escalar((int) (70 * progreso));             // El radio crece con el progreso.
            int alpha = (int) (255 * (1.0 - progreso));             // La opacidad baja con el progreso (se desvanece).

            // Resplandor exterior naranja
            g2.setColor(new Color(255, 140, 0, Math.max(0, alpha / 2)));   // Naranja, mitad de opaco que el núcleo.
            g2.fillOval(x - radio, y - radio, radio * 2, radio * 2);       // Círculo grande del resplandor.

            // Núcleo amarillo
            int radioNucleo = radio / 2;                                   // El núcleo es la mitad del radio.
            g2.setColor(new Color(255, 230, 120, Math.max(0, alpha)));     // Amarillo claro, más opaco.
            g2.fillOval(x - radioNucleo, y - radioNucleo, radioNucleo * 2, radioNucleo * 2); // Círculo del núcleo.
        }
    }

    private void dibujarSubmarino(Graphics2D g2, Submarino sub) {
        int x = escalar(sub.getPosX()); // Posición X del submarino en pixeles.
        int y = escalar(sub.getPosY()); // Profundidad del submarino en pixeles.
        int w = escalar(ANCHO_SUB);     // Ancho del submarino en pixeles.
        int h = escalar(ALTO_SUB);      // Alto del submarino en pixeles.

        // Cuerpo
        g2.setColor(new Color(255, 210, 70));                 // Amarillo para el cuerpo.
        g2.fillRoundRect(x - w / 2, y - h / 2, w, h, h, h);   // Dibuja el cuerpo como rectángulo redondeado.

        // Periscopio
        g2.fillRect(x - 2, y - h, 4, h / 2);                  // Dibuja el periscopio arriba del cuerpo.

        // Ventana
        g2.setColor(new Color(2, 28, 64));                    // Azul oscuro (igual que el fondo) para la ventana.
        g2.fillOval(x + w / 6, y - h / 4, h / 2, h / 2);      // Dibuja la ventanilla circular.
    }

    private void dibujarHud(Graphics2D g2, Juego juego) {
        g2.setColor(Color.WHITE);                             // Texto en blanco.
        g2.setFont(new Font("SansSerif", Font.BOLD, 16));     // Fuente del HUD.

        Submarino sub = juego.getSubmarino();                 // Obtiene el submarino para leer vidas/salud.
        g2.drawString("Nivel: " + juego.getNivel(), 12, 22);  // Muestra el nivel.
        g2.drawString("Puntaje: " + juego.getPuntaje(), 120, 22); // Muestra el puntaje.
        g2.drawString("Vidas: " + sub.getVidas(), 260, 22);   // Muestra las vidas.
        g2.drawString("Salud: " + sub.getSalud(), 360, 22);   // Muestra la salud.
    }

    private void dibujarGameOver(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 160));                 // Negro semitransparente para oscurecer la pantalla.
        g2.fillRect(0, 0, getWidth(), getHeight());           // Cubre toda la pantalla.

        g2.setColor(Color.WHITE);                             // Texto en blanco.
        g2.setFont(new Font("SansSerif", Font.BOLD, 48));     // Fuente grande para el cartel.
        String texto = "GAME OVER";                           // El texto a mostrar.
        int ancho = g2.getFontMetrics().stringWidth(texto);   // Mide el ancho del texto.
        g2.drawString(texto, (getWidth() - ancho) / 2, getHeight() / 2); // Lo dibuja centrado.
    }

    // ----- Teclado -----

    @Override
    public void keyPressed(KeyEvent e) {          // Se ejecuta al presionar una tecla.
        switch (e.getKeyCode()) {                 // Según el código de la tecla...
            case KeyEvent.VK_LEFT:                // Flecha izquierda:
                controlador.moverIzquierda();     // ...mueve el submarino a la izquierda.
                break;
            case KeyEvent.VK_RIGHT:               // Flecha derecha:
                controlador.moverDerecha();       // ...mueve el submarino a la derecha.
                break;
            case KeyEvent.VK_UP:                  // Flecha arriba:
                controlador.moverArriba();        // ...sube el submarino.
                break;
            case KeyEvent.VK_DOWN:                // Flecha abajo:
                controlador.moverAbajo();         // ...baja el submarino.
                break;
            default:                              // Cualquier otra tecla:
                return;                           // ...no hace nada (sale sin redibujar).
        }
        repaint();                                // Redibuja para ver el movimiento al instante.
    }

    @Override
    public void keyReleased(KeyEvent e) { } // No se usa (obligatorio por implementar KeyListener).

    @Override
    public void keyTyped(KeyEvent e) { }    // No se usa (obligatorio por implementar KeyListener).

    /**
     * Una explosión en pantalla: su posición (en coordenadas del modelo)
     * y cuántos ticks lleva animándose.
     */
    private static class Explosion { // Clase interna que representa una explosión en curso.
        final int x;     // Posición X de la explosión (coordenada del modelo).
        final int y;     // Posición Y de la explosión (coordenada del modelo).
        int frame;       // Cuántos ticks lleva la animación.

        Explosion(int x, int y) { // Constructor: ubica la explosión y arranca su contador.
            this.x = x;       // Guarda la X.
            this.y = y;       // Guarda la Y.
            this.frame = 0;   // Empieza en el frame 0.
        }
    }
}
