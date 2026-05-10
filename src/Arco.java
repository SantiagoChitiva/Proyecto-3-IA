/*Representa un arco dirigido dentro de la red bayesiana
 Conecta un nodo origen (padre) con un nodo destino (hijo)
 p. ej: Rain -> Maintenance (Rain es padre, Maintenance es hijo)
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