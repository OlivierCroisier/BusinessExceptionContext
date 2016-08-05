package net.mokatech.exceptioncontext.annotation;

import net.mokatech.exceptioncontext.BusinessContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.function.Supplier;

@Aspect
public class BusinessContextAspect {

    @Around("execution(* *(..)) && @annotation(net.mokatech.exceptioncontext.annotation.InBusinessContext)")
    public Object inBusinessContext(final ProceedingJoinPoint joinPoint) throws Throwable {
        InBusinessContext businessContext = getAnnotation(joinPoint);

        BusinessContext.push(new Supplier<String>() {
            @Override
            public String get() {
                return MessageFormat.format(businessContext.value(), joinPoint.getArgs());
            }
        });
        try {
            return joinPoint.proceed();
        } finally {
            BusinessContext.pop();
        }
    }

    private InBusinessContext getAnnotation(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        return method.getAnnotation(InBusinessContext.class);
    }

    @Around("@annotation(net.mokatech.exceptioncontext.annotation.ResetBusinessContext) && execution(* *(..))")
    public Object resetBusinessContext(final ProceedingJoinPoint joinPoint) throws Throwable {
        BusinessContext.reset();
        return joinPoint.proceed();
    }

}
