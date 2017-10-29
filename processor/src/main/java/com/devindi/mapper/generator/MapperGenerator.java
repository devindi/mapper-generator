package com.devindi.mapper.generator;

import com.devindi.mapper.model.MappingGraph;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

/**
 * Generates mapper class declaration for javapoet
 */
public class MapperGenerator {

    private final ElementResolver resolver;

    public MapperGenerator(ProcessingEnvironment processingEnv) {
        if (processingEnv == null) {
            throw new IllegalArgumentException("Environment is null");
        }
        this.resolver = new ElementResolver(processingEnv);
    }

    /**
     * Generate mapper class declaration.
     * @param mapperElement interface type element
     * @return declaration of mapper interface implementation. Will return null for non-interface
     * parameter (class)
     */
    // TODO: 29.10.17 implement mapper generation from abstract class
    public TypeSpec generateMapperSpec(TypeElement mapperElement) {
        ElementKind kind = mapperElement.getKind();
        if (kind.equals(ElementKind.INTERFACE)) {
            return generateFromInterface(mapperElement);
        }
        else {
            //Mapper supports interface only
            return null;
        }
    }

    private TypeSpec generateFromInterface(TypeElement interfaceElement) {
        // 1. collect mappings
        MappingGraph graph = new MappingGraph(interfaceElement, resolver);
        // 2. Generate specs
        // 3. Done!
        return null;
    }
}
