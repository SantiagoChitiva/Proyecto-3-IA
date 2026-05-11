import java.util.LinkedHashMap;
import java.util.Map;

/**
 * =====================================================================
 * PERSONA 4
 * =====================================================================
 *
 * Clase principal del proyecto. Integra los tres puntos del taller:
 *
 *   Punto 1 (Personas 1 y 2): Cargar y visualizar la red y sus CPTs.
 *   Punto 2 (Persona 3):      Motor de inferencia por enumeración.
 *   Punto 3 (Persona 4):      Validación con el ejemplo de clase
 *                              y consultas adicionales.
 *
 * -----------------------------------------------------------------------
 * NOTA PARA PERSONA 4:
 *   Tu responsabilidad principal es el PUNTO 3 del taller: diseñar y
 *   ejecutar las consultas de validación, verificar que los resultados
 *   son correctos y presentar evidencia del funcionamiento del motor.
 *
 *   Para hacer esto usas:
 *     - LectorEstructura.leerEstructura()     → Persona 1
 *     - LectorCPT.leerProbabilidades()        → Persona 2
 *     - red.imprimirEstructura()              → Persona 1
 *     - red.imprimirTodasCPT()               → Personas 1 y 2
 *     - new MotorInferencia(red, traza)       → Persona 3
 *     - motor.inferir(consulta, evidencia)    → Persona 3
 *
 * Uso desde consola:
 *   java Main estructura.txt probabilidades.txt
 *   (Si no se pasan argumentos, usa los archivos por defecto.)
 * -----------------------------------------------------------------------
 */
public class Main {

    public static void main(String[] args) {

        // ── Rutas de archivos de entrada ─────────────────────────────────
        String rutaEstructura     = "estructura.txt";
        String rutaProbabilidades = "probabilidades.txt";

        if (args.length >= 2) {
            rutaEstructura     = args[0];
            rutaProbabilidades = args[1];
        } else if (args.length == 1) {
            rutaEstructura = args[0];
        }

        System.out.println("Red bayesiana - Motor de inferencia");


        // ================================================================
        // PUNTO 1 — Carga y visualización de la red
        // (Personas 1 y 2 — ya implementado cuando ellas terminen)
        // ================================================================

        System.out.println("\nCarga de la red bayesiana\n");

        // Paso 1a: Cargar la estructura del grafo (Persona 1)
        System.out.println("Leyendo estructura desde: " + rutaEstructura);
        RedBayesiana red = LectorEstructura.leerEstructura(rutaEstructura);

        if (red == null) {
            System.out.println("No se pudo cargar la red. Verifique el archivo.");
            return;
        }

        // Paso 1b: Mostrar la estructura (Persona 1)
        System.out.println("\nEstructura de la red:");
        red.imprimirEstructura();

        // Paso 1c: Cargar las tablas de probabilidad (Persona 2)
        System.out.println("Leyendo probabilidades desde: " + rutaProbabilidades);
        LectorCPT.leerProbabilidades(rutaProbabilidades, red);

        // Paso 1d: Mostrar todas las CPTs (Personas 1 y 2)
        System.out.println("\nTablas de Probabilidad Condicional:");
        red.imprimirTodasCPT();


        // ================================================================
        // PUNTO 2 — Motor de inferencia con traza
        // (Persona 3 implementa MotorInferencia — Persona 4 lo llama aquí)
        // ================================================================

        System.out.println("\nMotor de inferencia");

        /*
         * TODO (Persona 4) — Demostración del motor con traza activada:
         * -----------------------------------------------------------------------
         * Crear el motor con trazaActiva = true para mostrar el paso a paso.
         * Ejecutar al menos la consulta del ejemplo de clase:
         *
         *   P(Appointment | Train = on time)
         *   Resultado esperado: attend = 0.9000, miss = 0.1000
         *
         * Código de ejemplo:
         *
         *   MotorInferencia motorConTraza = new MotorInferencia(red, true);
         *
         *   Map<String, String> evidencia = new LinkedHashMap<>();
         *   evidencia.put("Train", "on time");
         *   motorConTraza.inferir("Appointment", evidencia);
         *
         * -----------------------------------------------------------------------
         */
        // TODO (Persona 4): descomentar y completar cuando Persona 3 esté lista
        System.out.println("\nDemostración del motor con traza:");
        MotorInferencia motorConTraza = new MotorInferencia(red, true);
        Map<String, String> evidenciaTraza = new LinkedHashMap<>();
        evidenciaTraza.put("Train", "on time");
        motorConTraza.inferir("Appointment", evidenciaTraza);


        // ================================================================
        // PUNTO 3 — Validación: múltiples consultas sobre la misma red
        // (Persona 4 diseña y ejecuta las consultas)
        // ================================================================

        System.out.println("\n\nValidación del motor");

        /*
         * TODO (Persona 4) — Instrucciones:
         * -----------------------------------------------------------------------
         * Crear el motor sin traza (trazaActiva = false) para que la salida
         * sea más limpia y se vean solo los resultados de cada consulta.
         *
         *   MotorInferencia motor = new MotorInferencia(red, false);
         *
         * Luego ejecutar cada consulta con:
         *
         *   Map<String, String> ev = new LinkedHashMap<>();
         *   ev.put("NombreNodo", "valor");
         *   motor.inferir("NodoConsulta", ev);
         *
         * -----------------------------------------------------------------------
         * CONSULTAS OBLIGATORIAS (mínimo estas 7):
         *
         * [1] Consulta del ejemplo de clase (VALIDACIÓN PRINCIPAL):
         *     P(Appointment | Train = on time)
         *     Resultado esperado: attend=0.9000  miss=0.1000
         *     → Verifica que el motor coincide con el ejemplo visto en clase.
         *
         * [2] Sin evidencia (probabilidad marginal):
         *     P(Appointment)   — sin ninguna evidencia
         *     → El motor debe sumar sobre todas las variables ocultas.
         *
         * [3] Evidencia en ancestro:
         *     P(Appointment | Rain = none, Maintenance = yes)
         *     → La evidencia está en nodos padres de Train.
         *
         * [4] Variable intermedia como consulta:
         *     P(Train | Rain = heavy)
         *     → Consultar un nodo del medio de la red.
         *
         * [5] Inferencia hacia atrás ("diagnóstico"):
         *     P(Rain | Appointment = miss)
         *     → Dado el efecto, inferir la causa (dirección contraria al grafo).
         *
         * [6] Verificación de nodo raíz:
         *     P(Rain)  — sin evidencia
         *     Resultado esperado: none=0.700  light=0.200  heavy=0.100
         *     → Debe coincidir exactamente con la CPT de Rain.
         *
         * [7] Evidencia completa en padres directos:
         *     P(Train | Rain = heavy, Maintenance = no)
         *     → Los dos padres de Train tienen valor; no hay variables ocultas en esa parte.
         *
         * -----------------------------------------------------------------------
         * PRESENTACIÓN:
         * Para cada consulta imprimir una línea descriptiva antes de llamar inferir().
         * Ejemplo:
         *   System.out.println("\n─── Consulta 1: P(Appointment | Train=on time)");
         *   System.out.println("    Esperado: attend=0.9000, miss=0.1000");
         *   motor.inferir("Appointment", ev1);
         *
         * -----------------------------------------------------------------------
         */
        // TODO (Persona 4): implementar las consultas de validación aquí
        MotorInferencia motor = new MotorInferencia(red, false);

        // Consulta 1: P(Appointment | Train = on time)
        System.out.println("\n─── Consulta 1: P(Appointment | Train=on time)");
        System.out.println("    Esperado: attend=0.9000, miss=0.1000");
        Map<String, String> ev1 = new LinkedHashMap<>();
        ev1.put("Train", "on time");
        motor.inferir("Appointment", ev1);

        // Consulta 2: P(Appointment) sin evidencia
        System.out.println("\n─── Consulta 2: P(Appointment) sin evidencia");
        motor.inferir("Appointment", new LinkedHashMap<>());

        // Consulta 3: P(Appointment | Rain = none, Maintenance = yes)
        System.out.println("\n─── Consulta 3: P(Appointment | Rain=none, Maintenance=yes)");
        Map<String, String> ev3 = new LinkedHashMap<>();
        ev3.put("Rain", "none");
        ev3.put("Maintenance", "yes");
        motor.inferir("Appointment", ev3);

        // Consulta 4: P(Train | Rain = heavy)
        System.out.println("\n─── Consulta 4: P(Train | Rain=heavy)");
        Map<String, String> ev4 = new LinkedHashMap<>();
        ev4.put("Rain", "heavy");
        motor.inferir("Train", ev4);

        // Consulta 5: P(Rain | Appointment = miss)
        System.out.println("\n─── Consulta 5: P(Rain | Appointment=miss)");
        Map<String, String> ev5 = new LinkedHashMap<>();
        ev5.put("Appointment", "miss");
        motor.inferir("Rain", ev5);

        // Consulta 6: P(Rain) sin evidencia
        System.out.println("\n─── Consulta 6: P(Rain) sin evidencia");
        System.out.println("    Esperado: none=0.7000, light=0.2000, heavy=0.1000");
        motor.inferir("Rain", new LinkedHashMap<>());

        // Consulta 7: P(Train | Rain = heavy, Maintenance = no)
        System.out.println("\n─── Consulta 7: P(Train | Rain=heavy, Maintenance=no)");
        Map<String, String> ev7 = new LinkedHashMap<>();
        ev7.put("Rain", "heavy");
        ev7.put("Maintenance", "no");
        motor.inferir("Train", ev7);


        System.out.println("\nFin del programa");
    }
}