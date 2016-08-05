package net.mokatech.exceptioncontext;

import net.mokatech.exceptioncontext.annotation.InBusinessContext;
import net.mokatech.exceptioncontext.annotation.ResetBusinessContext;
import net.mokatech.exceptioncontext.concurrent.BusinessContextAwareRunnableDecorator;

public class TestAspect {

    @ResetBusinessContext
    public static void main(String[] args) {
        TestAspect test = new TestAspect();

        try {
            test.method1("hello");
        } catch (BusinessException e) {
            e.printStackTrace();
            e.printContext();
        }
    }

    @InBusinessContext("In method 1 with param {0}")
    private void method1(String foo) throws BusinessException {
        method2(foo, 42);
    }

    @InBusinessContext("In method 2 with params {0} and {1} ")
    public void method2(String foo, int bar) throws BusinessException {
        // Call a method that succeeds to show that its context is successfully pop()-ed from the context stack
        method3a();
        // Now call a dangerous mehod
        //method3b();
    }

    @InBusinessContext("In method 3a")
    private void method3a() throws BusinessException {
        System.out.println("Doing serious stuff here");
        new Thread(new BusinessContextAwareRunnableDecorator(new Job())).start();
    }

    @InBusinessContext("In method 3b")
    private void method3b() throws BusinessException {
        System.out.println("Doing dangerous stuff here");
        throw new BusinessException("Oh noes ! A business problem !", false);
    }

}
