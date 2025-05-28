package controller.repository;

import com.google.gson.reflect.TypeToken;
import controller.Cliente;
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
import java.util.stream.Collectors;
import java.util.concurrent.atomic.AtomicInteger; // For ID generation
import controller.repository.InterfazClienteRepository;

public class GsonClienteRepositoryImpl implements InterfazClienteRepository {
    private final String filePath;
    private List<Cliente> clientes;
    private AtomicInteger maxId;

    public GsonClienteRepositoryImpl(String filePath) {
        this.filePath = filePath;
        this.clientes = new ArrayList<>();
        this.maxId = new AtomicInteger(0);
        loadData();
    }

    private void loadData() {
        try (FileReader reader = new FileReader(filePath)) {
            Type listType = new TypeToken<ArrayList<Cliente>>() {}.getType();
            List<Cliente> loadedClientes = GsonUtil.getGson().fromJson(reader, listType);
            if (loadedClientes != null) {
                this.clientes.addAll(loadedClientes);
                // Initialize maxId based on loaded data
                for (Cliente cliente : clientes) {
                    if (cliente.getId() > maxId.get()) {
                        maxId.set(cliente.getId());
                    }
                }
            }
        } catch (IOException e) { // FileNotFoundException is a subclass of IOException
            System.err.println("Advertencia: No se encontró el archivo de clientes '" + filePath + "'. Se iniciará con una lista vacía.");
            // File might not exist on first run, which is fine.
            this.clientes = new ArrayList<>();
        } catch (com.google.gson.JsonSyntaxException e) {
            System.err.println("Error: El archivo de clientes '" + filePath + "' contiene JSON malformado. Se iniciará con una lista vacía.");
            e.printStackTrace();
            this.clientes = new ArrayList<>();
        }
    }

    private void saveData() {
        try (FileWriter writer = new FileWriter(filePath)) {
            GsonUtil.getGson().toJson(clientes, writer);
        } catch (IOException e) {
            System.err.println("Error al guardar datos de clientes en '" + filePath + "': " + e.getMessage());
            e.printStackTrace();
            // Consider re-throwing as a runtime exception or a custom persistence exception if critical
        }
    }

    @Override
    public Cliente save(Cliente cliente) throws ExcepcionDuplicado {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no puede ser nulo.");
        }

        // Check for duplicates based on documento and correo
        Optional<Cliente> existingByDocAndMail = findByDocumentoAndCorreo(cliente.getDocumento(), cliente.getCorreo());
        if (existingByDocAndMail.isPresent() && existingByDocAndMail.get().getId() != cliente.getId()) {
             throw new ExcepcionDuplicado("Ya existe un cliente con el mismo documento y correo: Doc=" + cliente.getDocumento() + ", Correo=" + cliente.getCorreo());
        }
        
        // Check for duplicates based on ID if it's not a new client
        if (cliente.getId() != 0) {
            Optional<Cliente> existingById = findById(cliente.getId());
            if (existingById.isPresent()) { // It's an update
                clientes.remove(existingById.get());
            } else {
                // If ID is set but not found, it's an issue, or we decide to treat it as new if ID logic allows.
                // For now, if ID is non-zero and not found, we proceed to add it with that ID,
                // potentially overwriting maxId logic if the given ID is higher.
                // This assumes IDs can be set externally before saving.
                if (cliente.getId() > maxId.get()) {
                    maxId.set(cliente.getId());
                }
            }
        } else { // New client, generate ID
            cliente.setId(maxId.incrementAndGet());
        }
        
        clientes.add(cliente);
        saveData();
        return cliente;
    }

    @Override
    public Optional<Cliente> findById(int id) {
        return clientes.stream()
                       .filter(c -> c.getId() == id)
                       .findFirst();
    }

    @Override
    public Optional<Cliente> findByDocumentoAndCorreo(String documento, String correo) {
        if (documento == null || correo == null) return Optional.empty();
        return clientes.stream()
                       .filter(c -> documento.equals(c.getDocumento()) && correo.equals(c.getCorreo()))
                       .findFirst();
    }

    @Override
    public List<Cliente> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(clientes)); // Return a copy
    }

    @Override
    public void deleteById(int id) {
        boolean removed = clientes.removeIf(c -> c.getId() == id);
        if (removed) {
            saveData();
        }
        // Optionally, throw an exception if client with id not found
    }

    @Override
    public List<Cliente> findByNombreContaining(String nombre) {
        return clientes.stream()
                .filter(cliente -> cliente.getNombre() != null && 
                                 cliente.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<Cliente> findByEmail(String email) {
        return clientes.stream()
                .filter(cliente -> cliente.getCorreo() != null && 
                                 cliente.getCorreo().equalsIgnoreCase(email))
                .findFirst();
    }
}
