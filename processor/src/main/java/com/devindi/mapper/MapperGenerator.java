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
    private List<MappingInfo> mappings;

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

        for (MappingInfo mapping : mappings) {
            MethodSpec methodSpec = generateMappingMethod(mapping);
            implBuilder.addMethod(methodSpec);
        }

        return implBuilder.build();
    }

    private void collectMappings() {
        for (ExecutableElement method : ElementFilter.methodsIn(element.getEnclosedElements())) {
            try {
                mappings.add(new MappingInfo(method));
            } catch (IllegalArgumentException exc) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, exc.getMessage(), method);
            }
        }
    }

    private MethodSpec generateMappingMethod(MappingInfo mapping) {
        ExecutableElement constructorElement = getConstructorElement(mapping.getTargetType().toString());
        List<? extends VariableElement> constructorParameters = constructorElement.getParameters();

        Map<String, ExecutableElement> argumentGetters = getGetters(mapping.getSourceType().toString());


        if (constructorParameters.size() != argumentGetters.size()) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.MANDATORY_WARNING, "Target constructor have different arguments count", mapping.getMethod());
        }

        MethodSpec.Builder methodBuilder = MethodSpec.overriding(mapping.getMethod());
        StringBuilder statementBuilder = new StringBuilder();
        statementBuilder
                .append("return new ")
                .append(mapping.getTargetType().toString())
                .append("(");

        String separator = "";
        for (VariableElement constructorParameter : constructorParameters) {
            String sourceFieldName = constructorParameter.getSimpleName().toString().toLowerCase();
            com.devindi.mapper.Mapping annotation = mapping.getMethod().getAnnotation(com.devindi.mapper.Mapping.class);
            if (annotation != null && annotation.target().toLowerCase().equals(constructorParameter.getSimpleName().toString().toLowerCase())) {
                sourceFieldName = annotation.source();
            }
            Mappings mappings = mapping.getMethod().getAnnotation(Mappings.class);
            if (mappings != null) {
                for (com.devindi.mapper.Mapping fieldMapping : mappings.value()) {
                    if (fieldMapping != null && fieldMapping.target().toLowerCase().equals(constructorParameter.getSimpleName().toString().toLowerCase())) {
                        sourceFieldName = fieldMapping.source();
                    }
                }
            }
            ExecutableElement getter = argumentGetters.get(sourceFieldName.toLowerCase());
            if (getter == null) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Failed to find getter at source for target field", constructorParameter);
                return null;
            }
            if (!constructorParameter.asType().equals(getter.getReturnType())) {
                //getter and constructor parameter have different types
                MappingInfo depMapping = findMapping(getter.getReturnType(), constructorParameter.asType());
                if (depMapping == null) {
                    // TODO: 06.09.17 generate method
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Getter return type and constructor argument type are different", mapping.getMethod());
                } else {
                    statementBuilder
                            .append(separator)
                            .append('\n')
                            .append("this.")
                            .append(depMapping.getMethod().getSimpleName())
                            .append("(")
                    .append(mapping.getMethod().getParameters().get(0).getSimpleName())
                            .append(".")
                            .append(getter.getSimpleName())
                            .append("()")
                    .append(")");
                    separator = ",";
                }
            } else {
                statementBuilder
                        .append(separator)
                        .append('\n')
                        .append(mapping.getMethod().getParameters().get(0).getSimpleName())
                        .append(".")
                        .append(getter.getSimpleName())
                        .append("()");
                separator = ",";
            }
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

    private MappingInfo findMapping(TypeMirror source, TypeMirror target) {
        for (MappingInfo mapping : mappings) {
            if (mapping.getSourceType().equals(source) && mapping.getTargetType().equals(target)) {
                return mapping;
            }
        }
        return null;
    }

    private MappingInfo generateMapping(TypeMirror source, TypeMirror target) {
        return null;
    }
}
