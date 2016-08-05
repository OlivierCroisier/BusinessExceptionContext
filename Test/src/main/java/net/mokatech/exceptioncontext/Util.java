package net.mokatech.exceptioncontext;

import java.util.concurrent.TimeUnit;

public final class Util {

    private Util() {
    }

    public static void delay(int timeout) {
        try {
            TimeUnit.SECONDS.sleep(timeout);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
