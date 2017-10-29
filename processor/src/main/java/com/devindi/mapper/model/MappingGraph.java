package com.devindi.mapper.model;

import com.devindi.mapper.generator.ElementResolver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;

/**
 * Data structure to describe defining mappings and its dependencies.
 */
public class MappingGraph {

    private final Set<Node> defined;
    private final List<Node> all;
    private final ElementResolver resolver;

    public MappingGraph(TypeElement typeElement, ElementResolver resolver) {
        this.resolver = resolver;
        defined = ElementFilter.methodsIn(typeElement.getEnclosedElements())
                .stream()
                .filter(this::shouldGenerateMapper)
                .map(element -> new Node(new MappingInfo.Defined(element)))
                .collect(Collectors.toSet());
        all = new ArrayList<>(defined);
        build();
    }

    private void build() {
        Stack<Node> nodesToDiscovery = new Stack<>();
        defined.forEach(nodesToDiscovery::push);
        while (!nodesToDiscovery.empty()) {
            Node n = nodesToDiscovery.pop();
            for (MappingInfo info : findDependencies(n.info)) {
                Node generatedNode = new Node(info);
                n.addChild(generatedNode);
                nodesToDiscovery.push(generatedNode);
                all.add(generatedNode);
            }
        }
    }

    /**
     * We generate code for non-void abstract methods with single argument
     * @param method method to check
     * @return boolean. true if we should generate code.
     */
    private boolean shouldGenerateMapper(ExecutableElement method) {
        if (method.getReturnType().getKind().equals(TypeKind.VOID)) {
            return false;
        }
        if (method.getParameters().size() != 1) {
            return false;
        }
        // TODO: 08.10.17 implement check is method abstract.
        //Currently we process interfaces only so all methods are abstract.
        return true;
    }

    private Set<MappingInfo> findDependencies(MappingInfo info) {
        Set<MappingInfo> result = new HashSet<>();
        ExecutableElement constructorElement = resolver.resolveConstructor(info.getTargetType());
        List<? extends VariableElement> constructorParameters = constructorElement.getParameters();

        Map<String, ExecutableElement> argumentGetters = resolver.resolveGetters(info.getSource().asType());

        for (VariableElement parameter : constructorParameters) {
            String sourceFieldName = info.getSourceFieldName(parameter.getSimpleName().toString().toLowerCase());
            ExecutableElement getter = argumentGetters.get(sourceFieldName.toLowerCase());
            if (getter == null) {
                //failed to resolve getter for parameter
            } else if (!parameter.asType().equals(getter.getReturnType())) {
                Node node = findMapping(getter.getReturnType(), parameter.asType());
                if (node == null) {
                    result.add(new MappingInfo.Generated(getter.getReturnType(), parameter));
                }
            }
        }
        return result;
    }

    private Node findMapping(TypeMirror source, TypeMirror target) {
        for (Node node : all) {
            if (node.info.getSource().asType().equals(source) && node.info.getTargetType().equals(target)) {
                return node;
            }
        }
        return null;
    }

    static class Node {

        private final MappingInfo info;
        private final Set<Node> children;

        Node(MappingInfo info) {
            this.info = info;
            children = new HashSet<>();
        }

        void addChild(Node n) {
            children.add(n);
        }
    }
}
