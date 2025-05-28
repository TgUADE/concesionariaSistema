package controller.repository;

import com.google.gson.reflect.TypeToken;
import controller.Vehiculo;
import controller.Vehiculo.TipoVehiculo;
import controller.ExcepcionDuplicado;
import controller.util.GsonUtil;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class GsonVehiculoRepositoryImpl implements InterfazVehiculoRepository {
    private final String filePath;
    private List<Vehiculo> vehiculos;
    private AtomicInteger maxId;

    public GsonVehiculoRepositoryImpl(String filePath) {
        this.filePath = filePath;
        this.vehiculos = new ArrayList<>();
        this.maxId = new AtomicInteger(0);
        loadData();
    }

    private void loadData() {
        try (FileReader reader = new FileReader(filePath)) {
            Type listType = new TypeToken<ArrayList<Vehiculo>>() {}.getType();
            List<Vehiculo> loadedVehiculos = GsonUtil.getGson().fromJson(reader, listType);
            if (loadedVehiculos != null) {
                this.vehiculos.addAll(loadedVehiculos);
                for (Vehiculo vehiculo : vehiculos) {
                    if (vehiculo.getId() > maxId.get()) {
                        maxId.set(vehiculo.getId());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Advertencia: No se encontró el archivo de vehículos '" + filePath + "'. Se iniciará con una lista vacía.");
            this.vehiculos = new ArrayList<>();
        } catch (com.google.gson.JsonSyntaxException e) {
            System.err.println("Error: El archivo de vehículos '" + filePath + "' contiene JSON malformado. Se iniciará con una lista vacía.");
            e.printStackTrace();
            this.vehiculos = new ArrayList<>();
        }
    }

    private void saveData() {
        try (FileWriter writer = new FileWriter(filePath)) {
            GsonUtil.getGson().toJson(vehiculos, writer);
        } catch (IOException e) {
            System.err.println("Error al guardar datos de vehículos en '" + filePath + "': " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Vehiculo save(Vehiculo vehiculo) throws ExcepcionDuplicado {
        if (vehiculo == null) {
            throw new IllegalArgumentException("El vehículo no puede ser nulo.");
        }

        Optional<Vehiculo> existingByDetails = findByMarcaAndModeloAndChasis(vehiculo.getMarca(), vehiculo.getModelo(), vehiculo.getChasis());
        if (existingByDetails.isPresent() && existingByDetails.get().getId() != vehiculo.getId()) {
            throw new ExcepcionDuplicado("Ya existe un vehículo con la misma marca, modelo y chasis: " +
                                         vehiculo.getMarca() + ", " + vehiculo.getModelo() + ", " + vehiculo.getChasis());
        }

        if (vehiculo.getId() != 0) {
            Optional<Vehiculo> existingById = findById(vehiculo.getId());
            if (existingById.isPresent()) {
                vehiculos.remove(existingById.get());
            } else {
                 if (vehiculo.getId() > maxId.get()) {
                    maxId.set(vehiculo.getId());
                }
            }
        } else {
            vehiculo.setId(maxId.incrementAndGet());
        }
        
        vehiculos.add(vehiculo);
        saveData();
        return vehiculo;
    }

    @Override
    public Optional<Vehiculo> findById(int id) {
        return vehiculos.stream()
                        .filter(v -> v.getId() == id)
                        .findFirst();
    }

    @Override
    public Optional<Vehiculo> findByMarcaAndModeloAndChasis(String marca, String modelo, String chasis) {
        if (marca == null || modelo == null || chasis == null) return Optional.empty();
        return vehiculos.stream()
                        .filter(v -> marca.equals(v.getMarca()) &&
                                     modelo.equals(v.getModelo()) &&
                                     chasis.equals(v.getChasis()))
                        .findFirst();
    }

    @Override
    public List<Vehiculo> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(vehiculos));
    }

    @Override
    public List<Vehiculo> findByTipoAndDisponibleVenta(TipoVehiculo tipo, boolean disponible) {
        if (tipo == null) { // If tipo is null, effectively behaves like findByDisponibleVenta
            return findByDisponibleVenta(disponible);
        }
        return vehiculos.stream()
                        .filter(v -> tipo.equals(v.getTipo()) && v.isDisponibleVenta() == disponible)
                        .collect(Collectors.toList());
    }
    
    @Override
    public List<Vehiculo> findByDisponibleVenta(boolean disponible) {
        return vehiculos.stream()
                        .filter(v -> v.isDisponibleVenta() == disponible)
                        .collect(Collectors.toList());
    }


    @Override
    public void deleteById(int id) {
        boolean removed = vehiculos.removeIf(v -> v.getId() == id);
        if (removed) {
            saveData();
        }
    }
}
