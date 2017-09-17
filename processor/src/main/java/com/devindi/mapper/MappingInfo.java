package com.devindi.mapper;

import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

public class MappingInfo {

    private final TypeMirror sourceType;
    private final TypeMirror targetType;
    private final ExecutableElement method;
    private final CharSequence sourceName;
    private final CharSequence mappingName;

    public MappingInfo(ExecutableElement method) {
        targetType = method.getReturnType();
        if (targetType.getKind().equals(TypeKind.VOID)) {
            throw new IllegalArgumentException("Mapper method should return value. Method will be ignored");
        }
        List<? extends VariableElement> parameters = method.getParameters();
        if (parameters.size() != 1) {
            throw new IllegalArgumentException("Mapper method should have only 1 parameter. Method will be ignored");
        }
        sourceType = parameters.get(0).asType();
        sourceName = parameters.get(0).getSimpleName();
        mappingName = method.getSimpleName();
        this.method = method;
    }

    public MappingInfo(TypeMirror source, TypeMirror target) {
        sourceType = source;
        targetType = target;
        this.sourceName = "source";
        mappingName = "convert";
        method = null;
    }

    public TypeMirror getSourceType() {
        return sourceType;
    }

    public TypeMirror getTargetType() {
        return targetType;
    }

    public ExecutableElement getMethod() {
        return method;
    }

    public CharSequence getMethodName() {
        return mappingName;
    }

    public CharSequence getSourceName() {
        return sourceName;
    }

    public String getSourceFieldName(String defaultName) {
        if (method == null) {
            return defaultName;
        }
        com.devindi.mapper.Mapping annotation = method.getAnnotation(com.devindi.mapper.Mapping.class);
        if (annotation != null && annotation.target().toLowerCase().equals(defaultName)) {
            return annotation.source();
        }
        Mappings mappings = method.getAnnotation(Mappings.class);
        if (mappings != null) {
            for (com.devindi.mapper.Mapping fieldMapping : mappings.value()) {
                if (fieldMapping != null && fieldMapping.target().toLowerCase().equals(defaultName)) {
                    return fieldMapping.source();
                }
            }
        }
        return defaultName;
    }

    @Override
    public String toString() {
        return targetType + " " + mappingName + "(" + sourceType + " " + sourceName + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MappingInfo that = (MappingInfo) o;

        if (!sourceType.equals(that.sourceType)) return false;
        return targetType.equals(that.targetType);

    }

    @Override
    public int hashCode() {
        int result = sourceType.hashCode();
        result = 31 * result + targetType.hashCode();
        return result;
    }

    String getDefaultValueExpression() {
        if (method == null) return "null";
        DefaultValue annotation = method.getAnnotation(DefaultValue.class);
        if (annotation != null) {
            return annotation.value();
        }
        return "null";
    }
}
