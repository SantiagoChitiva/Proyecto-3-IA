import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/*Representa la red bayesiana completa como un grafo dirigido
 Contiene todos los nodos y arcos
 - Persona 2: llama a asignarCPT() para adjuntar las tablas de probabilidad.
 - Persona 3: usa obtenerNodo(), obtenerRaices() y getNodos() para recorrer la red durante la inferencia.
 - Persona 4: usa inferir() del motor (que a su vez usa esta clase) para ejecutar las consultas de validación.
 */
public class RedBayesiana {
    // Mapa de todos los nodos: nombre → objeto Nodo.
    // Se usa LinkedHashMap para conservar el orden de inserción del archivo.
    private Map<String, Nodo> nodos;

    // Lista de todos los arcos de la red
    private List<Arco> arcos;

    //Constructor inicializa una red bayesiana vacía.
    public RedBayesiana() {
        this.nodos = new LinkedHashMap<>();
        this.arcos = new ArrayList<>();
    }

    // Construcción del grafo

    /*Agrega un nodo a la red si aún no existe uno con ese nombre
     Si ya existe, lo ignora (evita duplicados al leer el archivo)
     */
    public void agregarNodo(Nodo nodo) {
        if (!nodos.containsKey(nodo.getNombre())) {
            nodos.put(nodo.getNombre(), nodo);
        }
    }

    /*Agrega un arco dirigido entre dos nodos ya presentes en la red
     Además actualiza las listas de padres e hijos de cada nodo
     */
    public void agregarArco(String nombreOrigen, String nombreDestino) {
        Nodo origen  = nodos.get(nombreOrigen);
        Nodo destino = nodos.get(nombreDestino);

        if (origen == null || destino == null) {
            System.out.println("Error: Nodo no encontrado: "
                    + nombreOrigen + " o " + nombreDestino);
            return;
        }

        Arco arco = new Arco(origen, destino);
        arcos.add(arco);

        // Registrar la relación en ambos nodos
        origen.agregarHijo(destino);
        destino.agregarPadre(origen);
    }

    //Busca y devuelve un nodo de la red por su nombre.
    public Nodo obtenerNodo(String nombre) {
        return nodos.get(nombre);
    }

    // ---------------------------------------------------------------
    // Punto de integración con Persona 2
    // ---------------------------------------------------------------

    /*Asigna una tabla de probabilidad condicional (CPT) a un nodo de la red
     La Persona 2 llama a este método desde LectorCPT una vez que ha parseado el bloque correspondiente del archivo de probabilidades
     * @param nombreNodo Nombre del nodo al que pertenece la CPT
     * @param cpt        Tabla de probabilidad ya construida
     */
    public void asignarCPT(String nombreNodo, CPT cpt) {
        Nodo nodo = nodos.get(nombreNodo);
        if (nodo == null) {
            System.out.println("Error: Nodo no encontrado para asignar CPT: " + nombreNodo);
            return;
        }
        nodo.setCPT(cpt);
    }

    // Visualización de la estructura

    //Devuelve todos los nodos raíz de la red (los que no tienen padres).
    public List<Nodo> obtenerRaices() {
        List<Nodo> raices = new ArrayList<>();
        for (Nodo nodo : nodos.values()) {
            if (nodo.getPadres().isEmpty()) {
                raices.add(nodo);
            }
        }
        return raices;
    }

    /*Imprime la estructura completa de la red en formato árbol de texto,
    comenzando desde los nodos raíz y mostrando para cada nodo
    cuáles son sus padres
    Ejemplo:
     *   === Estructura de la Red Bayesiana ===
     *
     *   [Raíz] Rain  {none, light, heavy}  |  Sin padres
     *     └──► Maintenance  {yes, no}  [padres: Rain]
     *     └──► Train  {on time, delayed}  [padres: Rain, Maintenance]
     *            └──► Appointment  {attend, miss}  [padres: Train]
     *
     *   --- Arcos ---
     *     Rain ──► Maintenance
     *     Rain ──► Train
     *     ...
     */
    public void imprimirEstructura() {
        System.out.println("\nEstructura de la Red Bayesiana\n");

        List<Nodo> raices = obtenerRaices();
        if (raices.isEmpty()) {
            System.out.println("No se encontraron nodos raíz");
            return;
        }

        // Recorrer en profundidad (DFS) desde cada raíz
        for (Nodo raiz : raices) {
            imprimirNodoRecursivo(raiz, 0, new ArrayList<>());
        }

        // Lista de arcos al final
        System.out.println("\nArcos de la red");
        for (Arco arco : arcos) {
            arco.imprimirArco();
        }
        System.out.println();
    }

    /*Auxiliar recursivo para imprimir la estructura en forma de árbol.
     Usa sangría proporcional al nivel de profundidad del nodo.
     */
    private void imprimirNodoRecursivo(Nodo nodo, int nivel, List<String> visitados) {
        String sangria = "  ".repeat(nivel);
        String prefijo = (nivel == 0) ? "[Raíz] " : "└──► ";

        // Descripción de los padres
        String infoPadres = "";
        if (!nodo.getPadres().isEmpty()) {
            StringBuilder sb = new StringBuilder("  [padres: ");
            List<Nodo> padres = nodo.getPadres();
            for (int i = 0; i < padres.size(); i++) {
                sb.append(padres.get(i).getNombre());
                if (i < padres.size() - 1) sb.append(", ");
            }
            sb.append("]");
            infoPadres = sb.toString();
        }

        // Valores posibles del nodo
        String valores = "  {" + String.join(", ", nodo.getValores()) + "}";

        System.out.println(sangria + prefijo + nodo.getNombre() + valores + infoPadres);

        // Si ya fue impreso (nodo con múltiples padres), no repetir sus hijos
        if (visitados.contains(nodo.getNombre())) {
            System.out.println(sangria + "  (ya mostrado)");
            return;
        }
        visitados.add(nodo.getNombre());

        // Visitar hijos recursivamente
        for (Nodo hijo : nodo.getHijos()) {
            imprimirNodoRecursivo(hijo, nivel + 1, visitados);
        }
    }

    // Visualización de CPTs (Persona 2 implementa CPT.imprimirCPT())

    /*Imprime todas las tablas de probabilidad condicional de la red
     * Este método ya está conectado: cuando la Persona 2 implemente
     * CPT.imprimirCPT(), este método la llamará automáticamente
     * para cada nodo que tenga CPT asignada.
     */
    public void imprimirTodasCPT() {
        System.out.println("\nTablas de Probabilidad\n");
        for (Nodo nodo : nodos.values()) {
            if (nodo.tieneCPT()) {
                nodo.getTableProbabilidad().imprimirCPT();
                System.out.println();
            } else {
                System.out.println("  [" + nodo.getNombre() + "] Sin CPT asignada.\n");
            }
        }
    }

    // Getters

    public Map<String, Nodo> getNodos() { return nodos; }
    public List<Arco> getArcos()        { return arcos; }
}