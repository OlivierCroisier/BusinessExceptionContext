package net.mokatech.exceptioncontext;

import net.mokatech.exceptioncontext.concurrent.BusinessContextAwareRunnableDecorator;

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
            // Call a method that succeeds to show that its context is successfully pop()-ed from the context stack
            method3a();
            // Now call a dangerous mehod
//            method3b();
        } finally {
            BusinessContext.pop();
        }
    }

    private void method3a() throws BusinessException {
        BusinessContext.push(() -> "In method 3a");
        try {
            System.out.println("Doing serious stuff here");
            new Thread(new BusinessContextAwareRunnableDecorator(new Job())).start();
        } finally {
            BusinessContext.pop();
        }
    }

    private void method3b() throws BusinessException {
        BusinessContext.push(() -> "In method 3b");
        try {
            System.out.println("Doing dangerous stuff here");
            throw new MyBusinessException("Oh noes ! A business problem !", false); // oops
        } finally {
            BusinessContext.pop();
        }
    }

}
