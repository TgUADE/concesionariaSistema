package controller; // Using controller package as specified for now

public interface InterfazVehiculoService {
    Vehiculo registrarVehiculo(Vehiculo vehiculo) throws ExcepcionDuplicado, ExcepcionValidacion;
}
