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
    private final Name sourceName;
    private final Name mappingName;

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

    public TypeMirror getSourceType() {
        return sourceType;
    }

    public TypeMirror getTargetType() {
        return targetType;
    }

    public Name getSourceName() {
        return sourceName;
    }

    public Name getMappingName() {
        return mappingName;
    }

    public ExecutableElement getMethod() {
        return method;
    }
}
