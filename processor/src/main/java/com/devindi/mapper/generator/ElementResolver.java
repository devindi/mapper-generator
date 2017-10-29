package com.devindi.mapper.generator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;

/**
 * Resolves elements from processing environment
 */
public class ElementResolver {

    private final ProcessingEnvironment processingEnv;

    public ElementResolver(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
    }

    /**
     * Find constructor element from passed type. If there are a few constructors the first
     * one will be returned.
     * @param type type
     * @return executable element
     */
    public ExecutableElement resolveConstructor(TypeMirror type) {
        TypeElement element = processingEnv.getElementUtils().getTypeElement(type.toString());
        List<? extends Element> enclosedElements = element.getEnclosedElements();
        List<ExecutableElement> constructors = ElementFilter.constructorsIn(enclosedElements);
        return constructors.get(0);
    }

    /**
     * Find getters in passed type. Iterates over methods of type and filter by two conditions:
     * 1) method name have 'get' prefix
     * 2) method shouldn't have arguments
     * @param type type
     * @return map, where key is property name and value is executable element, ie method
     * 'String getName()' will produce entry 'name, ExecutableElement@000000'. Map may be empty.
     */
    public Map<String, ExecutableElement> resolveGetters(TypeMirror type) {
        Map<String, ExecutableElement> gettersMap = new HashMap<>();
        List<? extends Element> enclosedElements = processingEnv
                        .getElementUtils()
                        .getTypeElement(type.toString())
                        .getEnclosedElements();
        List<ExecutableElement> methodElements = ElementFilter.methodsIn(enclosedElements);
        for (ExecutableElement element : methodElements) {
            String methodName = element.getSimpleName().toString();
            if (methodName.startsWith("get") && element.getParameters().size() == 0) {
                gettersMap.put(methodName.substring(3).toLowerCase(), element);
            }
        }
        return gettersMap;
    }
}
