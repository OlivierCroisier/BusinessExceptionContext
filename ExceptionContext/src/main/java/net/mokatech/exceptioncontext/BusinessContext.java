package net.mokatech.exceptioncontext;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Maintains a "context" as a stack of {@code String}s
 */
public class BusinessContext {

    private static final ThreadLocal<List<Supplier<String>>> CONTEXT = ThreadLocal.withInitial(ArrayList::new);

    /**
     * Reset the stack
     */
    public static void reset() {
        CONTEXT.set(new ArrayList<>());
    }

    /**
     * Push a new context on the stack
     * @param context The context to push
     */
    public static void push(Supplier<String> context) {
        CONTEXT.get().add(context);
    }

    /**
     * Pop the latest context from the stack
     */
    public static void pop() {
        List<Supplier<String>> context = CONTEXT.get();
        context.remove(context.size() - 1);
    }

    /**
     * Replace the whole context by a new one
     * @param newContext The new context [not null]
     */
    public static void set(List<Supplier<String>> newContext) {
        Objects.requireNonNull(newContext, "The new context must not be null.");
        CONTEXT.set(newContext);
    }

    /**
     * Retrieve the whole context.
     * @return A copy of the current context.
     */
    public static List<Supplier<String>> get() {
        return new ArrayList<>(CONTEXT.get());
    }

}
