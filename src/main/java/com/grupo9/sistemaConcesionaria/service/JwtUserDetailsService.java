package com.grupo9.sistemaConcesionaria.service;

import com.grupo9.sistemaConcesionaria.model.Usuario;
import com.grupo9.sistemaConcesionaria.model.Cliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * Servicio para cargar detalles de usuario para autenticación JWT
 * Implementa UserDetailsService de Spring Security
 */
@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private GestorDeUsuarios gestorUsuarios;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        
        // Primero buscar en usuarios del sistema (Administrador, Vendedor)
        try {
            Usuario usuario = gestorUsuarios.getUsuarioSistema(email);
            return User.builder()
                    .username(usuario.getMail())
                    .password(getDefaultPassword()) // En producción usar hash real
                    .authorities(Arrays.asList(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name())))
                    .build();
        } catch (Exception e) {
            // Si no es usuario del sistema, buscar en clientes
        }

        // Buscar en clientes (Comprador)
        try {
            Cliente cliente = gestorUsuarios.getCliente(email); // Usar email como documento por simplicidad
            return User.builder()
                    .username(cliente.getMail())
                    .password(getDefaultPassword()) // En producción usar hash real
                    .authorities(Arrays.asList(new SimpleGrantedAuthority("ROLE_COMPRADOR")))
                    .build();
        } catch (Exception e) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + email);
        }
    }

    /**
     * Método temporal para obtener contraseña por defecto
     * En producción, las contraseñas deben estar hasheadas en la base de datos
     */
    private String getDefaultPassword() {
        // Contraseña plana para demo: "password123"
        return "password123";
    }

    /**
     * Obtiene UserDetails por email con manejo de roles específico
     */
    public UserDetails getUserDetailsByEmail(String email) throws UsernameNotFoundException {
        return loadUserByUsername(email);
    }

    /**
     * Verifica si un usuario existe en el sistema
     */
    public boolean userExists(String email) {
        try {
            loadUserByUsername(email);
            return true;
        } catch (UsernameNotFoundException e) {
            return false;
        }
    }
} 