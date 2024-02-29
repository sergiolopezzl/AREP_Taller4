package edu.escuelaing.arem.ASE.app;

import org.reflections.Reflections;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase que implementa un servidor web mínimo para servir páginas web estáticas y procesar solicitudes
 * que involucran componentes marcados con la anotación @Component y métodos marcados con @RequestMapping.
 */
public class MovieServer {

    // Mapa que almacena los componentes marcados con @RequestMapping
    protected static HashMap<String, Method> components = new HashMap<>();
    private static final Logger LOGGER = Logger.getLogger(MovieServer.class.getName());
    // Directorio base para archivos estáticos
    private static String directory = "target/classes/public";
    // Tipo de contenido predeterminado
    private static String contentType = "text/html";

    // Constructor privado para evitar instanciación
    private MovieServer() {
    }

    /**
     * Método principal que inicia el servidor.
     *
     * @param args Argumentos de línea de comandos (no se utilizan).
     * @throws URISyntaxException       Excepción de URI inválida.
     * @throws InvocationTargetException Excepción de invocación del método.
     * @throws IllegalAccessException    Excepción de acceso ilegal.
     */
    public static void main(String[] args) throws URISyntaxException, InvocationTargetException, IllegalAccessException {
        // Utilidad de reflexión para escanear clases y métodos anotados
        Reflections reflections = new Reflections("edu.escuelaing.arem.ASE.app");
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Component.class);
        // Escanea las clases para encontrar métodos anotados
        for (Class<?> c : classes) {
            for (Method method : c.getDeclaredMethods()) {
                if (method.isAnnotationPresent(RequestMapping.class)) {
                    components.put(method.getAnnotation(RequestMapping.class).value(), method);
                }
            }
        }
        // Inicia el servidor en el puerto 35000
        try (ServerSocket serverSocket = new ServerSocket(35000)) {
            while (true) {
                LOGGER.info("Listo para recibir ...");
                handleClientRequest(serverSocket.accept());
            }
        } catch (IOException e) {
            LOGGER.info("Could not listen on port: 35000.");
            System.exit(1);
        }
    }

    /**
     * Maneja la solicitud del cliente.
     *
     * @param clientSocket Socket del cliente.
     * @throws URISyntaxException       Excepción de URI inválida.
     * @throws InvocationTargetException Excepción de invocación del método.
     * @throws IllegalAccessException    Excepción de acceso ilegal.
     */
    public static void handleClientRequest(Socket clientSocket) throws URISyntaxException, InvocationTargetException, IllegalAccessException {
        try (OutputStream outputStream = clientSocket.getOutputStream();
             PrintWriter out = new PrintWriter(outputStream, true);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            String requestLine = in.readLine();
            LOGGER.log(Level.INFO, "Received:{0}", requestLine);
            if (requestLine != null) {
                // Analiza la línea de solicitud para obtener la URI y los parámetros
                URI fileUrl = new URI(requestLine.split(" ")[1]);
                String params = fileUrl.getRawQuery();
                String path = fileUrl.getPath();
                LOGGER.log(Level.INFO, "Path: {0}", path);
                String outputLine;
                // Procesa la solicitud dependiendo del tipo de ruta
                if (path.startsWith("/action")) {
                    String webUri = path.replace("/action", "");
                    if (components.containsKey(webUri)) {
                        Map<String, String> parameters = parseParams(params);
                        if (components.get(webUri).getParameterCount() == 0) {
                            outputLine = (String) components.get(webUri).invoke(null);
                        } else outputLine = (String) components.get(webUri).invoke(null, parameters);
                        out.println(httpHeader(contentType).append(outputLine));
                    }
                } else if (path.contains(".")) {
                    String contentType = contentType(path);
                    if (contentType.contains("image")) outputStream.write(httpClientImage(path));
                    else out.println(httpClientFiles(path));
                } else out.println(httpClientError());
            }
            clientSocket.close();
        } catch (IOException e) {
            LOGGER.info("Accept failed.");
            System.exit(1);
        }
    }

    /**
     * Devuelve una respuesta de error HTTP.
     *
     * @return Respuesta de error HTTP.
     */
    public static String httpClientError() {
        StringBuilder outputLine = new StringBuilder();
        outputLine.append("HTTP/1.1 404 Not Found\r\n");
        outputLine.append("Content-Type:text/html\r\n\r\n");
        Path file = Paths.get(directory + "/error.html");
        Charset charset = StandardCharsets.UTF_8;
        try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
            String line;
            while ((line = reader.readLine()) != null) outputLine.append(line);
        } catch (IOException e) {
            LOGGER.warning(e.getMessage());
        }
        return outputLine.toString();
    }

    /**
     * Analiza los parámetros de la solicitud HTTP.
     *
     * @param queryString Cadena de consulta de la URL.
     * @return Mapa de parámetros clave-valor.
     */
    public static Map<String, String> parseParams(String queryString) {
        if (queryString != null) {
            Map<String, String> params = new HashMap<>();
            for (String param : queryString.split("&")) {
                String[] nameValue = param.split("=");
                if (nameValue.length == 2) {
                    params.put(nameValue[0], nameValue[1]);
                } else {
                    params.put(nameValue[0], "");
                }
            }
            return params;
        } else {
            return Collections.emptyMap();
        }
    }

    /**
     * Lee y devuelve el contenido de un archivo de texto.
     *
     * @param path Ruta del archivo.
     * @return Contenido del archivo como una cadena.
     */
    public static String httpClientFiles(String path) {
        StringBuilder outputLine = httpHeader(contentType(path));
        Path file = Paths.get(directory + path);
        Charset charset = StandardCharsets.UTF_8;
        try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
            String line;
            while ((line = reader.readLine()) != null) outputLine.append(line);
        } catch (IOException e) {
            LOGGER.warning(e.getMessage());
        }
        return outputLine.toString();
    }

    /**
     * Lee y devuelve el contenido de una imagen.
     *
     * @param path Ruta de la imagen.
     * @return Contenido de la imagen como un array de bytes.
     */
    public static byte[] httpClientImage(String path) {
        Path file = Paths.get(directory + path);
        byte[] imageData = null;
        try {
            imageData = Files.readAllBytes(file);
        } catch (IOException e) {
            LOGGER.warning(e.getMessage());
        }
        byte[] headerBytes = httpHeader(contentType(path)).toString().getBytes();
        assert imageData != null;
        int totalLength = headerBytes.length + imageData.length;
        byte[] combinedBytes = new byte[totalLength];
        System.arraycopy(headerBytes, 0, combinedBytes, 0, headerBytes.length);
        System.arraycopy(imageData, 0, combinedBytes, headerBytes.length, imageData.length);
        return combinedBytes;
    }

    /**
     * Construye el encabezado HTTP para la respuesta.
     *
     * @param contentType Tipo de contenido.
     * @return Encabezado HTTP.
     */
    public static StringBuilder httpHeader(String contentType) {
        StringBuilder header = new StringBuilder();
        header.append("HTTP/1.1 200 OK\r\n");
        header.append("Content-Type:");
        header.append(contentType);
        header.append("\r\n");
        header.append("\r\n");
        return header;
    }

    /**
     * Determina el tipo de contenido basado en la extensión del archivo.
     *
     * @param path Ruta del archivo.
     * @return Tipo de contenido.
     */
    public static String contentType(String path) {
        File file = new File(path);
        String contentType = "";
        try {
            contentType = Files.probeContentType(file.toPath());
        } catch (IOException e) {
            LOGGER.warning(e.getMessage());
        }
        return contentType;
    }

    /**
     * Establece el tipo de respuesta HTTP.
     *
     * @param responseType Tipo de respuesta HTTP.
     */
    public static void responseType(String responseType) {
        contentType = responseType;
    }
}
