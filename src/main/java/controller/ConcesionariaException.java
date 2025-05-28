package controller; // Or specific exceptions package

public class ConcesionariaException extends Exception {
    public ConcesionariaException(String message) {
        super(message);
    }

    public ConcesionariaException(String message, Throwable cause) {
        super(message, cause);
    }
}
