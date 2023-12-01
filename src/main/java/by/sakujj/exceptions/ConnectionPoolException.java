package by.sakujj.exceptions;

public class ConnectionPoolException extends RuntimeException{
    public ConnectionPoolException(String message) {
        super(message);
    }

    public ConnectionPoolException() {
        super();
    }

    public ConnectionPoolException(Throwable cause) {
        super(cause);
    }
}
