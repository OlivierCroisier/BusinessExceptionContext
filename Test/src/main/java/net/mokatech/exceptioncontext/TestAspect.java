package net.mokatech.exceptioncontext;

import net.mokatech.exceptioncontext.annotation.InBusinessContext;
import net.mokatech.exceptioncontext.annotation.ResetBusinessContext;
import net.mokatech.exceptioncontext.concurrent.BusinessContextAwareExecutorServiceDecorator;
import net.mokatech.exceptioncontext.concurrent.BusinessContextAwareRunnableDecorator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TestAspect {

    @ResetBusinessContext
    public static void main(String[] args) {
        TestAspect test = new TestAspect();

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

    @InBusinessContext("In method 1 with param {0}")
    private void method1(String foo) throws BusinessException {
        method2(foo, 42);
    }

    @InBusinessContext("In method 2 with params {0} and {1} ")
    public void method2(String foo, int bar) throws BusinessException {
        methodThrowingBusinessException();
    }

    @InBusinessContext("In a method throwing BusinessException")
    private void methodThrowingBusinessException() throws BusinessException {
        throw new BusinessException("Oh noes ! A business problem !", false);
    }

    @InBusinessContext("In a method launching a new thread")
    private void methodLaunchingNewThread() {
        new Thread(new BusinessContextAwareRunnableDecorator(new Job())).start();
    }

    @InBusinessContext("In a method using an ExecutorService")
    private void methodUsingExecutorService() {
        ExecutorService pool = Executors.newFixedThreadPool(1);
        BusinessContextAwareExecutorServiceDecorator businessContextAwarePool = new BusinessContextAwareExecutorServiceDecorator(pool);
        businessContextAwarePool.submit(new Job());
        pool.shutdown();
        try {
            pool.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
