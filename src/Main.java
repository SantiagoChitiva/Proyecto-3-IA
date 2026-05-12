import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Punto de entrada del programa.
 *
 * Decisiones:
 * - Usa archivos por defecto y permite sobreescribirlos por argumentos.
 * - Ejecuta una demostración con traza y una batería fija de consultas
 *   para validar resultados de inferencia.
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


        System.out.println("\nCarga de la red bayesiana\n");

        System.out.println("Leyendo estructura desde: " + rutaEstructura);
        RedBayesiana red = LectorEstructura.leerEstructura(rutaEstructura);

        if (red == null) {
            System.out.println("No se pudo cargar la red. Verifique el archivo.");
            return;
        }

        System.out.println("\nEstructura de la red:");
        red.imprimirEstructura();

        System.out.println("Leyendo probabilidades desde: " + rutaProbabilidades);
        LectorCPT.leerProbabilidades(rutaProbabilidades, red);

        System.out.println("\nTablas de Probabilidad Condicional:");
        red.imprimirTodasCPT();


        System.out.println("\nMotor de inferencia");

        // Demostración con traza activada
        System.out.println("\nDemostración del motor con traza:");
        MotorInferencia motorConTraza = new MotorInferencia(red, true);
        Map<String, String> evidenciaTraza = new LinkedHashMap<>();
        evidenciaTraza.put("Train", "on time");
        motorConTraza.inferir("Appointment", evidenciaTraza);


        System.out.println("\n\nValidación del motor");

        // Consultas de validación sin traza
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