package edu.escuelaing.arem.ASE.app.provider;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Proveedor de datos de películas que utiliza la API de OMDb.
 */
public class OMDbProvider implements MovieProvider {

    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String API_KEY = "95abfe39";
    private static final String BASE_URL = "http://www.omdbapi.com/?apikey=" + API_KEY + "&t=";
    private static final Logger LOGGER = Logger.getLogger(OMDbProvider.class.getName());

    /**
     * Obtiene la información de una película utilizando la API de OMDb.
     *
     * @param titleValue Título de la película para buscar su información.
     * @return Información de la película en formato String, o null si ocurre un error.
     */
    @Override
    public String fetchMovieData(String titleValue) {
        String outputLine = null;
        try {
            LOGGER.info("Fetching movie data for: " + titleValue);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + titleValue))
                    .header("User-Agent", USER_AGENT)
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();
            LOGGER.log(Level.INFO, "GET Response Code: {0}", statusCode);

            if (statusCode == 200) {
                String movieInformation = response.body();
                LOGGER.log(Level.INFO, "Response Body: {0}", movieInformation);
                outputLine = movieInformation;
            } else {
                LOGGER.warning("GET request failed");
            }
            LOGGER.info("GET DONE");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error fetching movie data: {0}", e.getMessage());
            Thread.currentThread().interrupt();
        }
        return outputLine;
    }
}
