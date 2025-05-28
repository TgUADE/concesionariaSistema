package controller.repository;

import controller.Cliente;
import controller.ExcepcionDuplicado;
import java.util.List;
import java.util.Optional;

public interface InterfazClienteRepository {
    Cliente save(Cliente cliente) throws ExcepcionDuplicado;
    Optional<Cliente> findById(int id);
    Optional<Cliente> findByEmail(String email);
    List<Cliente> findAll();
    void deleteById(int id);
    List<Cliente> findByNombreContaining(String nombre);
    Optional<Cliente> findByDocumentoAndCorreo(String documento, String correo);
}
