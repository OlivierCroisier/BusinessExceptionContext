ExceptionContext
==

ExceptionContext : trying to improve business exceptions.

Overview
----

In "corporate" projects, exceptions are often categorized as `TechnicalExceptions` and `BusinessExceptions`.
 
As technical exceptions usually have a wide application range (eg. `DataAccessExceptions`), their stacktraces help finding out the exact technical context in which they occurred.

However, for business exceptions, which are often more specific (eg. `AuthenticationException`) and whose origin in the code is well known, stacktraces are usually useless - but still expensive to generate. What would be more interesting though, is the _business context_ in which they occurred ; for example, knowing that the system failed while processing order #42's payment as part of the daily book-keeping batch is an important information.

To achieve this, a "business context stack" needs to be maintained, on which business-related informations can be pushed and popped as the business processes are respectively started and terminated. This is the role of the `BusinessContext` class, loosely inspired from Log4J's `MDC` and Spring Security's `SecurityContextHolder`.

    public class BusinessContext {
        public static void reset();
        public static void push(Supplier<String> context);
        public static void pop();
        public static List<Supplier<String>> get();
        public static void set(List<Supplier<String>> newContext);
    }
    
The `BusinessContext` can be managed in two ways : 
* By manually calling the `push()` and `pop()` methods at the beginning and end of each interesting method ; 
* Or by annotating those methods with `@InBusinessContext` and/or `@ResetBusinessContext`. Those annotations trigger the provided `BusinessContextAspect`, which, once woven by AspectJ, manages the `BusinessContext` accordingly for you.

This `BusinessContext` is then captured by `BusinessContextException`s (and its subclasses) as they are thrown, and displayed in a similar way as traditional stacktraces :

    try {
        dangerousMethod();
    } catch (BusinessException e) {
        e.printContext();
    }    
 > 
    net.mokatech.exceptioncontext.BusinessException: Oh noes ! A business problem !
     while In method 1 with param hello
     while In method 2 with params hello and 42 
     while In a method throwing BusinessException
    
    
How to compile and run
----

This project is split in two :
* "ExceptionContext" contains all the "core" classes and the aspect
* "Test" shows how to use them, manually or via the aspect

Requirements :
* Java 8
* Maven 3
     
Getting started :
1. Compile the library :   
   In the "ExceptionContext" project root : `mvn install`
1. Compile the tests :  
   In the "Test" project root : `mvn compile`
1. Run the tests :  
   * Run the tests from your IDE (be sure to compile with Maven only, or enable AspectJ support in the IDE)
   * Run `mvn exec:exec` to run the `TestAspect` class
     
     
Technical details
----

This section details some technical points of interest and other glorious hacks.

### BusinessContext

For the sake of this experiment, `BusinessContext` maintains a simple `List<Supplier<String>>` stored in a static `ThreadLocal`. 

In a real system, we would probably want to define more sophisticated data structures as stack elements, and to decouple the data from its storage system. A good inspiration would be Spring Security's `SecurityContextHolder`.

### BusinessException

As stated above, stacktraces are expensive to generate.

As part of the process of instanciating any exception, `Throwable`'s constructor ends up being called. One of its duties is to call the native `fillInStackTrace()` method, which is responsible for stopping the thread and walking all its stack to collect interesting data such as the class and method names, line numbers etc. As you can imagine, this puts quite a heavy burden on the JVM.

So in the rare event when stacktraces are not needed, an interesting option is to override the `fillInStackTrace()` method with a no-op one.  
This is what the `BusinessException` does by default ; the boolean parameter (`withStackTrace`) that some of its constructors accept allows to revert to the standart behaviour if needed.
   
That boolean poses interesting problems on its own.  
Due to the way Java initialises class instances (first the parent hierarchy, then the class itself), by the time the overriden `fillInStackTrace()` method is called, the `withStackTrace` field still has its default value (`false`), and not yet its "real" value. This leads to the stacktrace generation being always skipped.

To value the `withStackTrace` field *before* calling the superclass' constructors, `BusinessException` implements a well-known ugly hack involving a `ThreadLocal` field and a static method wrapping some of the parameters passed to the superclass' constructor.  
 This of course, works only with superclasses accepting constructor parameters ; which is why the default no-arg constructor (which would call `Exception`'s no-arg constructor) is not implemented by `BusinessException`. 
 
### BusinessContextAspect

The pointcut intercepting "all methods bearing the `InBusinessContext` annotation" is defined like this :

     @Around("execution(* *(..)) && @annotation(net.mokatech.exceptioncontext.annotation.InBusinessContext)")

The `@annotation(...)` part is obvious ; the `execution(...)` much less.

AspectJ sees a method call as two separate events : a `call()` in the caller method, and the `execution()` of the target method.  
In both events, the target method would match a simple `@annotation(...)` pointcut definition, and the aspect would end up being woven twice. To prevent this, an additional `call(...)` or `execution(...)` predicate must be added.

Finally, it seems that AspectJ is still unaware of Java 8 features such as lambda expressions or method references. This is why the `Supplier` pushed onto the context stack is defined as an anonymous inline class, and not a method reference. 


Conclusion
----

Please let me know what you think about this experiment !  
Do you think technical and business exceptions should be differentiated and managed differently ? What about the idea of a "business context" (that could also be used elsewhere, eg. in the logs) ?

Do not hesitate to fork and/or comment !




