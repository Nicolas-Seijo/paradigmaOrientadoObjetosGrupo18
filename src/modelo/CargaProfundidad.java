package modelo;

/**
 * Representa una carga de profundidad lanzada por un barco enemigo.
 * Cae en línea recta y explota al llegar a cierta profundidad.
 */
public class CargaProfundidad {

    private int posX;
    private int posY;
    private int profundidadDetonacion;
    private double velocidad;

    public CargaProfundidad(int posX, int posY, int profundidadDetonacion, double velocidad) {
        this.posX = posX;
        this.posY = posY;
        this.profundidadDetonacion = profundidadDetonacion;
        this.velocidad = velocidad;
    }

    /**
     * Hace caer la carga hacia mayor profundidad.
     */
    public void caer() {
        posY += velocidad;
    }

    /**
     * Indica si ya llegó a la profundidad donde debe explotar.
     */
    public boolean debeExplotar() {
        return posY >= profundidadDetonacion;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public int getProfundidadDetonacion() {
        return profundidadDetonacion;
    }

    public double getVelocidad() {
        return velocidad;
    }

    public void setPosY(int posY) {this.posY = posY;}
}