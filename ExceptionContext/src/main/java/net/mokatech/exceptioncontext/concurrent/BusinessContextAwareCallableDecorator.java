package net.mokatech.exceptioncontext.concurrent;

import net.mokatech.exceptioncontext.BusinessContext;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

public class BusinessContextAwareCallableDecorator<V> implements Callable<V> {

    private final Callable<V> delegate;
    private final List<Supplier<String>> newBusinessContext;

    public BusinessContextAwareCallableDecorator(Callable<V> delegate) {
        this(delegate, BusinessContext.get());
    }

    public BusinessContextAwareCallableDecorator(Callable<V> delegate, List<Supplier<String>> newBusinessContext) {
        this.delegate = delegate;
        this.newBusinessContext = newBusinessContext;
    }

    @Override
    public V call() throws Exception {
        List<Supplier<String>> originalBusinessContext = BusinessContext.get();
        BusinessContext.set(newBusinessContext);
        try {
            return delegate.call();
        } finally {
            BusinessContext.set(originalBusinessContext);
        }
    }
}
