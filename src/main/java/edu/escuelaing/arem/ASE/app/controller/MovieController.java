package edu.escuelaing.arem.ASE.app.controller;

import edu.escuelaing.arem.ASE.app.*;
import edu.escuelaing.arem.ASE.app.provider.MovieProvider;
import edu.escuelaing.arem.ASE.app.provider.OMDbProvider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Clase controladora que maneja las solicitudes relacionadas con información de películas.
 * Esta clase está marcada como un componente de la aplicación.
 */
@Component
public class MovieController {

    // Mapa que almacena en caché la información de las películas por nombre
    private static final ConcurrentHashMap<String, String> CACHE = new ConcurrentHashMap<>();
    // Proveedor de datos de películas, en este caso, se utiliza OMDbProvider
    private static final MovieProvider movieDataProvider = new OMDbProvider();

    /**
     * Método que responde a las solicitudes GET en la ruta "/movie".
     * Devuelve la información de una película en formato JSON.
     *
     * @param p Mapa que contiene los parámetros de la solicitud, donde "name" es el nombre de la película.
     * @return Información de la película en formato JSON.
     */
    @RequestMapping("/movie")
    public static String movie(Map<String, String> p) {
        // Establece el tipo de respuesta como JSON
        MovieServer.responseType("application/json");
        // Obtiene la información de la película del caché o del proveedor de datos
        return CACHE.computeIfAbsent(p.get("name"), movieDataProvider::fetchMovieData);
    }
}
