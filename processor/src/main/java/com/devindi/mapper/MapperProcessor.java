package com.devindi.mapper;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes({
        "com.devindi.mapper.Mapper"
})
public class MapperProcessor extends AbstractProcessor {

    private ProcessingEnvironment processingEnv;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(Mapper.class)) {
            ElementKind kind = annotatedElement.getKind();
            if (kind.equals(ElementKind.INTERFACE)) {
                createInterfaceImplementation((TypeElement) annotatedElement);
            } else {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "Mapper supports interfaces only", annotatedElement);
                return false;
            }
        }
        return false;
    }

    private void createInterfaceImplementation(TypeElement interfaceElement) {
        MapperGenerator generator = MapperGenerator.create(interfaceElement, processingEnv);
        if (generator == null) return;
        TypeSpec implSpec = generator.generate();

        final JavaFile javaFile = JavaFile.builder(interfaceElement.getEnclosingElement().toString(), implSpec)
                .addFileComment("Generated by Mapping processor, do not modify. Created at $S", new Date())
                .build();

        try {
            final JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(
                    javaFile.packageName + "." + implSpec.name, interfaceElement);
            try (final Writer writer = new BufferedWriter(sourceFile.openWriter())) {
                javaFile.writeTo(writer);
            }
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage(), interfaceElement);
        }
    }
}
