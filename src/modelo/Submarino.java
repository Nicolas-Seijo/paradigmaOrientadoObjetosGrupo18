package modelo; // Paquete "modelo": clases de la lógica del juego.

/**
 * Representa el submarino controlado por el jugador.
 * Puede moverse horizontal y verticalmente dentro de ciertos límites.
 */
public class Submarino { // Declara la clase pública Submarino.

    private int posX;  // Posición horizontal del submarino.
    private int posY;  // Profundidad del submarino.
    private int salud; // Salud actual (de 0 a 100).
    private int vidas; // Cantidad de vidas restantes.

    private static final int PASO_HORIZONTAL = 20; // Cuánto se mueve a los lados por cada tecla.
    private static final int PASO_VERTICAL = 25;   // Cuánto sube/baja por cada tecla.

    // Constructor: ubica al submarino y le da salud y vidas iniciales.
    public Submarino(int posX, int posY) {
        this.posX = posX;   // Guarda la posición horizontal inicial.
        this.posY = posY;   // Guarda la profundidad inicial.
        this.salud = 100;   // Empieza con salud completa (100).
        this.vidas = 3;     // Empieza con 3 vidas.
    }

    /**
     * Sube el submarino, respetando el límite mínimo de profundidad.
     */
    public void subir() {                                  // Mueve el submarino hacia arriba.
        posY -= PASO_VERTICAL;                             // Resta a la profundidad (sube).
        if (posY < Juego.PROFUNDIDAD_MIN_SUBMARINO) {      // Si se pasó del límite superior...
            posY = Juego.PROFUNDIDAD_MIN_SUBMARINO;        // ...lo fija en el mínimo permitido (300).
        }
    }

    /**
     * Baja el submarino, respetando el límite máximo de profundidad.
     */
    public void bajar() {                                  // Mueve el submarino hacia abajo.
        posY += PASO_VERTICAL;                             // Suma a la profundidad (baja).
        if (posY > Juego.PROFUNDIDAD_MAX_SUBMARINO) {      // Si se pasó del límite inferior...
            posY = Juego.PROFUNDIDAD_MAX_SUBMARINO;        // ...lo fija en el máximo permitido (800).
        }
    }

    /**
     * Mueve el submarino hacia la izquierda.
     */
    public void moverIzquierda() {     // Mueve el submarino a la izquierda.
        posX -= PASO_HORIZONTAL;       // Resta a la posición horizontal.
        if (posX < 0) {                // Si se pasó del borde izquierdo...
            posX = 0;                  // ...lo fija en 0 (no sale del mapa).
        }
    }

    /**
     * Mueve el submarino hacia la derecha.
     */
    public void moverDerecha() {           // Mueve el submarino a la derecha.
        posX += PASO_HORIZONTAL;           // Suma a la posición horizontal.
        if (posX > Juego.ANCHO_MAPA) {     // Si se pasó del borde derecho...
            posX = Juego.ANCHO_MAPA;       // ...lo fija en el ancho del mapa (1000).
        }
    }

    /**
     * Recibe el efecto de una explosión según la distancia.
     * Devuelve los puntos ganados por esa explosión.
     */
    public int recibirImpacto(double distancia) { // Aplica daño/puntos según qué tan cerca explotó.
        if (distancia > 100) {                     // Explosión lejana (más de 100):
            return 30;                             // da 30 puntos y no hace daño.
        } else if (distancia >= 50) {              // Explosión entre 50 y 100:
            reducirSalud(30);                      // baja la salud un 30%...
            return 10;                             // ...y da 10 puntos.
        } else if (distancia >= 10) {              // Explosión entre 10 y 50:
            reducirSalud(50);                      // baja la salud un 50%...
            return 0;                              // ...y no da puntos.
        } else {                                   // Explosión muy cerca (menos de 10):
            perderVida();                          // hace perder una vida...
            return 0;                              // ...y no da puntos.
        }
    }

    /**
     * Reduce la salud del submarino.
     * Si la salud llega a 0 o menos, se pierde una vida.
     */
    private void reducirSalud(int porcentaje) { // Baja la salud en la cantidad indicada.
        salud -= porcentaje;                    // Resta el porcentaje a la salud.
        if (salud <= 0) {                       // Si la salud se agotó...
            perderVida();                        // ...se pierde una vida.
        }
    }

    /**
     * Hace perder una vida.
     * Si aún quedan vidas, la salud vuelve a 100.
     */
    public void perderVida() {     // Quita una vida y reinicia la salud si todavía quedan.
        if (vidas > 0) {           // Si todavía hay vidas...
            vidas--;               // ...resta una.
        }
        if (vidas > 0) {           // Si después de restar aún quedan vidas...
            salud = 100;           // ...la salud vuelve a 100.
        } else {                   // Si ya no quedan vidas...
            salud = 0;             // ...la salud queda en 0 (juego terminado).
        }
    }

    /**
     * Agrega una vida extra.
     */
    public void agregarVida() {    // Suma una vida (premio por puntaje).
        vidas++;                   // Incrementa el contador de vidas.
    }

    public int getPosX() {         // Getter: posición horizontal.
        return posX;               // Devuelve posX.
    }

    public int getPosY() {         // Getter: profundidad.
        return posY;               // Devuelve posY.
    }

    public int getSalud() {        // Getter: salud actual.
        return salud;              // Devuelve salud.
    }

    public int getVidas() {        // Getter: vidas restantes.
        return vidas;              // Devuelve vidas.
    }

    public void setPosX(int posX) { // Setter: cambia la posición horizontal.
        this.posX = posX;           // Asigna el nuevo valor.
    }

    public void setPosY(int posY) { // Setter: cambia la profundidad.
        this.posY = posY;           // Asigna el nuevo valor.
    }
}
