package controller.repository;

import com.google.gson.reflect.TypeToken;
import controller.Pedido;
import controller.ExcepcionDuplicado;
import controller.util.GsonUtil;
import controller.Cliente; // Pedido now uses concrete Cliente
import controller.Vehiculo; // Pedido now uses concrete Vehiculo

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class GsonPedidoRepositoryImpl implements InterfazPedidoRepository {
    private final String filePath;
    private List<Pedido> pedidos;
    private AtomicInteger maxNumeroPedido; // Pedido uses numeroPedido as its primary ID

    public GsonPedidoRepositoryImpl(String filePath) {
        this.filePath = filePath;
        this.pedidos = new ArrayList<>();
        this.maxNumeroPedido = new AtomicInteger(0);
        loadData();
    }

    private void loadData() {
        try (FileReader reader = new FileReader(filePath)) {
            Type listType = new TypeToken<ArrayList<Pedido>>() {}.getType();
            List<Pedido> loadedPedidos = GsonUtil.getGson().fromJson(reader, listType);
            if (loadedPedidos != null) {
                this.pedidos.addAll(loadedPedidos);
                for (Pedido pedido : pedidos) {
                    if (pedido.getNumeroPedido() > maxNumeroPedido.get()) {
                        maxNumeroPedido.set(pedido.getNumeroPedido());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Advertencia: No se encontró el archivo de pedidos '" + filePath + "'. Se iniciará con una lista vacía.");
            this.pedidos = new ArrayList<>();
        } catch (com.google.gson.JsonSyntaxException e) {
            System.err.println("Error: El archivo de pedidos '" + filePath + "' contiene JSON malformado. Se iniciará con una lista vacía.");
            e.printStackTrace();
            this.pedidos = new ArrayList<>();
        }
    }

    private void saveData() {
        try (FileWriter writer = new FileWriter(filePath)) {
            GsonUtil.getGson().toJson(pedidos, writer);
        } catch (IOException e) {
            System.err.println("Error al guardar datos de pedidos en '" + filePath + "': " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Pedido save(Pedido pedido) throws ExcepcionDuplicado {
        if (pedido == null) {
            throw new IllegalArgumentException("El pedido no puede ser nulo.");
        }

        // Duplicate check: numeroPedido (if set and not 0)
        if (pedido.getNumeroPedido() != 0) {
            Optional<Pedido> existing = findByNumeroPedido(pedido.getNumeroPedido());
            if (existing.isPresent()) { // It's an update
                pedidos.remove(existing.get());
            } else {
                // If numeroPedido is set but not found, update maxNumeroPedido if necessary
                if (pedido.getNumeroPedido() > maxNumeroPedido.get()) {
                    maxNumeroPedido.set(pedido.getNumeroPedido());
                }
            }
        } else { // New pedido, generate numeroPedido
            pedido.setNumeroPedido(maxNumeroPedido.incrementAndGet());
        }
        
        // More complex duplicate check (cliente + vehiculo + fecha) - this is a simplified version
        // A robust check might involve hashing or comparing specific fields.
        // For this example, we'll rely on numeroPedido for primary identification after it's set.
        // If a more specific business rule for "duplicate" Pedido exists before assigning an ID,
        // it would need to be implemented here. E.g.:
        // boolean isDuplicateBusinessLogic = pedidos.stream().anyMatch(p -> 
        //     p.getCliente().getId() == pedido.getCliente().getId() &&
        //     p.getVehiculo().getId() == pedido.getVehiculo().getId() &&
        //     Math.abs(p.getFechaCreacion().getTime() - pedido.getFechaCreacion().getTime()) < 1000*60*5 // 5 min tolerance
        // );
        // if (isDuplicateBusinessLogic && existingByNumeroPedido.isEmpty()) { // only if it's a new one by numeroPedido
        //     throw new ExcepcionDuplicado("Pedido duplicado detectado por lógica de negocio (cliente, vehiculo, fecha).");
        // }

        pedidos.add(pedido);
        saveData();
        return pedido;
    }

    @Override
    public Optional<Pedido> findByNumeroPedido(int numeroPedido) {
        return pedidos.stream()
                      .filter(p -> p.getNumeroPedido() == numeroPedido)
                      .findFirst();
    }

    @Override
    public List<Pedido> findPedidosByFechaBetween(Date fechaDesde, Date fechaHasta) {
        return pedidos.stream()
                      .filter(p -> {
                          boolean afterDesde = fechaDesde == null || !p.getFechaCreacion().before(fechaDesde);
                          boolean beforeHasta = fechaHasta == null || !p.getFechaCreacion().after(fechaHasta);
                          return afterDesde && beforeHasta;
                      })
                      .collect(Collectors.toList());
    }

    @Override
    public List<Pedido> findPedidosByEstado(String estadoClassName) {
        if (estadoClassName == null || estadoClassName.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return pedidos.stream()
                      .filter(p -> p.getEstadoActual() != null && 
                                   p.getEstadoActual().getClass().getSimpleName().equalsIgnoreCase(estadoClassName))
                      .collect(Collectors.toList());
    }

    @Override
    public List<Pedido> findPedidosByClienteId(int clienteId) {
        return pedidos.stream()
                      .filter(p -> p.getCliente() != null && p.getCliente().getId() == clienteId)
                      .collect(Collectors.toList());
    }

    @Override
    public List<Pedido> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(pedidos));
    }

    @Override
    public void deleteByNumeroPedido(int numeroPedido) {
        boolean removed = pedidos.removeIf(p -> p.getNumeroPedido() == numeroPedido);
        if (removed) {
            saveData();
        }
    }
}
