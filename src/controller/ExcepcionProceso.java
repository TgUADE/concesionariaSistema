package controller;

public class ExcepcionProceso extends ConcesionariaException {
    public ExcepcionProceso(String message) {
        super(message);
    }

    public ExcepcionProceso(String message, Throwable cause) {
        super(message, cause);
    }
}
