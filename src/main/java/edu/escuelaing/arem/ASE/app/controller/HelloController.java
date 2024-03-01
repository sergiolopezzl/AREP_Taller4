package edu.escuelaing.arem.ASE.app.controller;

import java.util.Map;
import edu.escuelaing.arem.ASE.app.Component;
import edu.escuelaing.arem.ASE.app.MovieServer;
import edu.escuelaing.arem.ASE.app.RequestMapping;


/**
 * Clase controladora que maneja las solicitudes relacionadas con el saludo "Hola".
 * Esta clase está marcada como un componente de la aplicación.
 */
@Component
public class HelloController {

    /**
     * Método que responde a las solicitudes GET en la ruta "/hola".
     * Devuelve un saludo personalizado en formato HTML que incluye el nombre proporcionado en los parámetros.
     *
     * @param p Mapa que contiene los parámetros de la solicitud, donde "nombre" es el nombre del usuario.
     * @return Respuesta HTML que incluye un saludo personalizado.
     */
    @RequestMapping("/hola")
    public static String hola(Map<String, String> p) {
        // Establece el tipo de respuesta como HTML
        MovieServer.responseType("text/html");
        // Construye y devuelve el mensaje de saludo personalizado
        return "<h1>Hola " + p.get("nombre") + "</h1>";
    }
}
