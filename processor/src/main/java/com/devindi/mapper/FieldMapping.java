package com.devindi.mapper;

import com.sun.tools.javac.util.Assert;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

public class FieldMapping {

    private final Mapping info;
    private final ExecutableElement sourceField;
    private final VariableElement targetField;

    public FieldMapping(VariableElement targetField, ExecutableElement sourceField) {
        this(sourceField, targetField, null);
    }

    public FieldMapping(ExecutableElement sourceField, VariableElement targetField, Mapping mappingInfo) {
        if (targetField == null) {
            throw new IllegalArgumentException("Target field can not be null");
        }
        this.sourceField = sourceField;
        this.targetField = targetField;
        this.info = mappingInfo;
    }

    public ExecutableElement getSourceField() {
        return sourceField;
    }

    public VariableElement getTargetField() {
        return targetField;
    }

    public Mapping getInfo() {
        return info;
    }
}
