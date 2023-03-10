package dev.springchassis.webflux.threadlocal;

import dev.springchassis.core.threadlocal.ThreadLocalContext;
import dev.springchassis.core.threadlocal.ThreadLocalPreservingDecorator;
import dev.springchassis.core.threadlocal.ThreadLocalPreservingTaskDecorator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import reactor.core.publisher.Hooks;
import reactor.core.scheduler.Schedulers;

@RequiredArgsConstructor
@Slf4j
public class ThreadLocalPreservingDecoratorRegistrar implements BeanFactoryPostProcessor {

    private static final String KEY = ThreadLocalPreservingDecorator.class.getName();

    private final ThreadLocalContext context;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        var schedulerDecorator = new ThreadLocalPreservingTaskDecorator(context);
        Schedulers.onScheduleHook(KEY, schedulerDecorator::decorate);

        var queueDecorator = new ThreadLocalPreservingReactorQueueDecorator(context);
        Hooks.addQueueWrapper(KEY, queueDecorator::decorate);
    }
}
