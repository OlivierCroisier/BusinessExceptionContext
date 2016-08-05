package net.mokatech.exceptioncontext.annotation;

import net.mokatech.exceptioncontext.BusinessContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.function.Supplier;

/**
 * Instruments the method annotated with {@link ResetBusinessContext} or {@link InBusinessContext}
 * to manage the {@link BusinessContext} accordingly
 */
@Aspect
public class BusinessContextAspect {

    /**
     * Pushes / pops the method's context on/from the BusinessContext
     * @param joinPoint The instrumented method
     * @return The method's result
     * @throws Throwable Exception thrown by the instrumented method
     */
    @Around("execution(* *(..)) && @annotation(net.mokatech.exceptioncontext.annotation.InBusinessContext)")
    public Object inBusinessContext(final ProceedingJoinPoint joinPoint) throws Throwable {
        InBusinessContext businessContext = getAnnotation(joinPoint);

        // Cannot use Java 8 features because AspectJ doesn't support them (yet ?)
        Supplier<String> context = new Supplier<String>() {
            @Override
            public String get() {
                return MessageFormat.format(businessContext.value(), joinPoint.getArgs());
            }
        };

        BusinessContext.push(context);
        try {
            return joinPoint.proceed();
        } finally {
            BusinessContext.pop();
        }
    }

    /**
     * Retrieves the {@link InBusinessContext} annotation from the {@code {@link ProceedingJoinPoint}}
     * @param joinPoint The joinpoint
     * @return The annotation present on this jointpoint
     */
    private InBusinessContext getAnnotation(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        return method.getAnnotation(InBusinessContext.class);
    }

    /**
     * Resets the {@link BusinessContext}
     * @param joinPoint The instrumented method
     * @return The method's result
     * @throws Throwable Exception thrown by the instrumented method
     */
    @Around("@annotation(net.mokatech.exceptioncontext.annotation.ResetBusinessContext) && execution(* *(..))")
    public Object resetBusinessContext(final ProceedingJoinPoint joinPoint) throws Throwable {
        BusinessContext.reset();
        return joinPoint.proceed();
    }

}
