import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Lector de tablas de probabilidad condicional.
 *
 * Decisiones:
 * - El archivo se procesa por bloques definidos por [NombreNodo].
 * - Se ignoran líneas vacías y comentarios con '#'.
 * - En nodos raíz, la condición es la cadena vacía antes del '|'.
 */
public class LectorCPT {

    /**
     * Lee el archivo de probabilidades y asigna cada CPT al nodo correspondiente.
     *
     * @param rutaArchivo Ruta al archivo de probabilidades (ej: "probabilidades.txt")
     * @param red         Red bayesiana ya construida (con nodos y arcos cargados)
     */
    public static void leerProbabilidades(String rutaArchivo, RedBayesiana red) {
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
     * La parte izquierda define la condición y la parte derecha las probabilidades
     * en el mismo orden de {@code valoresNodo}. Para nodos raíz, la condición es vacía.
     *
     * @param texto       Texto de la línea CONDICION (sin el prefijo "CONDICION:")
     * @param valoresNodo Lista ordenada de valores del nodo actual
     * @return Object[2] = { String clave, Map<String,Double> distribucion }
     *         o null si el formato es inválido.
     */
    private static Object[] parsearCondicion(String texto, List<String> valoresNodo) {
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