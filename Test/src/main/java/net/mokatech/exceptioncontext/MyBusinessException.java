package net.mokatech.exceptioncontext;

public class MyBusinessException extends BusinessException {

    public MyBusinessException(String message) {
        super(message);
    }

    public MyBusinessException(String message, boolean withStackTrace) {
        super(message, withStackTrace);
    }

    public MyBusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public MyBusinessException(String message, Throwable cause, boolean withStackTrace) {
        super(message, cause, withStackTrace);
    }

    public MyBusinessException(Throwable cause) {
        super(cause);
    }

    public MyBusinessException(Throwable cause, boolean withStackTrace) {
        super(cause, withStackTrace);
    }

    public MyBusinessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public MyBusinessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, boolean withStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace, withStackTrace);
    }
}
