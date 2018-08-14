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
        List<String> invokedComponents = new ArrayList<String>();
        StackTraceElement[] fullStackTrace = Thread.currentThread().getStackTrace();
        for(StackTraceElement element : fullStackTrace) {
            String className = element.getClassName();
            Class clasz = null;
            try {
                clasz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            ArchitectureComponent componentAnnotation;// = (ArchitectureComponent)clasz.getAnnotation(ArchitectureComponent.class);
//            logFilePrintStream.println("ANN " + clasz.getSimpleName() + " -> " + componentAnnotation);
            if((componentAnnotation = isAnnotated(clasz)) != null) {
                String componentDescription = className;
                componentDescription += "(" + componentAnnotation.componentType() + ")";
                invokedComponents.add(componentDescription);
            }
        }
        if(invokedComponents.size() > 0) {
            logFilePrintStream.println(ARCHITECTURE_COMPONENT_STACK_START +
                    CollectionSupport.format(invokedComponents, "<") + ARCHITECTURE_COMPONENT_STACK_END);
        }

    }

    private ArchitectureComponent isAnnotated(Class clasz) {
        for(Object classOrSuperClass : ReflectionSupport.getInterfacesAndSuperClassesForClass(clasz)) {
            ArchitectureComponent componentAnnotation = (ArchitectureComponent)((Class)classOrSuperClass).getAnnotation(ArchitectureComponent.class);
            if(componentAnnotation != null) {
                return componentAnnotation;
            }
        }
        return null;
    }

}
