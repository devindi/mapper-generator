package com.devindi.mapper;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;

class MethodGenerator {

    private final String defaultParameterName;
    private final String defaultMethodName;
    private final ProcessingEnvironment processingEnv;
    private Map<String, ExecutableElement> sourceGetters;
    private MappingInfo mappingInfo;

    MethodGenerator(ProcessingEnvironment processingEnv, String defaultMethodName, String defaultParameterName) {
        this.defaultParameterName = defaultParameterName;
        this.defaultMethodName = defaultMethodName;
        this.processingEnv = processingEnv;
    }

    MethodSpec generate(MappingInfo info) {
        mappingInfo = info;
        sourceGetters = findGetters(info.getSourceType());
        List<FieldMapping> fieldMappings = createFieldMappings(info.getTargetType());

        MethodSpec.Builder builder = createBuilder(info);
        for (FieldMapping fieldMapping : fieldMappings) {
            CodeBlock fieldInitBlock = generateFieldInitBlock(fieldMapping);
            builder.addCode(fieldInitBlock);
        }

        CodeBlock.Builder targetConstructionBlockBuilder = CodeBlock.builder()
                .add("return new ")
                .add(info.getTargetType().toString())
                .add(" (");

        String delimeter = "";
        for (FieldMapping fieldMapping : fieldMappings) {
            targetConstructionBlockBuilder
                    .add(delimeter)
                    .add(fieldMapping.getSourceField().getSimpleName().toString());
            delimeter = ", ";
        }

        targetConstructionBlockBuilder.add(");");
        builder.addCode(targetConstructionBlockBuilder.build());

        return builder.build();
    }

    private MethodSpec.Builder createBuilder(MappingInfo info) {
        ExecutableElement method = info.getMethod();
        if (method == null) {
            MethodSpec.Builder builder = MethodSpec.methodBuilder(defaultMethodName);
            builder.addModifiers(Modifier.PRIVATE);
            builder.returns(TypeName.get(info.getTargetType()));
            builder.addParameter(
                    ParameterSpec.builder(TypeName.get(info.getSourceType()), defaultParameterName).build());
            return builder;
        }
        return MethodSpec.overriding(method);
    }

    private CodeBlock generateFieldInitBlock(FieldMapping fieldMapping) {
        CodeBlock.Builder builder = CodeBlock.builder();
        if (fieldMapping.getSourceField() == null) {
            builder
                    .add(fieldMapping.getTargetField().asType().toString())
                    .add(" ")
                    .add(fieldMapping.getTargetField().getSimpleName().toString())
                    .add(" = ")
                    .add(findDefaultValueExpression(fieldMapping.getInfo()))
                    .add(";");
            return builder.build();
        } else if (fieldMapping.getSourceField().asType().equals(fieldMapping.getTargetField().asType())) {
            builder
                    .add(fieldMapping.getTargetField().asType().toString())
                    .add(" ")
                    .add(fieldMapping.getTargetField().getSimpleName().toString())
                    .add(" = ")
                    .add(fieldMapping.getSourceField().getSimpleName())
            ;

            return builder.build();
            // TODO: 29.09.17 generate code "Target t = source.getField(); if (t == null) t = default;"
        } else {
            // TODO: 29.09.17 generate code "Target t = map(source.getField(); if (t == null) t = default;"
        }
        return null;
    }

    private String findDefaultValueExpression(Mapping info) {
        if (info == null) {
            return "null";
        }
        return info.defaultValue();
    }



    private ExecutableElement findConstructor(TypeMirror type) {
        TypeElement typeElement = processingEnv.getElementUtils().getTypeElement(type.toString());
        List<? extends Element> enclosedElements = typeElement.getEnclosedElements();
        List<ExecutableElement> constructors = ElementFilter.constructorsIn(enclosedElements);
        return constructors.get(0);
    }

    private Map<String, ExecutableElement> findGetters(TypeMirror type) {
        Map<String, ExecutableElement> gettersMap = new HashMap<>();
        List<? extends Element> enclosedElements = processingEnv.getElementUtils().getTypeElement(type.toString()).getEnclosedElements();
        List<ExecutableElement> methodElements = ElementFilter.methodsIn(enclosedElements);
        for (ExecutableElement element : methodElements) {
            String methodName = element.getSimpleName().toString();
            if (methodName.startsWith("get") && element.getParameters().size() == 0) {
                gettersMap.put(methodName.substring(3).toLowerCase(), element);
            }
        }
        return gettersMap;
    }

    private List<FieldMapping> createFieldMappings(TypeMirror type) {
        List<FieldMapping> result = new ArrayList<>();
        ExecutableElement targetConstructor = findConstructor(type);
        List<? extends VariableElement> targetFields = targetConstructor.getParameters();
        for (VariableElement targetField : targetFields) {
            Mapping fieldAnnotation = findFieldAnnotation(targetField.getSimpleName());
            CharSequence sourceFieldName;
            if (fieldAnnotation != null) {
                sourceFieldName = fieldAnnotation.source();
            } else {
                sourceFieldName = targetField.getSimpleName();
            }
            ExecutableElement sourceField = findSourceFieldGetter(sourceFieldName);
            result.add(new FieldMapping(sourceField, targetField, fieldAnnotation));
        }
        return result;
    }

    private ExecutableElement findSourceFieldGetter(CharSequence fieldName) {
        return sourceGetters.get(fieldName.toString().toLowerCase());
    }

    private Mapping findFieldAnnotation(CharSequence targetName) {
        ExecutableElement method = mappingInfo.getMethod();
        if (method == null) {
            return null;
        }
        Mapping mappingAnnotation = method.getAnnotation(Mapping.class);
        if (mappingAnnotation != null && mappingAnnotation.target().equals(targetName)) {
            return mappingAnnotation;
        }
        Mappings mappingsAnnotation = method.getAnnotation(Mappings.class);
        if (mappingsAnnotation != null && mappingsAnnotation.value().length != 0) {
            for (Mapping mapping : mappingsAnnotation.value()) {
                if (mapping != null && mapping.target().equals(targetName)) {
                    return mapping;
                }
            }
        }
        return null;
    }
}
