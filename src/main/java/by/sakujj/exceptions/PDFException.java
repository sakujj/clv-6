package by.sakujj.exceptions;

public class PDFException extends RuntimeException {
    public PDFException(String message) {
        super(message);
    }

    public PDFException() {
        super();
    }

    public PDFException(Throwable cause) {
        super(cause);
    }
}
