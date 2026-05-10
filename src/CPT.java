import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * =====================================================================
 * PERSONA 2
 * =====================================================================
 *
 * Tabla de Probabilidad Condicional (CPT - Conditional Probability Table).
 *
 * Almacena, para cada combinación de valores de los padres de un nodo,
 * la distribución de probabilidad del nodo.
 *
 * Ejemplo para el nodo "Train" (padres en orden: Rain, Maintenance):
 *
 *   Clave       | Distribución
 *   "none,yes"  | {on time: 0.8, delayed: 0.2}
 *   "none,no"   | {on time: 0.9, delayed: 0.1}
 *   "light,yes" | {on time: 0.6, delayed: 0.4}
 *   ...
 *
 * Para nodos raíz (sin padres), la clave es cadena vacía "":
 *   ""  →  {none: 0.7, light: 0.2, heavy: 0.1}
 *
 * NOTA PARA PERSONA 2:
 *   Esta clase es usada por la Persona 3 (MotorInferencia) a través del
 *   método obtenerProbabilidad(). El contrato de ese método es fundamental:
 *   debe devolver la probabilidad correcta dada la clave de condición.
 *   No cambies su firma (nombre, parámetros ni tipo de retorno).
 */
public class CPT {

    // Nombre del nodo propietario de esta tabla
    private String nombreNodo;

    /**
     * TODO (Persona 2) — ATRIBUTO PRINCIPAL
     * -----------------------------------------------------------------------
     * Define aquí la estructura interna que almacena la tabla.
     *
     * La estructura recomendada es un Map anidado:
     *   - Clave externa: String con los valores de los padres separados por
     *     coma (ej: "none,yes" para Rain=none, Maintenance=yes).
     *     Para nodos raíz, la clave es "" (cadena vacía).
     *   - Valor: Map<String, Double> que mapea cada valor del nodo a su
     *     probabilidad (ej: {"on time" → 0.8, "delayed" → 0.2}).
     *
     * Usar LinkedHashMap preserva el orden de inserción del archivo,
     * lo cual ayuda al imprimir la tabla en el mismo orden que el archivo.
     *
     * Ejemplo de declaración:
     *   private Map<String, Map<String, Double>> tabla = new LinkedHashMap<>();
     * -----------------------------------------------------------------------
     */
    // TODO (Persona 2): declarar el atributo "tabla" aquí


    /**
     * TODO (Persona 2) — ATRIBUTO AUXILIAR
     * -----------------------------------------------------------------------
     * Valores posibles del nodo en orden (ej: ["on time", "delayed"]).
     * Son necesarios para:
     *   1. Imprimir correctamente la cabecera de la tabla.
     *   2. Saber con qué valor del nodo corresponde cada probabilidad
     *      al leer el archivo (la Persona 2 los usa en LectorCPT).
     *
     * Ejemplo de declaración:
     *   private List<String> valoresNodo;
     * -----------------------------------------------------------------------
     */
    // TODO (Persona 2): declarar el atributo "valoresNodo" aquí


    /**
     * Constructor: crea una CPT vacía para el nodo indicado.
     *
     * @param nombreNodo  Nombre del nodo propietario (ej: "Train")
     * @param valoresNodo Lista ordenada de valores posibles del nodo
     *                    (ej: ["on time", "delayed"])
     *
     * TODO (Persona 2):
     *   Inicializa aquí los atributos: nombreNodo, valoresNodo y tabla.
     *   Para tabla, usar: new LinkedHashMap<>()
     */
    public CPT(String nombreNodo, List<String> valoresNodo) {
        this.nombreNodo = nombreNodo;
        // TODO (Persona 2): inicializar valoresNodo y tabla
    }


    // =====================================================================
    // MÉTODOS QUE DEBES IMPLEMENTAR (Persona 2)
    // =====================================================================

    /**
     * Agrega una fila a la tabla de probabilidad.
     *
     * Llamado por LectorCPT (también Persona 2) para cada línea CONDICION:
     * del archivo de probabilidades.
     *
     * @param claveCondicion Valores de los padres concatenados por coma,
     *                       sin espacios (ej: "none,yes").
     *                       Para nodos raíz: cadena vacía "".
     * @param distribucion   Mapa (valor del nodo → probabilidad)
     *                       Ej: {"on time" → 0.8, "delayed" → 0.2}
     *
     * TODO (Persona 2):
     *   Guarda la fila en el atributo "tabla":
     *     tabla.put(claveCondicion, distribucion);
     */
    public void agregarFila(String claveCondicion, Map<String, Double> distribucion) {
        // TODO (Persona 2): implementar
    }

    /**
     * Devuelve P(nodo = valorNodo | padres = claveCondicion).
     *
     * *** MÉTODO CRÍTICO: lo usa el motor de inferencia (Persona 3). ***
     * No cambies su firma.
     *
     * @param valorNodo      Valor del nodo que se quiere evaluar (ej: "on time")
     * @param claveCondicion Clave construida por el motor con los valores de
     *                       los padres en orden (ej: "none,yes")
     * @return La probabilidad como double entre 0 y 1,
     *         o -1.0 si la clave o el valor no se encontraron en la tabla.
     *
     * TODO (Persona 2):
     *   1. Buscar la distribución: tabla.get(claveCondicion)
     *   2. Si es null → imprimir aviso y retornar -1.0
     *   3. Buscar la probabilidad: distribucion.get(valorNodo)
     *   4. Si es null → imprimir aviso y retornar -1.0
     *   5. Retornar la probabilidad encontrada
     */
    public double obtenerProbabilidad(String valorNodo, String claveCondicion) {
        // TODO (Persona 2): implementar
        return -1.0; // placeholder, reemplazar con la lógica real
    }

    /**
     * Devuelve la distribución de probabilidad completa para una condición.
     * Útil para debug o consultas directas desde Main.
     *
     * @param claveCondicion Clave de condición (ej: "none,yes")
     * @return Mapa (valor → probabilidad), o null si la clave no existe
     *
     * TODO (Persona 2):
     *   return tabla.get(claveCondicion);
     */
    public Map<String, Double> obtenerDistribucion(String claveCondicion) {
        // TODO (Persona 2): implementar
        return null; // placeholder
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
     * TODO (Persona 2):
     *   1. Imprimir la línea de título "=== CPT: nombreNodo ==="
     *   2. Imprimir la cabecera: columna "Condición" + una columna por valor en valoresNodo
     *   3. Imprimir una línea separadora de guiones y "+"
     *   4. Recorrer tabla.entrySet() e imprimir cada fila:
     *      - Columna condición: la clave del mapa (mostrar "(raíz)" si es vacía)
     *      - Columnas de prob: para cada valor en valoresNodo, buscar en la distribución
     *   Tip: usar String.format() para alinear columnas con ancho fijo.
     */
    public void imprimirCPT() {
        // TODO (Persona 2): implementar
        System.out.println("  === CPT: " + nombreNodo
                + " === [pendiente de implementar por Persona 2]");
    }


    // ---------------------------------------------------------------
    // Getters (ya implementados, no modificar)
    // ---------------------------------------------------------------

    public String getNombreNodo()               { return nombreNodo; }

    /**
     * TODO (Persona 2): implementar este getter una vez que declares valoresNodo.
     * Reemplazar el cuerpo con:  return valoresNodo;
     */
    public List<String> getValoresNodo() {
        // TODO (Persona 2): return valoresNodo;
        return null;
    }

    /**
     * TODO (Persona 2): implementar este getter una vez que declares tabla.
     * Reemplazar el cuerpo con:  return tabla;
     */
    public Map<String, Map<String, Double>> getTabla() {
        // TODO (Persona 2): return tabla;
        return null;
    }
}