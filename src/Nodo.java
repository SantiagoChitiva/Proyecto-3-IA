import java.util.ArrayList;
import java.util.List;

/*Representa un nodo dentro de la red bayesiana
 Cada nodo corresponde a una variable aleatoria con sus posibles valores
 y su tabla de probabilidad (CPT) asociada.
 p. ej: el nodo "Rain" puede tomar los valores {none, light, heavy}.
 */
public class Nodo {

    // Nombre identificador del nodo (ej: "Rain", "Train")
    private String nombre;

    // Valores posibles que puede tomar la variable (ej: ["none","light","heavy"])
    private List<String> valores;

    // Lista de nodos padres
    private List<Nodo> padres;

    // Lista de nodos hijos
    private List<Nodo> hijos;

    // Tabla de probabilidad asociada a este nodo.
    private CPT tablaProbabilidad;

    public Nodo(String nombre, String valores) {
        this.nombre = nombre;
        this.padres = new ArrayList<>();
        this.hijos = new ArrayList<>();
        this.tablaProbabilidad = null;

        // Parsear la cadena de valores separados por coma
        this.valores = new ArrayList<>();
        for (String v : valores.split(",")) {
            this.valores.add(v.trim());
        }
    }

    //Métodos de relación entre nodos

    //Agrega un nodo padre a este nodo (solo lo agrega si no estaba ya en la lista)
    public void agregarPadre(Nodo padre) {
        if (!padres.contains(padre)) {
            padres.add(padre);
        }
    }

    //Agrega un nodo hijo a este nodo (solo lo agrega si no estaba ya en la lista)
    public void agregarHijo(Nodo hijo) {
        if (!hijos.contains(hijo)) {
            hijos.add(hijo);
        }
    }

    // Visualización


    //Muestra en consola el nombre, los valores posibles y los padres del nodo
    public void imprimirNodo() {
        System.out.print("  Nodo: " + nombre);
        System.out.print("  |  Valores: {");
        for (int i = 0; i < valores.size(); i++) {
            System.out.print(valores.get(i));
            if (i < valores.size() - 1) System.out.print(", ");
        }
        System.out.print("}");

        if (padres.isEmpty()) {
            System.out.println("  |  Sin padres (nodo raíz)");
        } else {
            System.out.print("  |  Padres: [");
            for (int i = 0; i < padres.size(); i++) {
                System.out.print(padres.get(i).getNombre());
                if (i < padres.size() - 1) System.out.print(", ");
            }
            System.out.println("]");
        }
    }

    // Getters y Setters
    public String getNombre() { return nombre; }
    public List<String> getValores() { return valores; }
    public List<Nodo> getPadres() { return padres; }
    public List<Nodo> getHijos() { return hijos; }
    public CPT getTableProbabilidad() { return tablaProbabilidad; }
    public boolean tieneCPT() { return tablaProbabilidad != null; }

    //Asigna la tabla de probabilidad condicional a este nodo.
    public void setCPT(CPT cpt) {
        this.tablaProbabilidad = cpt;
    }
}