package net.mokatech.exceptioncontext;

public class Job implements Runnable {
    @Override
    public void run() {
        jobMethod(42);
    }

    private void jobMethod(int param) {
        BusinessContext.push(() -> "In the jobMethod with param " + param);
        try {
            throw new MyBusinessException("Oh noes ! A business problem !", false); // oops
        } catch (BusinessException e) {
            e.printStackTrace();
            e.printContext();
        } finally {
            BusinessContext.pop();
        }
    }


}
