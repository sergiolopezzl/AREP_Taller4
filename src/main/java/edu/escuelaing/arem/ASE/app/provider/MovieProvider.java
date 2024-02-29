package edu.escuelaing.arem.ASE.app.provider;

/**
 * Interfaz que define el contrato para proveedores de datos de películas.
 */
public interface MovieProvider {

    /**
     * Método que devuelve la información de una película según el título proporcionado.
     *
     * @param titleValue Título de la película para buscar su información.
     * @return Información de la película en formato String.
     */
    String fetchMovieData(String titleValue);
}
