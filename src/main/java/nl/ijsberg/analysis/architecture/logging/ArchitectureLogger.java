package nl.ijsberg.analysis.architecture.logging;

import nl.ijsberg.analysis.architecture.ArchitectureComponent;
import org.ijsberg.iglu.logging.LogEntry;
import org.ijsberg.iglu.logging.Logger;
import org.ijsberg.iglu.logging.module.SimpleFileLogger;
import org.ijsberg.iglu.util.collection.ArraySupport;
import org.ijsberg.iglu.util.collection.CollectionSupport;
import org.ijsberg.iglu.util.collection.StringFormatter;
import org.ijsberg.iglu.util.reflection.ReflectionSupport;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by J Meetsma on 28-11-2016.
 */
public class ArchitectureLogger extends SimpleFileLogger {

    public static final String ARCHITECTURE_COMPONENT_STACK_START = "[ArchitectureComponentStack]";
    public static final String ARCHITECTURE_COMPONENT_STACK_END = "[/ArchitectureComponentStack]";

    public ArchitectureLogger(String fileName) {
        super(fileName);
    }

    public void log(LogEntry entry) {
        List<Class> invokedComponents = new ArrayList<Class>();
        StackTraceElement[] fullStackTrace = Thread.currentThread().getStackTrace();
        for(StackTraceElement element : fullStackTrace) {
            String className = element.getClassName();
            Class clasz = null;
            try {
                clasz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            Annotation componentAnnotation = clasz.getAnnotation(ArchitectureComponent.class);
//            logFilePrintStream.println("ANN " + clasz.getSimpleName() + " -> " + componentAnnotation);
            if(isAnnotated(clasz)) {
                invokedComponents.add(clasz);
            }
        }
        if(invokedComponents.size() > 0) {
            logFilePrintStream.println(ARCHITECTURE_COMPONENT_STACK_START +
                    CollectionSupport.format(invokedComponents, new StringFormatter<Class>() {
                        public String formatString(Class clasz) {
                            return clasz.getName();
                        }
                    }, ",") + ARCHITECTURE_COMPONENT_STACK_END);
        }

    }

    private boolean isAnnotated(Class clasz) {
        for(Class<?> classAndSuperClasses : ReflectionSupport.getInterfacesAndSuperClassesForClass(clasz)) {
            Annotation[] declaredAnnotations = classAndSuperClasses.getDeclaredAnnotations();
            for (Annotation annotation : declaredAnnotations) {
                if (ArchitectureComponent.class.isAssignableFrom(annotation.getClass())) {
                    return true;
                }
            }
        }
        return false;
    }

}
