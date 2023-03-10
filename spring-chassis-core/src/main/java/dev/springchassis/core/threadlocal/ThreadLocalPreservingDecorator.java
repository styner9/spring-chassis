package dev.springchassis.core.threadlocal;

public interface ThreadLocalPreservingDecorator<T> {

    T decorate(T src);

}
