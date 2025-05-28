
import java.io.*;
import java.util.*;

/**
 * 
 */
public interface InterfazGestorVehiculos {

    public void agregarVehiculo();
    public void eliminarVehiculo();
    public void actualizarVehiculo();
    public void getVehiculos();
    public void registrarVehiculo(Vehiculo vehiculo);
    public void actualizarDatosVehiculo(Vehiculo vehiculo);
    public void eliminarVehiculo(Vehiculo vehiculo);
}