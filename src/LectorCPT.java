import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * =====================================================================
 * PERSONA 2
 * =====================================================================
 *
 * Lee el archivo de tablas de probabilidad condicional y las asigna
 * a los nodos de la red bayesiana mediante red.asignarCPT().
 *
 * -----------------------------------------------------------------------
 * FORMATO DEL ARCHIVO probabilidades.txt:
 * -----------------------------------------------------------------------
 * El archivo se divide en bloques, uno por nodo. Cada bloque:
 *
 *   [NombreNodo]
 *   VALORES: val1, val2, ...
 *   CONDICION: val_padre1, val_padre2, ... | prob1, prob2, ...
 *   CONDICION: ...
 *
 * Para nodos raíz (sin padres), la condición antes del '|' es vacía:
 *   CONDICION: | 0.7, 0.2, 0.1
 *
 * Líneas con '#' son comentarios. Líneas vacías se ignoran.
 *
 * Ejemplo completo (red del ejemplo de clase):
 *
 *   [Rain]
 *   VALORES: none, light, heavy
 *   CONDICION: | 0.7, 0.2, 0.1
 *
 *   [Maintenance]
 *   VALORES: yes, no
 *   CONDICION: none  | 0.4, 0.6
 *   CONDICION: light | 0.2, 0.8
 *   CONDICION: heavy | 0.1, 0.9
 *
 *   [Train]
 *   VALORES: on time, delayed
 *   CONDICION: none, yes  | 0.8, 0.2
 *   CONDICION: none, no   | 0.9, 0.1
 *   CONDICION: light, yes | 0.6, 0.4
 *   CONDICION: light, no  | 0.7, 0.3
 *   CONDICION: heavy, yes | 0.4, 0.6
 *   CONDICION: heavy, no  | 0.5, 0.5
 *
 *   [Appointment]
 *   VALORES: attend, miss
 *   CONDICION: on time | 0.9, 0.1
 *   CONDICION: delayed | 0.6, 0.4
 * -----------------------------------------------------------------------
 */
public class LectorCPT {

    /**
     * Lee el archivo de probabilidades y asigna cada CPT al nodo
     * correspondiente de la red.
     *
     * @param rutaArchivo Ruta al archivo de probabilidades (ej: "probabilidades.txt")
     * @param red         Red bayesiana ya construida (con nodos y arcos cargados)
     *
     * TODO (Persona 2) — Algoritmo a implementar:
     * -----------------------------------------------------------------------
     * Variables de estado del bloque actual:
     *   String nombreNodoActual   → nombre del nodo en el bloque "[NombreNodo]"
     *   List<String> valoresActuales → valores del nodo (línea "VALORES:")
     *   CPT cptActual             → la CPT que se está construyendo
     *
     * Para cada línea del archivo:
     *
     *   Si empieza con '#' o está vacía → ignorar (continue)
     *
     *   Si empieza con '[' y termina con ']':
     *     → Es el inicio de un bloque nuevo.
     *     → Si cptActual != null, cerrar el bloque anterior:
     *          red.asignarCPT(nombreNodoActual, cptActual);
     *     → Extraer el nombre: texto entre '[' y ']'
     *     → Resetear valoresActuales = null y cptActual = null
     *
     *   Si empieza con "VALORES:":
     *     → Extraer el texto después de "VALORES:" y hacer trim()
     *     → Separar por ',' → lista de strings con trim() a cada elemento
     *     → Crear: cptActual = new CPT(nombreNodoActual, valoresActuales)
     *
     *   Si empieza con "CONDICION:":
     *     → Extraer el texto después de "CONDICION:" y hacer trim()
     *     → Llamar a parsearCondicion(texto, valoresActuales)
     *     → Si el resultado no es null:
     *          String clave = (String) resultado[0];
     *          Map<String,Double> dist = (Map<String,Double>) resultado[1];
     *          cptActual.agregarFila(clave, dist);
     *
     * Al terminar el while (fin de archivo):
     *   → Si cptActual != null → red.asignarCPT(nombreNodoActual, cptActual);
     *
     * USAR try-with-resources para el BufferedReader (manejo de IOException).
     * -----------------------------------------------------------------------
     */
    public static void leerProbabilidades(String rutaArchivo, RedBayesiana red) {
        // TODO (Persona 2): implementar según el algoritmo descrito arriba
        try (BufferedReader lector = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            String nombreNodoActual = null;
            List<String> valoresActuales = null;
            CPT cptActual = null;

            while ((linea = lector.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty() || linea.startsWith("#")) continue;

                if (linea.startsWith("[") && linea.endsWith("]")) {
                    // Nuevo bloque
                    if (cptActual != null) {
                        red.asignarCPT(nombreNodoActual, cptActual);
                    }
                    nombreNodoActual = linea.substring(1, linea.length() - 1).trim();
                    valoresActuales = null;
                    cptActual = null;
                } else if (linea.startsWith("VALORES:")) {
                    String valoresStr = linea.substring(8).trim();
                    valoresActuales = new ArrayList<>();
                    for (String v : valoresStr.split(",")) {
                        valoresActuales.add(v.trim());
                    }
                    cptActual = new CPT(nombreNodoActual, valoresActuales);
                } else if (linea.startsWith("CONDICION:")) {
                    if (cptActual != null && valoresActuales != null) {
                        String resto = linea.substring(10).trim();
                        Object[] resultado = parsearCondicion(resto, valoresActuales);
                        if (resultado != null) {
                            String clave = (String) resultado[0];
                            Map<String, Double> dist = (Map<String, Double>) resultado[1];
                            cptActual.agregarFila(clave, dist);
                        }
                    }
                }
            }
            // Asignar el último
            if (cptActual != null) {
                red.asignarCPT(nombreNodoActual, cptActual);
            }
            System.out.println("  Probabilidades cargadas.");
        } catch (IOException e) {
            System.out.println("Error: No se pudo leer: " + rutaArchivo);
            System.out.println("Causa: " + e.getMessage());
        }
    }

    /**
     * Parsea una línea de condición con formato:
     *   "val_padre1, val_padre2, ... | prob1, prob2, ..."
     *
     * La parte izquierda del '|' es la condición (clave de la CPT).
     * La parte derecha son las probabilidades en el mismo orden que valoresNodo.
     * Para nodos raíz, la parte izquierda está vacía: " | 0.7, 0.2, 0.1"
     *
     * @param texto       Texto de la línea CONDICION (sin el prefijo "CONDICION:")
     * @param valoresNodo Lista ordenada de valores del nodo actual
     * @return Object[2] = { String clave, Map<String,Double> distribucion }
     *         o null si el formato es inválido.
     *
     * TODO (Persona 2) — Algoritmo a implementar:
     * -----------------------------------------------------------------------
     * 1. Buscar la posición del '|' con texto.indexOf('|')
     *    Si no existe → imprimir error y retornar null
     *
     * 2. Parte izquierda (condición):
     *    texto.substring(0, posBarra).trim()
     *    Si está vacía → clave = ""  (nodo raíz)
     *    Si no:
     *      Separar por ',' → para cada parte hacer .trim()
     *      Unir con ',' sin espacios → esa es la clave
     *      Ejemplo: " none , yes " → "none,yes"
     *
     * 3. Parte derecha (probabilidades):
     *    texto.substring(posBarra + 1).trim()
     *    Separar por ',' → array de strings
     *    Si la cantidad no coincide con valoresNodo.size() → error y null
     *
     * 4. Construir el mapa distribución:
     *    Map<String, Double> distribucion = new LinkedHashMap<>();
     *    Para i = 0 hasta valoresNodo.size()-1:
     *      double prob = Double.parseDouble(tokensProb[i].trim());
     *      distribucion.put(valoresNodo.get(i), prob);
     *    Si Double.parseDouble lanza NumberFormatException → error y null
     *
     * 5. Retornar new Object[]{ clave, distribucion }
     * -----------------------------------------------------------------------
     */
    private static Object[] parsearCondicion(String texto, List<String> valoresNodo) {
        // TODO (Persona 2): implementar según el algoritmo descrito arriba
        int posBarra = texto.indexOf('|');
        if (posBarra == -1) {
            System.out.println("Error: Falta '|' en condición: " + texto);
            return null;
        }
        String parteIzq = texto.substring(0, posBarra).trim();
        String parteDer = texto.substring(posBarra + 1).trim();

        String clave;
        if (parteIzq.isEmpty()) {
            clave = "";
        } else {
            String[] tokensCond = parteIzq.split(",");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < tokensCond.length; i++) {
                sb.append(tokensCond[i].trim());
                if (i < tokensCond.length - 1) sb.append(",");
            }
            clave = sb.toString();
        }

        String[] tokensProb = parteDer.split(",");
        if (tokensProb.length != valoresNodo.size()) {
            System.out.println("Error: Número de probabilidades no coincide con valores del nodo. Esperado: " + valoresNodo.size() + ", encontrado: " + tokensProb.length);
            return null;
        }

        Map<String, Double> distribucion = new LinkedHashMap<>();
        try {
            for (int i = 0; i < valoresNodo.size(); i++) {
                double prob = Double.parseDouble(tokensProb[i].trim());
                distribucion.put(valoresNodo.get(i), prob);
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Probabilidad no numérica en: " + parteDer);
            return null;
        }

        return new Object[]{clave, distribucion};
    }
}