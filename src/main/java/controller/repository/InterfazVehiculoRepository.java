package controller.repository;

import controller.Vehiculo;
import controller.Vehiculo.TipoVehiculo;
import controller.ExcepcionDuplicado;
import java.util.List;
import java.util.Optional;

public interface InterfazVehiculoRepository {
    Vehiculo save(Vehiculo vehiculo) throws ExcepcionDuplicado; // Add or update
    Optional<Vehiculo> findById(int id);
    Optional<Vehiculo> findByMarcaAndModeloAndChasis(String marca, String modelo, String chasis);
    List<Vehiculo> findAll();
    List<Vehiculo> findByTipoAndDisponibleVenta(TipoVehiculo tipo, boolean disponible);
    List<Vehiculo> findByDisponibleVenta(boolean disponible); // Added as per task description
    void deleteById(int id);
}
