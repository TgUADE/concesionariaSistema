package controller;

public class ExcepcionDuplicado extends ConcesionariaException { // Changed parent
    public ExcepcionDuplicado(String message) {
        super(message);
    }
}
