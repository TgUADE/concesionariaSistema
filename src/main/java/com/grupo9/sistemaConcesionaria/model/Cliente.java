package com.grupo9.sistemaConcesionaria.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Entidad Cliente - Hereda de Usuario
 * Representa a los clientes de la concesionaria que también son usuarios del sistema
 * Implementa ICliente para compatibilidad con el sistema existente
 */
@Entity
@Table(name = "clientes")
@PrimaryKeyJoinColumn(name = "usuario_id")
public class Cliente extends Usuario implements ICliente {

    @NotBlank(message = "El documento es obligatorio")
    @Column(unique = true, nullable = false, length = 20)
    @JsonProperty("documento")
    private String documento;

    @Column(length = 200)
    @JsonProperty("direccion")
    private String direccion;

    // Constructors
    public Cliente() {
        super();
        setRol(RolUsuario.CLIENTE);
    }

    public Cliente(String nombre, String apellido, String mail, String documento, String telefono) {
        super(nombre, apellido, mail, RolUsuario.CLIENTE);
        this.documento = documento;
        setTelefono(telefono);
    }

    // Business methods
    public void verEstadoPedidos() {
        System.out.println("Cliente " + getNombre() + ": Consultando estado de pedidos...");
    }

    public void consultarCatalogoVehiculos() {
        System.out.println("Cliente " + getNombre() + ": Consultando catálogo de vehículos...");
    }

    // ICliente implementation - delegamos a los métodos heredados de Usuario
    @Override
    public int getIdCliente() {
        return getIdUsuario() != null ? getIdUsuario().intValue() : 0;
    }

    @Override
    public String getCorreoElectronico() {
        return getMail();
    }

    // Getters and Setters específicos de Cliente
    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    // Métodos de conveniencia para compatibilidad con JSON y APIs
    public Long getId() {
        return getIdUsuario();
    }

    public void setId(Long id) {
        setIdUsuario(id);
    }

    public void setCorreoElectronico(String correoElectronico) {
        setMail(correoElectronico);
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "idUsuario=" + getIdUsuario() +
                ", nombre='" + getNombre() + '\'' +
                ", apellido='" + getApellido() + '\'' +
                ", documento='" + documento + '\'' +
                ", mail='" + getMail() + '\'' +
                ", telefono='" + getTelefono() + '\'' +
                ", direccion='" + direccion + '\'' +
                '}';
    }
} 