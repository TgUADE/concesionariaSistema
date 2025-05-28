package controller; // Using controller package as specified for now

public interface InterfazClienteService {
    Cliente registrarCliente(Cliente cliente) throws ExcepcionDuplicado, ExcepcionValidacion;
}
