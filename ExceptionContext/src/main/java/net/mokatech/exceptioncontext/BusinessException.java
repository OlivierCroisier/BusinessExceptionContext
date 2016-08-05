package net.mokatech.exceptioncontext;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.function.Supplier;

public class BusinessException extends Exception {

    private static ThreadLocal<Boolean> STACKTRACE_FLAG = new ThreadLocal<>();
    private boolean hasStackTrace = false;
    private List<Supplier<String>> context;

    {
        context = BusinessContext.get();
    }

    public BusinessException(String message) {
        this(message, false);
    }

    public BusinessException(String message, boolean withStackTrace) {
        super(beforeSuper(message, withStackTrace));
    }

    public BusinessException(String message, Throwable cause) {
        this(message, cause, false);
    }

    public BusinessException(String message, Throwable cause, boolean withStackTrace) {
        super(beforeSuper(message, withStackTrace), cause);
    }

    public BusinessException(Throwable cause) {
        this(cause, false);
    }

    public BusinessException(Throwable cause, boolean withStackTrace) {
        super(beforeSuper(cause, withStackTrace));
    }

    public BusinessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        this(message, cause, enableSuppression, writableStackTrace, false);
    }

    public BusinessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, boolean withStackTrace) {
        super(beforeSuper(message, withStackTrace), cause, enableSuppression, writableStackTrace);
    }

    @Override
    public Throwable fillInStackTrace() {
        if (STACKTRACE_FLAG.get()) {
            super.fillInStackTrace();
        }
        return this;
    }

    public void printContext() {
        printContextWithPrintStream(System.err, "\n while ");
    }

    public void printContext(PrintStream printer, CharSequence separator) {
        printContextWithPrintStream(printer, separator);
    }

    private void printContextWithPrintStream(PrintStream printer, CharSequence separator) {
        synchronized (printer) {
            printer.print(this);
            for (Supplier<String> contextElement : context) {
                printer.append(separator).append(contextElement.get());
            }
            printer.println();
        }
    }

    public void printContext(PrintWriter printer, CharSequence separator) {
        synchronized (printer) {
            printer.print(this);
            for (Supplier<String> contextElement : context) {
                printer.append(separator).append(contextElement.get());
            }
            printer.println();
        }
    }

    @SuppressWarnings("unchecked")
    public Supplier<String>[] getContext() {
        return context.toArray(new Supplier[0]);
    }

    public boolean hasStackTrace() {
        return hasStackTrace;
    }

    private static <T> T beforeSuper(T dummy, boolean withStackTrace) {
        BusinessException.STACKTRACE_FLAG.set(withStackTrace);
        return dummy;
    }

}
