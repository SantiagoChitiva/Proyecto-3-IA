import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/*Lee el archivo de estructura de la red bayesiana y construye los objetos Nodo y Arco que conforman el grafo
 Formato de estructura.txt:
 - Cada línea define un arco:
 NombrePadre[val1,val2,...] -> NombreHijo[val1,val2,...]
- Líneas con '#' son comentarios y se ignoran.
- Líneas vacías se ignoran.
- Un nodo puede aparecer en varias líneas; solo se crea la primera vez.
Ejemplo (red del ejemplo de clase):
 # Red Bayesiana - Ejemplo de clase
Rain[none,light,heavy]   -> Maintenance[yes,no]
Rain[none,light,heavy]   -> Train[on time,delayed]
Maintenance[yes,no]      -> Train[on time,delayed]
Train[on time,delayed]   -> Appointment[attend,miss]
 */
public class LectorEstructura {
    //Lee el archivo y construye la red bayesiana completa.
    public static RedBayesiana leerEstructura(String rutaArchivo) {
        RedBayesiana red = new RedBayesiana();

        try (BufferedReader lector = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            int numeroLinea = 0;

            while ((linea = lector.readLine()) != null) {
                numeroLinea++;
                linea = linea.trim();

                // Ignorar comentarios y líneas vacías
                if (linea.isEmpty() || linea.startsWith("#")) continue;

                // Validar que exista el separador "->"
                if (!linea.contains("->")) {
                    System.out.println("Línea " + numeroLinea
                            + " ignorada (sin '->'): " + linea);
                    continue;
                }

                // Separar en parte padre y parte hijo
                String[] partes = linea.split("->");
                if (partes.length != 2) {
                    System.out.println("Línea " + numeroLinea
                            + " ignorada (formato incorrecto): " + linea);
                    continue;
                }

                // Parsear ambos nodos
                Nodo nodoPadre = parsearNodo(partes[0].trim());
                Nodo nodoHijo  = parsearNodo(partes[1].trim());

                if (nodoPadre == null || nodoHijo == null) {
                    System.out.println("Línea " + numeroLinea
                            + " ignorada (no se pudo parsear).");
                    continue;
                }

                // Agregar nodos (si ya existen en la red, agregarNodo los ignora)
                red.agregarNodo(nodoPadre);
                red.agregarNodo(nodoHijo);

                // Crear el arco padre → hijo
                red.agregarArco(nodoPadre.getNombre(), nodoHijo.getNombre());
            }

            System.out.println("  Estructura cargada: " + red.getNodos().size() + " nodos, " + red.getArcos().size() + " arcos.");

        } catch (IOException e) {
            System.out.println("Error: No se pudo leer: " + rutaArchivo);
            System.out.println("Causa: " + e.getMessage());
            return null;
        }

        return red;
    }

    /*Parsea un texto con formato "NombreNodo[val1,val2,...]" y crea un Nodo
     Ejemplo: "Rain[none,light,heavy]" -> Nodo con nombre="Rain", valores=["none","light","heavy"]
     */
    private static Nodo parsearNodo(String texto) {
        int abre  = texto.indexOf('[');
        int cierra = texto.indexOf(']');

        if (abre == -1 || cierra == -1 || cierra < abre) {
            System.out.println("Error: Formato de nodo inválido: " + texto);
            return null;
        }

        String nombre  = texto.substring(0, abre).trim();
        String valores = texto.substring(abre + 1, cierra).trim();

        if (nombre.isEmpty() || valores.isEmpty()) {
            System.out.println("Error: Nombre o valores vacíos en: " + texto);
            return null;
        }

        return new Nodo(nombre, valores);
    }
}