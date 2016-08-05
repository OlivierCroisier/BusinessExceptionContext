package net.mokatech.exceptioncontext.concurrent;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * <p>Decorator that wraps the {@code Runnable}s and {@code Callable}s passed to the given {@code ExecutorService} in
 * business context-aware decorators (resp. in {@link BusinessContextAwareRunnableDecorator}s and
 * {@link BusinessContextAwareCallableDecorator}s).
 * <p>All methods are simply delegated to the underlying {@code ExecutorService}.
 */
public class BusinessContextAwareExecutorServiceDecorator implements ExecutorService {

    private final ExecutorService delegate;

    public BusinessContextAwareExecutorServiceDecorator(ExecutorService delegate) {
        this.delegate = delegate;
    }

    @Override
    public void shutdown() {
        delegate.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return delegate.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return delegate.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return delegate.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return delegate.awaitTermination(timeout, unit);
    }

    @Override
    public void execute(Runnable command) {
        delegate.execute(new BusinessContextAwareRunnableDecorator(command));
    }

    @Override
    public Future<?> submit(Runnable task) {
        return delegate.submit(new BusinessContextAwareRunnableDecorator(task));
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return delegate.submit(new BusinessContextAwareRunnableDecorator(task), result);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return delegate.submit(new BusinessContextAwareCallableDecorator<>(task));
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        List<BusinessContextAwareCallableDecorator<T>> decoratedTasks = tasks.stream().map(BusinessContextAwareCallableDecorator::new).collect(Collectors.toList());
        return delegate.invokeAll(decoratedTasks);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        List<BusinessContextAwareCallableDecorator<T>> decoratedTasks = tasks.stream().map(BusinessContextAwareCallableDecorator::new).collect(Collectors.toList());
        return delegate.invokeAll(decoratedTasks, timeout, unit);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        List<BusinessContextAwareCallableDecorator<T>> decoratedTasks = tasks.stream().map(BusinessContextAwareCallableDecorator::new).collect(Collectors.toList());
        return delegate.invokeAny(decoratedTasks);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        List<BusinessContextAwareCallableDecorator<T>> decoratedTasks = tasks.stream().map(BusinessContextAwareCallableDecorator::new).collect(Collectors.toList());
        return delegate.invokeAny(decoratedTasks, timeout, unit);
    }
}
