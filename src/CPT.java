import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Tabla de Probabilidad Condicional (CPT) de un nodo.
 *
 * Decisiones:
 * - La clave de condición es una cadena con valores de padres separados por coma
 *   y en el orden declarado; para nodos raíz se usa "".
 * - Se usa LinkedHashMap para preservar el orden del archivo al imprimir.
 * - Se conserva el orden de {@code valoresNodo} para mapear probabilidades.
 */
public class CPT {

    // Nombre del nodo propietario de esta tabla
    private String nombreNodo;

    // Mapa condición -> (valor del nodo -> probabilidad)
    private Map<String, Map<String, Double>> tabla;


    // Valores posibles del nodo, en el orden declarado
    private List<String> valoresNodo;


    /**
     * Crea una CPT vacía para el nodo indicado.
     *
     * @param nombreNodo  Nombre del nodo propietario (ej: "Train")
     * @param valoresNodo Lista ordenada de valores posibles del nodo
     */
    public CPT(String nombreNodo, List<String> valoresNodo) {
        this.nombreNodo = nombreNodo;
        this.valoresNodo = valoresNodo;
        this.tabla = new LinkedHashMap<>();
    }


    /**
     * Agrega una fila a la tabla de probabilidad.
     *
    * Llamado por LectorCPT para cada línea CONDICION:
     * del archivo de probabilidades.
     *
     * @param claveCondicion Valores de los padres concatenados por coma,
     *                       sin espacios (ej: "none,yes").
     *                       Para nodos raíz: cadena vacía "".
     * @param distribucion   Mapa (valor del nodo → probabilidad)
     *                       Ej: {"on time" → 0.8, "delayed" → 0.2}
     *
     */
    public void agregarFila(String claveCondicion, Map<String, Double> distribucion) {
        tabla.put(claveCondicion, distribucion);
    }

    /**
     * Devuelve P(nodo = valorNodo | padres = claveCondicion).
     *
    * Método crítico para el motor de inferencia.
     * No cambies su firma.
     *
     * @param valorNodo      Valor del nodo que se quiere evaluar (ej: "on time")
     * @param claveCondicion Clave construida por el motor con los valores de
     *                       los padres en orden (ej: "none,yes")
     * @return La probabilidad como double entre 0 y 1,
     *         o -1.0 si la clave o el valor no se encontraron en la tabla.
     *
     */
    public double obtenerProbabilidad(String valorNodo, String claveCondicion) {
        Map<String, Double> distribucion = tabla.get(claveCondicion);
        if (distribucion == null) {
            System.out.println("Advertencia: No se encontró distribución para condición '" + claveCondicion + "' en CPT de " + nombreNodo);
            return -1.0;
        }
        Double prob = distribucion.get(valorNodo);
        if (prob == null) {
            System.out.println("Advertencia: No se encontró probabilidad para valor '" + valorNodo + "' en condición '" + claveCondicion + "' de " + nombreNodo);
            return -1.0;
        }
        return prob;
    }

    /**
     * Devuelve la distribución de probabilidad completa para una condición.
     * Útil para debug o consultas directas desde Main.
     *
     * @param claveCondicion Clave de condición (ej: "none,yes")
     * @return Mapa (valor → probabilidad), o null si la clave no existe
     *
     */
    public Map<String, Double> obtenerDistribucion(String claveCondicion) {
        return tabla.get(claveCondicion);
    }

    /**
     * Imprime la CPT completa en consola con formato de tabla de texto.
     *
     * Salida esperada para Train:
     *
     *   === CPT: Train ===
     *   Condición              | on time    | delayed
     *   -----------------------+------------+------------
     *   none,yes               |   0.800    |   0.200
     *   none,no                |   0.900    |   0.100
     *   light,yes              |   0.600    |   0.400
     *   ...
     *
     */
    public void imprimirCPT() {
        System.out.println("=== CPT: " + nombreNodo + " ===");
        if (valoresNodo == null || valoresNodo.isEmpty()) {
            System.out.println("  (Sin valores definidos)");
            return;
        }
        // Cabecera
        System.out.print("Condición");
        for (String val : valoresNodo) {
            System.out.print(" | " + val);
        }
        System.out.println();
        // Separador
        System.out.print("---------");
        for (int i = 0; i < valoresNodo.size(); i++) {
            System.out.print("+--------");
        }
        System.out.println();
        // Filas
        for (Map.Entry<String, Map<String, Double>> entry : tabla.entrySet()) {
            String condicion = entry.getKey();
            if (condicion.isEmpty()) {
                condicion = "(raíz)";
            }
            System.out.printf("%-9s", condicion);
            Map<String, Double> dist = entry.getValue();
            for (String val : valoresNodo) {
                Double prob = dist.get(val);
                if (prob != null) {
                    System.out.printf(" | %6.3f", prob);
                } else {
                    System.out.print(" |   ?   ");
                }
            }
            System.out.println();
        }
    }


    public String getNombreNodo()               { return nombreNodo; }

    public List<String> getValoresNodo() {
        return valoresNodo;
    }

    public Map<String, Map<String, Double>> getTabla() {
        return tabla;
    }
}