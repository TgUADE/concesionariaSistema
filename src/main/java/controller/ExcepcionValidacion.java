package controller;

public class ExcepcionValidacion extends ConcesionariaException {
    public ExcepcionValidacion(String message) {
        super(message);
    }

    public ExcepcionValidacion(String message, Throwable cause) {
        super(message, cause);
    }
}
