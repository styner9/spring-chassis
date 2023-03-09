package dev.springchassis.core.threadlocal;

import lombok.NonNull;
import org.springframework.core.task.TaskDecorator;

public class ThreadLocalPreservingTaskDecorator implements TaskDecorator {

    private final ThreadLocalAccessorRegistry registry;

    public ThreadLocalPreservingTaskDecorator(@NonNull ThreadLocalAccessorRegistry registry) {
        this.registry = registry;
    }

    @Override
    public final Runnable decorate(Runnable runnable) {
        var contexts = registry.getContexts();
        return () -> {
            try {
                registry.setContexts(contexts);
                runnable.run();
            } finally {
                registry.clearContexts();
            }
        };
    }
}
