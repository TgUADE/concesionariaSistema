package com.grupo9.sistemaConcesionaria.controller;

import com.grupo9.sistemaConcesionaria.model.*;
import com.grupo9.sistemaConcesionaria.service.GestorDeUsuarios;
import com.grupo9.sistemaConcesionaria.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador REST para operaciones de usuarios del sistema
 * Maneja Clientes, Administradores y Vendedores
 */
@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final GestorDeUsuarios gestorUsuarios = GestorDeUsuarios.getInstancia();

    // === CLIENTES ===

    @PostMapping("/clientes")
    public ResponseEntity<?> registrarCliente(@Valid @RequestBody Cliente cliente) {
        try {
            gestorUsuarios.registrarCliente(cliente);
            return ResponseEntity.status(HttpStatus.CREATED).body(cliente);
        } catch (DuplicateClienteException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(createErrorResponse("Error: Cliente duplicado", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Error: Datos inválidos", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error interno", e.getMessage()));
        }
    }

    @GetMapping("/clientes/{documento}")
    public ResponseEntity<?> obtenerCliente(@PathVariable String documento) {
        try {
            Cliente cliente = gestorUsuarios.getCliente(documento);
            return ResponseEntity.ok(cliente);
        } catch (ClienteNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Cliente no encontrado", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error interno", e.getMessage()));
        }
    }

    @GetMapping("/clientes")
    public ResponseEntity<Collection<Cliente>> obtenerTodosLosClientes() {
        Collection<Cliente> clientes = gestorUsuarios.getTodosLosClientes();
        return ResponseEntity.ok(clientes);
    }

    @PutMapping("/clientes")
    public ResponseEntity<?> actualizarCliente(@Valid @RequestBody Cliente cliente) {
        try {
            gestorUsuarios.actualizarCliente(cliente);
            return ResponseEntity.ok(cliente);
        } catch (ClienteNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Cliente no encontrado", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error interno", e.getMessage()));
        }
    }

    @DeleteMapping("/clientes/{documento}")
    public ResponseEntity<?> eliminarCliente(@PathVariable String documento) {
        try {
            gestorUsuarios.eliminarCliente(documento);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Cliente eliminado exitosamente");
            response.put("documento", documento);
            return ResponseEntity.ok(response);
        } catch (ClienteNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Cliente no encontrado", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error interno", e.getMessage()));
        }
    }

    // === USUARIOS DEL SISTEMA ===

    @PostMapping("/sistema")
    public ResponseEntity<?> registrarUsuarioSistema(@Valid @RequestBody Usuario usuario) {
        try {
            gestorUsuarios.registrarUsuarioSistema(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
        } catch (DuplicateUsuarioException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(createErrorResponse("Error: Usuario duplicado", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error interno", e.getMessage()));
        }
    }

    @PostMapping("/administradores")
    public ResponseEntity<?> registrarAdministrador(@Valid @RequestBody Administrador administrador) {
        try {
            gestorUsuarios.registrarUsuarioSistema(administrador);
            return ResponseEntity.status(HttpStatus.CREATED).body(administrador);
        } catch (DuplicateUsuarioException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(createErrorResponse("Error: Administrador duplicado", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error interno", e.getMessage()));
        }
    }

    @PostMapping("/vendedores")
    public ResponseEntity<?> registrarVendedor(@Valid @RequestBody Vendedor vendedor) {
        try {
            gestorUsuarios.registrarUsuarioSistema(vendedor);
            return ResponseEntity.status(HttpStatus.CREATED).body(vendedor);
        } catch (DuplicateUsuarioException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(createErrorResponse("Error: Vendedor duplicado", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error interno", e.getMessage()));
        }
    }

    @GetMapping("/sistema/{mail}")
    public ResponseEntity<?> obtenerUsuarioSistema(@PathVariable String mail) {
        try {
            Usuario usuario = gestorUsuarios.getUsuarioSistema(mail);
            return ResponseEntity.ok(usuario);
        } catch (UsuarioNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Usuario no encontrado", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error interno", e.getMessage()));
        }
    }

    @GetMapping("/sistema")
    public ResponseEntity<Collection<Usuario>> obtenerTodosLosUsuariosSistema() {
        Collection<Usuario> usuarios = gestorUsuarios.getTodosLosUsuariosSistema();
        return ResponseEntity.ok(usuarios);
    }

    @PutMapping("/sistema")
    public ResponseEntity<?> actualizarUsuarioSistema(@Valid @RequestBody Usuario usuario) {
        try {
            gestorUsuarios.actualizarUsuarioSistema(usuario);
            return ResponseEntity.ok(usuario);
        } catch (UsuarioNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Usuario no encontrado", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error interno", e.getMessage()));
        }
    }

    @DeleteMapping("/sistema/{mail}")
    public ResponseEntity<?> eliminarUsuarioSistema(@PathVariable String mail) {
        try {
            gestorUsuarios.eliminarUsuarioSistema(mail);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Usuario eliminado exitosamente");
            response.put("mail", mail);
            return ResponseEntity.ok(response);
        } catch (UsuarioNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Usuario no encontrado", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error interno", e.getMessage()));
        }
    }

    // === ENDPOINTS DE UTILIDAD ===

    @GetMapping("/estadisticas")
    public ResponseEntity<Map<String, String>> obtenerEstadisticas() {
        Map<String, String> estadisticas = new HashMap<>();
        estadisticas.put("informacion", gestorUsuarios.getEstadisticas());
        return ResponseEntity.ok(estadisticas);
    }

    @PostMapping("/limpiar")
    public ResponseEntity<Map<String, String>> limpiarDatos() {
        gestorUsuarios.limpiarDatos();
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Datos limpiados exitosamente");
        return ResponseEntity.ok(response);
    }

    // === MÉTODOS AUXILIARES ===

    private Map<String, String> createErrorResponse(String titulo, String detalle) {
        Map<String, String> error = new HashMap<>();
        error.put("error", titulo);
        error.put("detalle", detalle);
        return error;
    }
} 