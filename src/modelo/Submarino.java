package modelo;

/**
 * Representa el submarino controlado por el jugador.
 * Puede moverse horizontal y verticalmente dentro de ciertos límites.
 */
public class Submarino {

    private int posX;
    private int posY;
    private int salud;
    private int vidas;

    private static final int VELOCIDAD_HORIZONTAL = 20;
    private static final int VELOCIDAD_VERTICAL = 25;

    public Submarino(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
        this.salud = 100;
        this.vidas = 3;
    }

    /**
     * Sube el submarino, respetando el límite mínimo de profundidad.
     */
    public void subir() {
        posY -= VELOCIDAD_VERTICAL;

        if (posY < Juego.PROFUNDIDAD_MIN_SUBMARINO) {
            posY = Juego.PROFUNDIDAD_MIN_SUBMARINO;
        }
    }

    /**
     * Baja el submarino, respetando el límite máximo de profundidad.
     */
    public void bajar() {
        posY += VELOCIDAD_VERTICAL;

        if (posY > Juego.PROFUNDIDAD_MAX_SUBMARINO) {
            posY = Juego.PROFUNDIDAD_MAX_SUBMARINO;
        }
    }

    /**
     * Mueve el submarino hacia la izquierda.
     */
    public void moverIzquierda() {
        posX -= VELOCIDAD_HORIZONTAL;

        if (posX < 0) {
            posX = 0;
        }
    }

    /**
     * Mueve el submarino hacia la derecha.
     */
    public void moverDerecha() {
        posX += VELOCIDAD_HORIZONTAL;

        if (posX > Juego.ANCHO_MAPA) {
            posX = Juego.ANCHO_MAPA;
        }
    }

    /**
     * Recibe el efecto de una explosión según la distancia.
     * Devuelve los puntos ganados por esa explosión.
     */
    public int recibirImpacto(double distancia) {
        if (distancia > 100) {
            // Explosión lejana: da puntos y no daña
            return 30;
        } else if (distancia >= 50) {
            // Explosión relativamente cerca: da algo de puntaje y daña 30%
            reducirSalud(30);
            return 10;
        } else if (distancia >= 10) {
            // Explosión muy cerca: no da puntos y daña 50%
            reducirSalud(50);
            return 0;
        } else {
            // Explosión extremadamente cerca: se pierde una vida
            perderVida();
            return 0;
        }
    }

    /**
     * Reduce la salud del submarino.
     * Si la salud llega a 0 o menos, se pierde una vida.
     */
    private void reducirSalud(int porcentaje) {
        salud -= porcentaje;

        if (salud <= 0) {
            perderVida();
        }
    }

    /**
     * Hace perder una vida.
     * Si aún quedan vidas, la salud vuelve a 100.
     */
    public void perderVida() {
        if (vidas > 0) {
            vidas--;
        }

        if (vidas > 0) {
            salud = 100;
        } else {
            salud = 0;
        }
    }

    /**
     * Agrega una vida extra.
     */
    public void agregarVida() {
        vidas++;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public int getSalud() {
        return salud;
    }

    public int getVidas() {
        return vidas;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }
}