package com.devindi.mapper.model;

import com.devindi.mapper.Mapping;
import com.devindi.mapper.Mappings;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

/**
 * Describes mapping from source entity to target entity
 */
public abstract class MappingInfo {

    abstract TypeMirror getTargetType();

    abstract VariableElement getSource();

    abstract String getSourceFieldName(String defaultName);

    /**
     * Describes mapping that defined by user at interface.
     */
    static class Defined extends MappingInfo {

        private final ExecutableElement method;

        public Defined(ExecutableElement method) {
            this.method = method;
        }

        @Override
        TypeMirror getTargetType() {
            return method.getReturnType();
        }

        @Override
        VariableElement getSource() {
            return method.getParameters().get(0);
        }

        @Override
        String getSourceFieldName(String defaultName) {
            Mapping annotation = method.getAnnotation(Mapping.class);
            if (annotation != null && annotation.target().toLowerCase().equals(defaultName)) {
                return annotation.source();
            }
            Mappings mappings = method.getAnnotation(Mappings.class);
            if (mappings != null) {
                for (Mapping fieldMapping : mappings.value()) {
                    if (fieldMapping != null && fieldMapping.target().toLowerCase().equals(defaultName)) {
                        return fieldMapping.source();
                    }
                }
            }
            return defaultName;
        }
    }

    /**
     * Describes mapping that required by defined mapping
     */
    static class Generated extends MappingInfo {

        private final TypeMirror target;
        private final VariableElement source;

        public Generated(TypeMirror target, VariableElement source) {
            this.target = target;
            this.source = source;
        }

        @Override
        TypeMirror getTargetType() {
            return target;
        }

        @Override
        VariableElement getSource() {
            return source;
        }

        @Override
        String getSourceFieldName(String defaultName) {
            return defaultName;
        }
    }
}
