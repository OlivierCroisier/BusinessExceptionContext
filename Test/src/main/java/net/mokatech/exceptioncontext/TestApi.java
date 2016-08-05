package net.mokatech.exceptioncontext;

import net.mokatech.exceptioncontext.concurrent.BusinessContextAwareExecutorServiceDecorator;
import net.mokatech.exceptioncontext.concurrent.BusinessContextAwareRunnableDecorator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TestApi {

    public static void main(String[] args) {
        TestApi test = new TestApi();

        BusinessContext.reset();

        try {
            test.method1("hello");
        } catch (BusinessException e) {
            e.printStackTrace();
            e.printContext();
        }

        Util.delay(1);
        test.methodLaunchingNewThread();

        Util.delay(1);
        test.methodUsingExecutorService();
    }

    private void method1(String foo) throws BusinessException {
        BusinessContext.push(() -> "In method 1 with param " + foo);
        try {
            method2(foo, 42);
        } finally {
            BusinessContext.pop();
        }
    }

    public void method2(String foo, int bar) throws BusinessException {
        BusinessContext.push(() -> "In method 2 with params " + foo + " and " + bar);
        try {
            methodThrowingBusinessException();
        } finally {
            BusinessContext.pop();
        }
    }

    private void methodThrowingBusinessException() throws BusinessException {
        BusinessContext.push(() -> "In a method throwing BusinessException");
        try {
            throw new MyBusinessException("Oh noes ! A business problem !", false); // oops
        } finally {
            BusinessContext.pop();
        }
    }

    private void methodLaunchingNewThread() {
        BusinessContext.push(() -> "In a method launching a new thread");
        try {
            new Thread(new BusinessContextAwareRunnableDecorator(new Job())).start();
        } finally {
            BusinessContext.pop();
        }
    }

    private void methodUsingExecutorService() {
        BusinessContext.push(() -> "In a method using an ExecutorService");
        try {
            ExecutorService pool = Executors.newFixedThreadPool(1);
            BusinessContextAwareExecutorServiceDecorator businessContextAwarePool = new BusinessContextAwareExecutorServiceDecorator(pool);
            businessContextAwarePool.submit(new Job());
            pool.shutdown();
            try {
                pool.awaitTermination(1, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } finally {
            BusinessContext.pop();
        }
    }

}
