package dev.springchassis.core.threadlocal;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.task.TaskDecorator;

@RequiredArgsConstructor
public class ThreadLocalPreservingTaskDecorator implements TaskDecorator, ThreadLocalPreservingDecorator<Runnable> {

    @NonNull
    @Getter
    private final ThreadLocalContext threadLocalContext;

    @Override
    public final Runnable decorate(Runnable runnable) {
        var contexts = threadLocalContext.get();
        return () -> {
            try {
                threadLocalContext.set(contexts);
                runnable.run();
            } finally {
                threadLocalContext.clear();
            }
        };
    }
}
