package edu.escuelaing.arem.ASE.app;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotación utilizada para mapear métodos a rutas específicas dentro del servidor.
 * Esta anotación se aplica a los métodos de los controladores para definir cómo se manejan las solicitudes HTTP.
 * @
 */
@Target(ElementType.METHOD) // La anotación se puede aplicar solo a métodos.
@Retention(RetentionPolicy.RUNTIME) // La anotación estará disponible en tiempo de ejecución.
public @interface RequestMapping {

    /**
     * Define la ruta asociada al método.
     *
     * @return La ruta asociada al método.
     */
    String value();
}
