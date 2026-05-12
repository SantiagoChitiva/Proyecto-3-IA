import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Motor de inferencia por enumeración para una red bayesiana.
 *
 * Decisiones:
 * - Se procesa la red en orden topológico para respetar dependencias.
 * - Se calcula una distribución no normalizada y se normaliza al final.
 * - La traza es opcional y no altera el cálculo, solo imprime el flujo.
 */
public class MotorInferencia {

    // Red bayesiana sobre la cual se van a realizar las consultas
    private RedBayesiana red;

    // Permite activar o desactivar la impresión paso a paso del algoritmo
    private boolean trazaActiva;

    /**
     * Constructor del motor de inferencia.
     *
     * @param red Red bayesiana ya construida con nodos, arcos y CPTs.
     * @param trazaActiva Indica si se desea mostrar la traza del cálculo.
     */
    public MotorInferencia(RedBayesiana red, boolean trazaActiva) {
        this.red = red;
        this.trazaActiva = trazaActiva;
    }

    /**
     * Método principal del motor.
     *
     * Calcula la distribución de probabilidad de una variable de consulta
     * dada una evidencia.
     *
     * Ejemplo:
     *
     *      inferir("Appointment", evidencia)
     *
     * Si evidencia contiene:
     *
     *      Train = on time
     *
     * entonces se está calculando:
     *
     *      P(Appointment | Train = on time)
     *
     * @param nombreConsulta Nombre de la variable que se quiere consultar.
     * @param evidencia Variables conocidas con sus respectivos valores.
     * @return Distribución de probabilidad normalizada.
     */
    public Map<String, Double> inferir(String nombreConsulta,
                                       Map<String, String> evidencia) {

        // Se busca el nodo de consulta dentro de la red
        Nodo nodoConsulta = red.obtenerNodo(nombreConsulta);

        if (nodoConsulta == null) {
            System.out.println("Error: la variable de consulta no existe en la red: "
                    + nombreConsulta);
            return null;
        }

        // Se verifica que cada variable de evidencia exista en la red
        for (String nombreEvidencia : evidencia.keySet()) {
            if (red.obtenerNodo(nombreEvidencia) == null) {
                System.out.println("Error: la variable de evidencia no existe en la red: "
                        + nombreEvidencia);
                return null;
            }
        }

        // El algoritmo necesita recorrer los nodos en orden topológico.
        // Esto significa que los padres se procesan antes que los hijos.
        List<Nodo> orden = obtenerOrdenTopologico();

        if (orden.isEmpty()) {
            System.out.println("Error: no fue posible obtener el orden topológico de la red.");
            return null;
        }

        if (trazaActiva) {
            imprimirEncabezadoInferencia(nombreConsulta, evidencia, orden);
        }

        // Aquí se guardan las probabilidades sin normalizar.
        // Ejemplo:
        // attend -> 0.72
        // miss   -> 0.08
        Map<String, Double> distribucion = new LinkedHashMap<>();

        // Para cada posible valor de la variable de consulta,
        // se calcula P(valorConsulta, evidencia).
        for (String valorConsulta : nodoConsulta.getValores()) {

            // Se copia la evidencia original para no modificarla directamente
            Map<String, String> evidenciaExtendida = new LinkedHashMap<>(evidencia);

            // Se agrega temporalmente la hipótesis que se está evaluando.
            // Ejemplo: Appointment = attend
            evidenciaExtendida.put(nombreConsulta, valorConsulta);

            if (trazaActiva) {
                System.out.println("\nCalculando caso: "
                        + nombreConsulta + " = " + valorConsulta);
            }

            // Se llama al algoritmo recursivo que enumera todas las variables
            double probabilidad = enumerarTodo(orden, 0, evidenciaExtendida, "│  ");

            // Se guarda la probabilidad sin normalizar
            distribucion.put(valorConsulta, probabilidad);

            if (trazaActiva) {
                System.out.printf("Resultado parcial para %s = %s: %.6f%n",
                        nombreConsulta, valorConsulta, probabilidad);
            }
        }

        // Se normaliza para que la suma final sea 1
        Map<String, Double> distribucionNormalizada = normalizar(distribucion);

        // Se imprime el resultado final de la consulta
        imprimirResultado(nombreConsulta, evidencia, distribucionNormalizada);

        return distribucionNormalizada;
    }

    /**
     * Implementa el algoritmo recursivo ENUMERATE-ALL.
     *
     * Este método recorre todas las variables de la red.
     *
     * Hay dos casos:
     *
     * 1. Si la variable actual ya tiene un valor en la evidencia,
     *    se usa directamente ese valor.
     *
     * 2. Si la variable actual no está en la evidencia,
     *    se considera variable oculta y se suman todos sus posibles valores.
     *
     * @param orden Lista de nodos en orden topológico.
     * @param indice Posición actual dentro de la lista.
     * @param evidencia Valores conocidos hasta el momento.
     * @param sangria Texto usado solo para imprimir la traza ordenada.
     * @return Probabilidad calculada por enumeración.
     */
    private double enumerarTodo(List<Nodo> orden, int indice,
                                Map<String, String> evidencia, String sangria) {

        // Caso base:
        // Si ya se recorrieron todos los nodos, se retorna 1.
        if (indice == orden.size()) {
            if (trazaActiva) {
                System.out.println(sangria + "Hoja alcanzada -> 1.0");
            }
            return 1.0;
        }

        Nodo nodoActual = orden.get(indice);
        String nombreNodo = nodoActual.getNombre();

        // Para consultar la CPT se necesita construir la condición
        // a partir de los valores de los padres.
        String claveCondicion = construirClaveCondicion(nodoActual, evidencia);

        /*
         * CASO 1:
         * El nodo actual ya tiene un valor conocido.
         *
         * Ejemplo:
         * Si evidencia contiene Rain = heavy, entonces no se suman
         * todos los valores de Rain, sino que se usa directamente
         * P(Rain = heavy).
         */
        if (evidencia.containsKey(nombreNodo)) {

            String valorConocido = evidencia.get(nombreNodo);

            double p = obtenerProbabilidadCPT(nodoActual, valorConocido, claveCondicion);

            if (trazaActiva) {
                System.out.printf("%sP(%s = %s | %s) = %.6f%n",
                        sangria,
                        nombreNodo,
                        valorConocido,
                        claveCondicion.equals("") ? "raiz" : claveCondicion,
                        p);
            }

            // Se multiplica la probabilidad actual por el resultado
            // de seguir enumerando el resto de variables.
            return p * enumerarTodo(orden, indice + 1, evidencia, sangria + "  ");
        }

        /*
         * CASO 2:
         * El nodo actual no tiene valor conocido.
         *
         * Por tanto, es una variable oculta.
         * Se deben probar todos sus valores posibles y sumar los resultados.
         */
        if (trazaActiva) {
            System.out.println(sangria + "Sumando sobre variable oculta: " + nombreNodo);
        }

        double suma = 0.0;

        // Se recorren todos los valores posibles del nodo oculto.
        for (String valor : nodoActual.getValores()) {

            // Se crea una nueva evidencia extendida con el valor actual
            Map<String, String> evidenciaExtendida = new LinkedHashMap<>(evidencia);
            evidenciaExtendida.put(nombreNodo, valor);

            // Se obtiene la probabilidad del valor actual usando la CPT
            double p = obtenerProbabilidadCPT(nodoActual, valor, claveCondicion);

            if (trazaActiva) {
                System.out.printf("%s  P(%s = %s | %s) = %.6f%n",
                        sangria,
                        nombreNodo,
                        valor,
                        claveCondicion.equals("") ? "raiz" : claveCondicion,
                        p);
            }

            // Se calcula el aporte de esta rama de la enumeración
            double resultadoRama = p * enumerarTodo(
                    orden,
                    indice + 1,
                    evidenciaExtendida,
                    sangria + "    "
            );

            // Se acumula el resultado
            suma += resultadoRama;

            if (trazaActiva) {
                System.out.printf("%s  Rama %s = %s aporta: %.6f%n",
                        sangria, nombreNodo, valor, resultadoRama);
            }
        }

        if (trazaActiva) {
            System.out.printf("%sSuma total para %s = %.6f%n", sangria, nombreNodo, suma);
        }

        return suma;
    }

    /**
     * Construye la clave para buscar una probabilidad en la CPT.
     *
     * La clave se forma con los valores de los padres del nodo.
     *
     * Ejemplo:
     *
     * Nodo:
     *      Train
     *
     * Padres:
     *      Rain, Maintenance
     *
     * Evidencia:
     *      Rain = none
     *      Maintenance = yes
     *
     * Clave generada:
     *      "none,yes"
     *
     * Para nodos raíz, la clave es una cadena vacía "".
     *
     * @param nodo Nodo cuya condición se quiere construir.
     * @param evidencia Valores conocidos hasta el momento.
     * @return Clave de condición para consultar la CPT.
     */
    private String construirClaveCondicion(Nodo nodo, Map<String, String> evidencia) {

        // Si el nodo no tiene padres, es raíz
        if (nodo.getPadres().isEmpty()) {
            return "";
        }

        StringBuilder clave = new StringBuilder();

        // Se agregan los valores de los padres en el mismo orden
        // en que aparecen en la red.
        for (int i = 0; i < nodo.getPadres().size(); i++) {

            Nodo padre = nodo.getPadres().get(i);
            String valorPadre = evidencia.get(padre.getNombre());

            if (valorPadre == null) {
                System.out.println("Error: falta el valor del padre "
                        + padre.getNombre()
                        + " para calcular la CPT de "
                        + nodo.getNombre());

                valorPadre = "?";
            }

            clave.append(valorPadre);

            if (i < nodo.getPadres().size() - 1) {
                clave.append(",");
            }
        }

        return clave.toString();
    }

    /**
     * Obtiene una probabilidad específica desde la tabla de probabilidad
     * condicional de un nodo.
     *
     * Ejemplo:
     *
     *      P(Train = on time | Rain = none, Maintenance = yes)
     *
     * @param nodo Nodo al que pertenece la CPT.
     * @param valor Valor del nodo que se está evaluando.
     * @param claveCondicion Condición formada por los valores de los padres.
     * @return Probabilidad encontrada en la CPT.
     */
    private double obtenerProbabilidadCPT(Nodo nodo, String valor, String claveCondicion) {

        CPT cpt = nodo.getTableProbabilidad();

        if (cpt == null) {
            System.out.println("Error: el nodo " + nodo.getNombre()
                    + " no tiene CPT asignada.");
            return 0.0;
        }

        double p = cpt.obtenerProbabilidad(valor, claveCondicion);

        if (p < 0) {
            System.out.println("Error: no se encontró la probabilidad para "
                    + nodo.getNombre()
                    + " = "
                    + valor
                    + " con condición ["
                    + claveCondicion
                    + "]");
            return 0.0;
        }

        return p;
    }

    /**
     * Obtiene el orden topológico de la red bayesiana.
     *
     * El orden topológico es necesario porque cada nodo depende de sus padres.
     * Por eso, los padres deben aparecer antes que sus hijos.
     *
     * Ejemplo de orden válido:
     *
     *      Rain -> Maintenance -> Train -> Appointment
     *
     * @return Lista de nodos en orden topológico.
     */
    private List<Nodo> obtenerOrdenTopologico() {

        List<Nodo> resultado = new ArrayList<>();

        // Se empieza desde los nodos raíz, es decir,
        // los nodos que no tienen padres.
        List<Nodo> cola = new ArrayList<>(red.obtenerRaices());

        List<String> visitados = new ArrayList<>();

        int intentosSinAvance = 0;

        while (!cola.isEmpty()) {

            Nodo actual = cola.remove(0);

            if (visitados.contains(actual.getNombre())) {
                continue;
            }

            // Se verifica si todos los padres del nodo actual
            // ya fueron visitados.
            boolean padresListos = true;

            for (Nodo padre : actual.getPadres()) {
                if (!visitados.contains(padre.getNombre())) {
                    padresListos = false;
                    break;
                }
            }

            if (padresListos) {

                resultado.add(actual);
                visitados.add(actual.getNombre());

                // Después de visitar un nodo, se agregan sus hijos
                // como posibles próximos nodos.
                for (Nodo hijo : actual.getHijos()) {
                    if (!visitados.contains(hijo.getNombre())) {
                        cola.add(hijo);
                    }
                }

                intentosSinAvance = 0;

            } else {

                // Si todavía no están listos sus padres, se vuelve a poner
                // el nodo al final de la cola.
                cola.add(actual);
                intentosSinAvance++;

                // Esta validación evita un ciclo infinito si la red
                // tuviera algún ciclo o error en la estructura.
                if (intentosSinAvance > red.getNodos().size() * red.getNodos().size()) {
                    System.out.println("Error: posible ciclo en la red bayesiana.");
                    return new ArrayList<>();
                }
            }
        }

        return resultado;
    }

    /**
     * Normaliza una distribución de probabilidad.
     *
     * La inferencia inicialmente obtiene valores sin normalizar.
     * Para convertirlos en una distribución válida, se divide cada valor
     * entre la suma total.
     *
     * Ejemplo:
     *
     *      attend -> 0.72
     *      miss   -> 0.08
     *
     * Suma:
     *      0.80
     *
     * Normalizado:
     *      attend -> 0.72 / 0.80 = 0.90
     *      miss   -> 0.08 / 0.80 = 0.10
     *
     * @param distribucion Distribución sin normalizar.
     * @return Distribución normalizada.
     */
    private Map<String, Double> normalizar(Map<String, Double> distribucion) {

        Map<String, Double> normalizada = new LinkedHashMap<>();

        double suma = 0.0;

        for (double valor : distribucion.values()) {
            suma += valor;
        }

        if (suma == 0.0) {
            System.out.println("Error: no se puede normalizar porque la suma es 0.");
            return distribucion;
        }

        for (Map.Entry<String, Double> entrada : distribucion.entrySet()) {
            normalizada.put(entrada.getKey(), entrada.getValue() / suma);
        }

        return normalizada;
    }

    /**
     * Imprime información inicial de la inferencia cuando la traza está activa.
     */
    private void imprimirEncabezadoInferencia(String nombreConsulta,
                                              Map<String, String> evidencia,
                                              List<Nodo> orden) {

        System.out.println("\n========================================");
        System.out.println("     INFERENCIA POR ENUMERACION");
        System.out.println("========================================");

        System.out.println("Consulta : P(" + nombreConsulta + " | evidencia)");
        System.out.println("Evidencia: " + formatearEvidencia(evidencia));

        System.out.print("Orden    : ");

        for (int i = 0; i < orden.size(); i++) {
            System.out.print(orden.get(i).getNombre());

            if (i < orden.size() - 1) {
                System.out.print(" -> ");
            }
        }

        System.out.println("\n----------------------------------------");
    }

    /**
     * Imprime el resultado final de la consulta realizada.
     */
    private void imprimirResultado(String nombreConsulta,
                                   Map<String, String> evidencia,
                                   Map<String, Double> distribucion) {

        System.out.println("\n===================================================");
        System.out.println("RESULTADO: P(" + nombreConsulta + " | "
                + formatearEvidencia(evidencia) + ")");
        System.out.println("===================================================");

        for (Map.Entry<String, Double> entrada : distribucion.entrySet()) {

            double prob = entrada.getValue();

            System.out.printf("%-15s | %.4f  (%.2f%%)%n",
                    entrada.getKey(),
                    prob,
                    prob * 100);
        }

        System.out.println("---------------------------------------------------");
    }

    /**
     * Convierte el mapa de evidencia a texto para mostrarlo en pantalla.
     *
     * Ejemplo:
     *
     *      Rain=heavy, Maintenance=no
     *
     * Si no hay evidencia, retorna:
     *
     *      sin evidencia
     */
    private String formatearEvidencia(Map<String, String> evidencia) {

        if (evidencia == null || evidencia.isEmpty()) {
            return "sin evidencia";
        }

        StringBuilder sb = new StringBuilder();

        int contador = 0;

        for (Map.Entry<String, String> entrada : evidencia.entrySet()) {

            sb.append(entrada.getKey())
              .append("=")
              .append(entrada.getValue());

            if (contador < evidencia.size() - 1) {
                sb.append(", ");
            }

            contador++;
        }

        return sb.toString();
    }
}