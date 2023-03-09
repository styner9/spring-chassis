package dev.springchassis.core.threadlocal;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;
import java.util.function.Supplier;

@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class ThreadLocalAccessor<T> {

    @NonNull
    @Getter
    @EqualsAndHashCode.Include
    private final Class<T> objectType;

    @NonNull
    private final Supplier<T> getter;

    @NonNull
    private final Consumer<T> setter;

    @NonNull
    private final Runnable cleaner;

    public T get() {
        return getter.get();
    }

    @SuppressWarnings("unchecked")
    public void set(Object obj) {
        if (obj == null) {
            setter.accept(null);
        } else if (objectType.isAssignableFrom(obj.getClass())) {
            setter.accept((T) obj);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void clear() {
        cleaner.run();
    }
}
