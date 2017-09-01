package com.devindi.mapper;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;

public class MapperGenerator {

    static MapperGenerator create(TypeElement element, ProcessingEnvironment env) {
        if (element.getKind().equals(ElementKind.INTERFACE)) {
            return new MapperGenerator(element, env);
        } else {
            env.getMessager().printMessage(Diagnostic.Kind.WARNING, "Mapper supports interfaces only", element);
            return null;
        }
    }

    private final TypeElement element;
    private final ProcessingEnvironment processingEnv;
    private List<Mapping> mappings;

    private MapperGenerator(TypeElement mapperElement, ProcessingEnvironment processingEnvironment) {
        this.processingEnv = processingEnvironment;
        this.element = mapperElement;
        mappings = new ArrayList<>();
    }

    public TypeSpec generate() {
        collectMappings();

        TypeSpec.Builder implBuilder = TypeSpec.classBuilder(element.getSimpleName() + "Impl")
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ClassName.get(element));

        for (Mapping mapping : mappings) {
            MethodSpec methodSpec = generateMappingMethod(mapping);
            implBuilder.addMethod(methodSpec);
        }

        return implBuilder.build();
    }

    private void collectMappings() {
        for (ExecutableElement method : ElementFilter.methodsIn(element.getEnclosedElements())) {
            try {
                mappings.add(new Mapping(method));
            } catch (IllegalArgumentException exc) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, exc.getMessage(), method);
            }
        }
    }

    private MethodSpec generateMappingMethod(Mapping mapping) {
        ExecutableElement constructorElement = getConstructorElement(mapping.target.toString());
        List<? extends VariableElement> constructorParameters = constructorElement.getParameters();

        Map<String, ExecutableElement> argumentGetters = getGetters(mapping.source.toString());


        if (constructorParameters.size() != argumentGetters.size()) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.MANDATORY_WARNING, "Target constructor have different arguments count", mapping.method);
        }

        MethodSpec.Builder methodBuilder = MethodSpec.overriding(mapping.method);
        StringBuilder statementBuilder = new StringBuilder();
        statementBuilder
                .append("return new ")
                .append(mapping.target.toString())
                .append("(");

        String separator = "";
        for (VariableElement constructorParameter : constructorParameters) {
            String sourceFieldName;
            com.devindi.mapper.Mapping annotation = mapping.method.getAnnotation(com.devindi.mapper.Mapping.class);
            if (annotation != null && annotation.target().toLowerCase().equals(constructorParameter.getSimpleName().toString().toLowerCase())) {
                sourceFieldName = annotation.source();
            } else {
                sourceFieldName = constructorParameter.getSimpleName().toString().toLowerCase();
            }
            ExecutableElement getter = argumentGetters.get(sourceFieldName.toLowerCase());
            if (getter == null) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Failed to find getter at source for target field", constructorParameter);
                return null;
            }
            if (!constructorParameter.asType().equals(getter.getReturnType())) {
                //getter and constructor parameter have different types
                // TODO: 01.09.17 try to map/convert source field to target field
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Getter return type and constructor argument type are different", mapping.method);
            }

            statementBuilder
                    .append(separator)
                    .append('\n')
                    .append(mapping.method.getParameters().get(0).getSimpleName())
                    .append(".")
                    .append(getter.getSimpleName())
                    .append("()");
            separator = ",";
        }

        statementBuilder
                .append('\n')
                .append(")");
        methodBuilder.addStatement(statementBuilder.toString());



        return methodBuilder.build();
    }

    private ExecutableElement getConstructorElement(String className) {
        TypeElement element = processingEnv.getElementUtils().getTypeElement(className);
        List<? extends Element> enclosedElements = element.getEnclosedElements();
        List<ExecutableElement> constructors = ElementFilter.constructorsIn(enclosedElements);
        return constructors.get(0);
    }

    private Map<String, ExecutableElement> getGetters(String className) {
        Map<String, ExecutableElement> gettersMap = new HashMap<>();
        List<? extends Element> enclosedElements = processingEnv.getElementUtils().getTypeElement(className).getEnclosedElements();
        List<ExecutableElement> methodElements = ElementFilter.methodsIn(enclosedElements);
        for (ExecutableElement element : methodElements) {
            String methodName = element.getSimpleName().toString();
            if (methodName.startsWith("get") && element.getParameters().size() == 0) {
                gettersMap.put(methodName.substring(3).toLowerCase(), element);
            }
        }
        return gettersMap;
    }

    private static class Mapping {
        private final TypeMirror source;
        private final TypeMirror target;
        private final ExecutableElement method;

        public Mapping(ExecutableElement method) {
            this.method = method;
            target = method.getReturnType();
            if (target.getKind().equals(TypeKind.VOID)) {
                throw new IllegalArgumentException("Mapper method should return value. Method will be ignored");
            }
            List<? extends VariableElement> parameters = method.getParameters();
            if (parameters.size() != 1) {
                throw new IllegalArgumentException("Mapper method should have only 1 parameter. Method will be ignored");
            }
            source = parameters.get(0).asType();
        }
    }


}
