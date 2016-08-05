package net.mokatech.exceptioncontext;

public class Job implements Runnable {
    @Override
    public void run() {
        foobar(42);
    }

    private void foobar(int param) {
        BusinessContext.push(() -> "In foobar with param " + param);
        System.out.println("Doing some async work...");
        try {
            throw new MyBusinessException("Oh noes ! A business problem !", false); // oops
        } catch (BusinessException e) {
            e.printContext();
        } finally {
            BusinessContext.pop();
        }
    }


}
