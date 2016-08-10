package net.mokatech.exceptioncontext;

public class MyBusinessException extends BusinessException {

    public MyBusinessException() {
        super();
    }

    public MyBusinessException(boolean withStackTrace) {
        super(withStackTrace);
    }

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

    public MyBusinessException(String message, Throwable cause, boolean enableSuppression, boolean withStackTrace) {
        super(message, cause, enableSuppression, withStackTrace);
    }
}
