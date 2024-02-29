package edu.escuelaing.arem.ASE.app;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation utilizada para marcar una clase como componente de la aplicación.
 * Esto permite que el framework de la aplicación identifique automáticamente los componentes y los administre.
 */
@Target(ElementType.TYPE) // La anotación se puede aplicar a tipos (clases, interfaces, enums).
@Retention(RetentionPolicy.RUNTIME) // La anotación estará disponible en tiempo de ejecución.
public @interface Component {

}
