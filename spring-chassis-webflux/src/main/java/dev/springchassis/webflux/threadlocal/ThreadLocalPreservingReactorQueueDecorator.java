package dev.springchassis.webflux.threadlocal;

import dev.springchassis.core.threadlocal.ThreadLocalContext;
import dev.springchassis.core.threadlocal.ThreadLocalPreservingDecorator;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;

@SuppressWarnings("rawtypes")
@RequiredArgsConstructor
public final class ThreadLocalPreservingReactorQueueDecorator implements ThreadLocalPreservingDecorator<Queue> {

    @NonNull
    @Getter
    private final ThreadLocalContext threadLocalContext;

    @SuppressWarnings("rawtypes")
    public Queue decorate(Queue queue) {
        return new AbstractQueue<>() {
            @Override
            public int size() {
                return queue.size();
            }

            @SuppressWarnings("unchecked")
            @Override
            public boolean offer(Object o) {
                return queue.offer(new Envelope(o, threadLocalContext.get()));
            }

            @Override
            public Object poll() {
                Object object = queue.poll();
                if (object == null) {
                    return null;
                } else if (object instanceof Envelope) {
                    var envelope = (Envelope) object;
                    threadLocalContext.set(envelope.context);
                    return envelope.body;
                }
                return object;
            }

            @Override
            public Object peek() {
                throw new UnsupportedOperationException();
            }

            @Override
            public Iterator<Object> iterator() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Value
    private static class Envelope {
        Object body;
        Map<Class<?>, Object> context;
    }
}
