package main.java.com.i0dev.util;

public class i0devException extends Exception{

    private static final long serialVersionUID = 0;
    private Throwable cause;

    public i0devException(String message) {
        super(message);
    }

    public i0devException(Throwable cause) {
        super(cause.getMessage());
        this.cause = cause;
    }

    public Throwable getCause() {
        return this.cause;
    }

}
