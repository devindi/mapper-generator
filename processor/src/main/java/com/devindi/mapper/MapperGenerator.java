package com.devindi.mapper;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private Set<MappingInfo> mappings;

    private MapperGenerator(TypeElement mapperElement, ProcessingEnvironment processingEnvironment) {
        this.processingEnv = processingEnvironment;
        this.element = mapperElement;
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
        mappings = new HashSet<>();
        for (ExecutableElement method : ElementFilter.methodsIn(element.getEnclosedElements())) {
            try {
                mappings.add(new MappingInfo(method));
            } catch (IllegalArgumentException exc) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, exc.getMessage(), method);
            }
        }

        Set<MappingInfo> lastRow = mappings;
        Set<MappingInfo> nextRow = new HashSet<>();
        do {
            for (MappingInfo mappingInfo : lastRow) {
                nextRow.addAll(createDependencies(mappingInfo));
            }
            mappings.addAll(nextRow);
            lastRow = nextRow;
            nextRow = new HashSet<>();
        } while (!lastRow.isEmpty());

        System.out.println("mappings = " + mappings);
    }

    private List<MappingInfo> createDependencies(MappingInfo info) {
        List<MappingInfo> result = new ArrayList<>();
        ExecutableElement constructorElement = getConstructorElement(info.getTargetType().toString());
        List<? extends VariableElement> constructorParameters = constructorElement.getParameters();

        Map<String, ExecutableElement> argumentGetters = getGetters(info.getSourceType().toString());

        if (constructorParameters.size() != argumentGetters.size()) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.MANDATORY_WARNING, "Target constructor have different arguments count", info.getMethod());
        }

        for (VariableElement parameter : constructorParameters) {
            String sourceFieldName = info.getSourceFieldName(parameter.getSimpleName().toString().toLowerCase());
            ExecutableElement getter = argumentGetters.get(sourceFieldName.toLowerCase());
            if (getter == null) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Failed to find getter at source for target field", parameter);
                return Collections.emptyList();
            }
            if (!parameter.asType().equals(getter.getReturnType())) {
                MappingInfo fieldMapping = findMapping(getter.getReturnType(), parameter.asType());
                if (fieldMapping == null) {
                    result.add(new MappingInfo(getter.getReturnType(), parameter.asType()));
                }
            }
        }
        return result;
    }

    private MethodSpec generateMappingMethod(MappingInfo mapping) {
        ExecutableElement constructorElement = getConstructorElement(mapping.getTargetType().toString());
        List<? extends VariableElement> constructorParameters = constructorElement.getParameters();

        Map<String, ExecutableElement> argumentGetters = getGetters(mapping.getSourceType().toString());


        if (constructorParameters.size() != argumentGetters.size()) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.MANDATORY_WARNING, "Target constructor have different arguments count", mapping.getMethod());
        }


        MethodSpec.Builder methodBuilder = createMethodBuilder(mapping);
        StringBuilder statementBuilder = new StringBuilder();
        statementBuilder
                .append("return new ")
                .append(mapping.getTargetType().toString())
                .append("(");

        String separator = "";
        for (VariableElement constructorParameter : constructorParameters) {
            String sourceFieldName = mapping.getSourceFieldName(constructorParameter.getSimpleName().toString().toLowerCase());
            ExecutableElement getter = argumentGetters.get(sourceFieldName.toLowerCase());
            System.out.println("constructorParameter = " + constructorParameter);
            System.out.println("getter = " + getter);
            if (!constructorParameter.asType().equals(getter.getReturnType())) {
                //getter and constructor parameter have different types
                MappingInfo depMapping = findMapping(getter.getReturnType(), constructorParameter.asType());
                if (depMapping == null) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "ERROR 1. Failed to find generated mapping info", constructorParameter);
                    return null;
                }
                statementBuilder
                        .append(separator)
                        .append('\n')
                        .append("this.")
                        .append(depMapping.getMethodName())
                        .append("(")
                        .append(mapping.getSourceName())
                        .append(".")
                        .append(getter.getSimpleName())
                        .append("()")
                        .append(")");
                separator = ",";
            } else {
                statementBuilder
                        .append(separator)
                        .append('\n')
                        .append(mapping.getSourceName())
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
        System.out.println("finding constructor for = " + className);
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

    private MethodSpec.Builder createMethodBuilder(MappingInfo info) {
        ExecutableElement method = info.getMethod();
        if (method == null) {
            MethodSpec.Builder builder = MethodSpec.methodBuilder(info.getMethodName().toString());
            builder.addModifiers(Modifier.PRIVATE);
            builder.returns(TypeName.get(info.getTargetType()));
            builder.addParameter(ParameterSpec.builder(TypeName.get(info.getSourceType()), "source").build());
            return builder;
        }
        return MethodSpec.overriding(method);
    }
}
