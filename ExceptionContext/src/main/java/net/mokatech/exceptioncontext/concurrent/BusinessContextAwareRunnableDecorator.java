package net.mokatech.exceptioncontext.concurrent;

import net.mokatech.exceptioncontext.BusinessContext;

import java.util.List;
import java.util.function.Supplier;

/**
 * Decorator that sets the correct {@code BusinessContext} during the execution of the given {@code Runnable}, and then
 * resets it to its previous value.
 */
public class BusinessContextAwareRunnableDecorator implements Runnable {

    private final Runnable delegate;
    private final List<Supplier<String>> newBusinessContext;

    public BusinessContextAwareRunnableDecorator(Runnable delegate) {
        this(delegate, BusinessContext.get());
    }

    public BusinessContextAwareRunnableDecorator(Runnable delegate, List<Supplier<String>> newBusinessContext) {
        this.delegate = delegate;
        this.newBusinessContext = newBusinessContext;
    }

    @Override
    public void run() {
        List<Supplier<String>> originalBusinessContext = BusinessContext.get();
        BusinessContext.set(newBusinessContext);
        try {
            delegate.run();
        } finally {
            BusinessContext.set(originalBusinessContext);
        }
    }
}
