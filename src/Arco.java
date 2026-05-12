/**
 * Representa un arco dirigido en la red bayesiana.
 *
 * Decisiones: se guardan referencias directas a los nodos para evitar
 * búsquedas por nombre y el arco se imprime con un formato simple de
 * depuración.
 */
public class Arco {
    // Nodo de origen del arco: padre
    private Nodo origen;

    // Nodo de destino del arco: hijo
    private Nodo destino;

    public Arco(Nodo origen, Nodo destino) {
        this.origen  = origen;
        this.destino = destino;
    }

    /*Imprime el arco en consola
     p. ej: Rain -> Maintenance
     */
    public void imprimirArco() {
        System.out.println("  " + origen.getNombre() + " -> " + destino.getNombre());
    }

    // Getters
    public Nodo getOrigen()  { return origen; }
    public Nodo getDestino() { return destino; }
}