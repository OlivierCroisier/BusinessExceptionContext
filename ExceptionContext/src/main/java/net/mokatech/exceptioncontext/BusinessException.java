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
 * the constructors.
 * <p>As an alternative, this exception captures the "business context" exposed by the {@link BusinessContext} a the
 * time it is thrown. The {@code printContext()} methods mirror the {@code printStackTrace()} ones and, together with
 * {@link BusinessException#getContext()}, allow to retrieve and print the business context.
 */
public class BusinessException extends Exception {

    /** The business context captured on exception construction */
    private final List<Supplier<String>> context = BusinessContext.get();

    public BusinessException() {
        super(null, null, true, false);
    }

    public BusinessException(boolean withStackTrace) {
        super(null, null, true, withStackTrace);
    }

    public BusinessException(String message) {
        super(message, null, true, false);
    }

    public BusinessException(String message, boolean withStackTrace) {
        super(message, null, true, withStackTrace);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause, true, false);
    }

    public BusinessException(String message, Throwable cause, boolean withStackTrace) {
        super(message, cause, true, withStackTrace);
    }

    public BusinessException(Throwable cause) {
        super(null, cause, true, false);
    }

    public BusinessException(Throwable cause, boolean withStackTrace) {
        super(null, cause, true, withStackTrace);
    }

    public BusinessException(String message, Throwable cause, boolean enableSuppression, boolean withStackTrace) {
        super(message, cause, enableSuppression, withStackTrace);
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

}
