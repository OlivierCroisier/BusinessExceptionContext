package net.mokatech.exceptioncontext;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.function.Supplier;

/**
 * <p>Base class for business-oriented exceptions.
 * <p>Technical stacktraces do not make much sense in the context of a business exception, so they are disabled by
 * default. As a side benefit, this yields much better performance, as the JVM does not have to walk the thread's
 * stack to create the stacktrace. However, they can be re-enabled by passing {@code true} as the last parameter of
 * the constructors (the {@link BusinessException#hasStackTrace()} method tells if a stacktrace has been captured by
 * this exception).
 * <p>As an alternative, this exception captures the "business context" exposed by the {@link BusinessContext} a the
 * time it is thrown. The {@code printContext()} methods mirror the {@code printStackTrace()} ones and, together with
 * {@link BusinessException#getContext()}, allow to retrieve and print the business context.
 */
public class BusinessException extends Exception {

    /**
     * Together with the {@code beforeSuper()} methos, allows to pass the {@code withStackTrace} parameter to the
     * {@code fillInStackTrace} method before superclass initialization.
     */
    private static final ThreadLocal<Boolean> STACKTRACE_FLAG = new ThreadLocal<>();
    /** Whether this exception has captured a stacktrace */
    private boolean withStackTrace = STACKTRACE_FLAG.get();
    /** The business context captured on exception construction */
    private final List<Supplier<String>> context = BusinessContext.get();

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

    /** Print the business context stack on the standard error stream. */
    public void printContext() {
        printContextWithPrintStream(System.err, "\n while ");
    }

    /** Print the business context stack on the given {@code PrintStream}. */
    public void printContext(PrintStream printer, CharSequence separator) {
        printContextWithPrintStream(printer, separator);
    }

    /** Print the business context stack on the given {@code PrintStream}. */
    private void printContextWithPrintStream(PrintStream printer, CharSequence separator) {
        synchronized (printer) {
            printer.print(this);
            for (Supplier<String> contextElement : context) {
                printer.append(separator).append(contextElement.get());
            }
            printer.println();
        }
    }

    /** Print the business context stack on the given {@code PrintWriter}. */
    public void printContext(PrintWriter printer, CharSequence separator) {
        synchronized (printer) {
            printer.print(this);
            for (Supplier<String> contextElement : context) {
                printer.append(separator).append(contextElement.get());
            }
            printer.println();
        }
    }

    /**
     * Get the captured business context
     * @return The business context
     */
    @SuppressWarnings("unchecked")
    public Supplier<String>[] getContext() {
        return context.toArray(new Supplier[0]);
    }

    /**
     * Tells whether this exception has captured a technical stacktrace
     * @return {@code true} if it has captured a stacktrace, {@code false} otherwise.
     */
    public boolean hasStackTrace() {
        return withStackTrace;
    }

    /**
     * Hack to save the {@code withStackTrace} paramter before invoking the superclass' constructor.
     * @param dummy Some parameter that will be returned untouched.
     * @param withStackTrace The flag to save
     * @param <T> The dummy input parameter
     * @return The dummy input parameter
     */
    private static <T> T beforeSuper(T dummy, boolean withStackTrace) {
        BusinessException.STACKTRACE_FLAG.set(withStackTrace);
        return dummy;
    }

}
