package net.mokatech.exceptioncontext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class BusinessContext {

    private static final ThreadLocal<List<Supplier<String>>> CONTEXT = ThreadLocal.withInitial(ArrayList::new);

    public static void reset() {
        CONTEXT.set(new ArrayList<>());
    }

    public static void push(Supplier<String> context) {
        CONTEXT.get().add(context);
    }

    public static void pop() {
        List<Supplier<String>> context = CONTEXT.get();
        context.remove(context.size() - 1);
    }

    public static void set(List<Supplier<String>> newContext) {
        CONTEXT.set(newContext);
    }

    public static List<Supplier<String>> get() {
        return new ArrayList<>(CONTEXT.get());
    }

}
