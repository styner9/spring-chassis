package dev.springchassis.core.util;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.Optional;
import java.util.Set;

@UtilityClass
public class ClassUtil {

    public ClassInfoList allImplementations(@NonNull Class<?> interfaceClass, @NonNull Set<String> basePackages) {
        if (!interfaceClass.isInterface()) {
            throw new IllegalArgumentException();
        }

        try (var graph = scan(basePackages)) {
            return Optional.ofNullable(graph.getClassesImplementing(interfaceClass))
                    .orElseGet(ClassInfoList::emptyList);
        }
    }

    private static ScanResult scan(Set<String> packages) {
        return new ClassGraph().acceptPackages(packages.toArray(String[]::new)).scan();
    }
}
