import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * =====================================================================
 * PERSONA 3
 * =====================================================================
 *
 * Motor de Inferencia por Enumeración para Redes Bayesianas.
 *
 * Implementa el algoritmo que calcula la
 * distribución de probabilidad de una variable de consulta dado
 * un conjunto de evidencias observadas.
 *
 * -----------------------------------------------------------------------
 * FUNDAMENTO TEÓRICO:
 *
 *   P(X | e) = α · ENUMERATE-ALL(vars, e extendida con X=xi)
 *
 * donde:
 *   X    = variable de consulta (lo que se quiere inferir)
 *   e    = evidencia (variables con valores ya observados)
 *   vars = todos los nodos de la red en orden topológico
 *   α    = constante de normalización (para que las probs sumen 1)
 *
 * ENUMERATE-ALL(vars, e):
 *   Si vars está vacía → retornar 1.0
 *   Y = primer nodo de vars
 *   Si Y tiene valor en e (es conocido):
 *     retornar P(Y=e[Y] | padres(Y)) * ENUMERATE-ALL(resto, e)
 *   Si Y no tiene valor (es oculto):
 *     retornar Σ_y [ P(Y=y | padres(Y)) * ENUMERATE-ALL(resto, e ∪ {Y=y}) ]
 * -----------------------------------------------------------------------
 *
 * TRAZA: el motor debe generar una traza que muestre paso a paso
 * cada probabilidad consultada y cada suma sobre variables ocultas.
 * Esto es requerido por el enunciado para "evidenciar su correcto
 * funcionamiento".
 *
 * NOTA PARA PERSONA 3:
 *   - Usas red.obtenerNodo(), red.obtenerRaices() y red.getNodos()
 *     de la clase RedBayesiana (Persona 1).
 *   - Usas nodo.getTableProbabilidad().obtenerProbabilidad()
 *     de la clase CPT (Persona 2).
 *   - No cambies la firma de inferir(); la Persona 4 la llama desde Main.
 */
public class MotorInferencia {

    // La red bayesiana sobre la cual se opera
    private RedBayesiana red;

    // Controla si se imprime la traza detallada paso a paso
    private boolean trazaActiva;

    /**
     * Constructor.
     *
     * @param red         Red bayesiana con estructura y CPTs ya cargadas
     * @param trazaActiva true = imprimir traza paso a paso;
     *                    false = solo imprimir el resultado final
     */
    public MotorInferencia(RedBayesiana red, boolean trazaActiva) {
        this.red         = red;
        this.trazaActiva = trazaActiva;
    }


    // =====================================================================
    // MÉTODO PÚBLICO DE INFERENCIA (Persona 3)
    // =====================================================================

    /**
     * Calcula P(variableConsulta | evidencia) usando enumeración.
     *
     * Este es el único método público. Lo llama la Persona 4 desde Main.
     * No cambies su firma.
     *
     * @param nombreConsulta Nombre de la variable a inferir (ej: "Appointment")
     * @param evidencia      Mapa de variables observadas y su valor conocido
     *                       (ej: {"Train" → "on time"})
     * @return Mapa (valor → probabilidad normalizada), o null si hay error.
     *
     * TODO (Persona 3) — Algoritmo a implementar:
     * -----------------------------------------------------------------------
     * 1. Verificar que nombreConsulta existe en la red:
     *      Nodo nodoConsulta = red.obtenerNodo(nombreConsulta);
     *      Si es null → imprimir error y retornar null
     *
     * 2. Verificar que cada nodo en evidencia.keySet() existe en la red.
     *    Si alguno no existe → imprimir error y retornar null
     *
     * 3. Obtener el orden topológico:
     *      List<Nodo> orden = obtenerOrdenTopologico();
     *
     * 4. Si trazaActiva → llamar a imprimirEncabezadoInferencia(...)
     *
     * 5. Para cada valor posible de la variable de consulta:
     *      a. Crear una copia de evidencia y agregar {nombreConsulta → valorConsulta}
     *      b. Si trazaActiva → imprimir "Calculando P(X = valorConsulta | e)"
     *      c. double prob = enumerarTodo(orden, 0, evidenciaExtendida, "│  ")
     *      d. Guardar en: distribucion.put(valorConsulta, prob)
     *      e. Si trazaActiva → imprimir el resultado parcial
     *
     * 6. Normalizar: distribucionNormalizada = normalizar(distribucion)
     *
     * 7. Llamar a imprimirResultado(...) siempre (con o sin traza)
     *
     * 8. Retornar distribucionNormalizada
     * -----------------------------------------------------------------------
     */
    public Map<String, Double> inferir(String nombreConsulta,
                                       Map<String, String> evidencia) {
        // TODO (Persona 3): implementar según el algoritmo descrito arriba
        System.out.println("  [Motor] Inferencia pendiente de implementar por Persona 3.");
        return null; // placeholder
    }


    // =====================================================================
    // ENUMERATE-ALL — NÚCLEO RECURSIVO (Persona 3)
    // =====================================================================

    /**
     * Recorre recursivamente los nodos en orden topológico calculando
     * la probabilidad conjunta.
     *
     * @param orden     Nodos en orden topológico (raíces primero)
     * @param indice    Posición actual en la lista (0 en la primera llamada)
     * @param evidencia Asignación actual de valores (se extiende en variables ocultas)
     * @param sangria   Texto de sangría para la traza (crece con la recursión)
     * @return Suma de probabilidades conjuntas sobre las variables ocultas
     *
     * TODO (Persona 3) — Algoritmo a implementar:
     * -----------------------------------------------------------------------
     * CASO BASE:
     *   Si indice == orden.size() (no quedan nodos por procesar):
     *     Si trazaActiva → imprimir "término hoja = 1.0"
     *     Retornar 1.0
     *
     * CASO RECURSIVO:
     *   Nodo nodoActual = orden.get(indice)
     *   String nombre   = nodoActual.getNombre()
     *   String claveCondicion = construirClaveCondicion(nodoActual, evidencia)
     *
     *   SI evidencia.containsKey(nombre):
     *     // El nodo tiene valor conocido
     *     String valorConocido = evidencia.get(nombre)
     *     double p = obtenerProbabilidadCPT(nodoActual, valorConocido, claveCondicion)
     *     Si trazaActiva → imprimir "P(nombre=valor | clave) = p"
     *     Retornar p * enumerarTodo(orden, indice+1, evidencia, sangria+"  ")
     *
     *   SI NO (nodo oculto):
     *     Si trazaActiva → imprimir "Σ sobre nombre {oculto}:"
     *     double suma = 0.0
     *     Para cada valor en nodoActual.getValores():
     *       Crear copia de evidencia y agregar {nombre → valor}
     *       double p = obtenerProbabilidadCPT(nodoActual, valor, claveCondicion)
     *       Si trazaActiva → imprimir "P(nombre=valor | clave) = p"
     *       suma += p * enumerarTodo(orden, indice+1, evidenciaExtendida, sangria+"    ")
     *     Si trazaActiva → imprimir "Σ(nombre) = suma"
     *     Retornar suma
     * -----------------------------------------------------------------------
     */
    private double enumerarTodo(List<Nodo> orden, int indice,
                                Map<String, String> evidencia, String sangria) {
        // TODO (Persona 3): implementar según el algoritmo descrito arriba
        return 0.0; // placeholder
    }


    // =====================================================================
    // MÉTODOS AUXILIARES (Persona 3)
    // =====================================================================

    /**
     * Construye la clave de condición para consultar la CPT de un nodo,
     * uniendo los valores actuales de sus padres (en el orden de
     * Nodo.getPadres()) separados por coma.
     *
     * Ejemplo:
     *   Nodo "Train", padres = [Rain, Maintenance]
     *   evidencia = {Rain="none", Maintenance="yes"}
     *   → clave = "none,yes"
     *
     * Para nodos raíz (sin padres) → retornar "" (cadena vacía).
     *
     * @param nodo      Nodo cuya clave se construye
     * @param evidencia Asignación actual de valores
     * @return Clave de condición para buscar en la CPT
     *
     * TODO (Persona 3):
     *   Si nodo.getPadres().isEmpty() → return ""
     *   Para cada padre en nodo.getPadres():
     *     Buscar su valor en evidencia → si null, usar "?" e imprimir error
     *   Unir los valores con ',' sin espacios
     */
    private String construirClaveCondicion(Nodo nodo, Map<String, String> evidencia) {
        // TODO (Persona 3): implementar
        return ""; // placeholder
    }

    /**
     * Consulta la CPT del nodo para obtener P(nodo=valor | claveCondicion).
     *
     * @param nodo           Nodo a consultar
     * @param valor          Valor del nodo (ej: "on time")
     * @param claveCondicion Clave de condición (ej: "none,yes")
     * @return Probabilidad entre 0 y 1, o 0.0 si hay error
     *
     * TODO (Persona 3):
     *   CPT cpt = nodo.getTableProbabilidad();
     *   Si cpt == null → imprimir error y retornar 0.0
     *   double p = cpt.obtenerProbabilidad(valor, claveCondicion);
     *   Retornar (p < 0) ? 0.0 : p
     */
    private double obtenerProbabilidadCPT(Nodo nodo, String valor, String claveCondicion) {
        // TODO (Persona 3): implementar
        return 0.0; // placeholder
    }

    /**
     * Calcula el orden topológico de los nodos de la red usando BFS desde las raíces.
     * En el orden resultante, todo padre aparece ANTES que sus hijos.
     * Esto garantiza que cuando el motor procesa un nodo, los valores de
     * sus padres ya están disponibles en la evidencia.
     *
     * @return Lista de nodos en orden topológico
     *
     * TODO (Persona 3) — Algoritmo (Kahn / BFS modificado):
     * -----------------------------------------------------------------------
     * List<Nodo> resultado   = new ArrayList<>();
     * List<Nodo> cola        = new ArrayList<>(red.obtenerRaices());
     * List<String> visitados = new ArrayList<>();
     *
     * Mientras cola no esté vacía:
     *   Nodo actual = cola.remove(0)  // extraer el primero
     *   Si visitados contiene actual.getNombre() → continuar (ya procesado)
     *
     *   boolean padresListos = true
     *   Para cada padre de actual:
     *     Si visitados NO contiene padre.getNombre():
     *       padresListos = false; break
     *
     *   Si padresListos:
     *     resultado.add(actual)
     *     visitados.add(actual.getNombre())
     *     cola.addAll(actual.getHijos())  // encolar hijos para procesar después
     *   Si no:
     *     cola.add(actual)  // reencolar al final; sus padres aún no están listos
     *
     * Retornar resultado
     * -----------------------------------------------------------------------
     */
    private List<Nodo> obtenerOrdenTopologico() {
        // TODO (Persona 3): implementar según el algoritmo descrito arriba
        return new ArrayList<>(); // placeholder
    }

    /**
     * Normaliza la distribución para que sus probabilidades sumen exactamente 1.
     * Aplica el factor de normalización α = 1 / Σ(probabilidades).
     *
     * @param distribucion Mapa (valor → probabilidad sin normalizar)
     * @return Nuevo mapa con probabilidades normalizadas
     *
     * TODO (Persona 3):
     *   Calcular suma = suma de todos los valores del mapa
     *   Para cada entrada: nuevaProb = valor / suma  (si suma > 0)
     *   Retornar nuevo LinkedHashMap con los valores normalizados
     */
    private Map<String, Double> normalizar(Map<String, Double> distribucion) {
        // TODO (Persona 3): implementar
        return new LinkedHashMap<>(); // placeholder
    }


    // =====================================================================
    // MÉTODOS DE IMPRESIÓN DE TRAZA (Persona 3)
    // =====================================================================

    /**
     * Imprime el encabezado de la consulta antes de iniciar la enumeración.
     * Muestra: variable consultada, evidencia y orden de procesamiento.
     *
     * Ejemplo de salida:
     *   ╔══════════════════════════════════╗
     *   ║    INFERENCIA POR ENUMERACIÓN   ║
     *   ╚══════════════════════════════════╝
     *   Consulta : P(Appointment | e)
     *   Evidencia: Train=on time
     *   Orden    : Rain → Maintenance → Train → Appointment
     *   ──────────────────────────────────
     *
     * TODO (Persona 3): implementar con el formato que prefieras,
     * pero debe mostrar los tres datos indicados.
     */
    private void imprimirEncabezadoInferencia(String nombreConsulta,
                                              Map<String, String> evidencia,
                                              List<Nodo> orden) {
        // TODO (Persona 3): implementar
    }

    /**
     * Imprime el resultado final de la inferencia en formato tabla.
     *
     * Ejemplo de salida para P(Appointment | Train=on time):
     *
     *   ═══════════════════════════════════════════════════
     *   RESULTADO: P(Appointment | Train=on time)
     *   ═══════════════════════════════════════════════════
     *   attend       │  0.9000  ( 90.00%)
     *   miss         │  0.1000  ( 10.00%)
     *   ───────────────────────────────────────────────────
     *
     * TODO (Persona 3): implementar con ese formato aproximado.
     * Llamado tanto desde inferir() con traza como sin traza.
     */
    private void imprimirResultado(String nombreConsulta,
                                   Map<String, String> evidencia,
                                   Map<String, Double> distribucion) {
        // TODO (Persona 3): implementar
    }
}