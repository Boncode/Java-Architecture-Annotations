package nl.ijsberg.analysis.architecture;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by J Meetsma on 28-11-2016.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ArchitectureComponent {

    ComponentType componentType() default ComponentType.UNDEFINED;

    ComponentAttribute[] componentAttributes() default {};

}
