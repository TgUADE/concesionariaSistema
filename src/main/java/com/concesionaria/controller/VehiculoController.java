package com.concesionaria.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/vehiculos")
@CrossOrigin(origins = "*")
public class VehiculoController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllVehiculos() {
        try {
            List<Map<String, Object>> vehiculos = new ArrayList<>();
            
            // Crear algunos vehículos de ejemplo
            Map<String, Object> auto = new HashMap<>();
            auto.put("id", 1);
            auto.put("marca", "Toyota");
            auto.put("modelo", "Corolla");
            auto.put("tipo", "AUTO");
            auto.put("año", 2023);
            auto.put("precio", 25000.0);
            auto.put("disponible", true);
            vehiculos.add(auto);
            
            Map<String, Object> camioneta = new HashMap<>();
            camioneta.put("id", 2);
            camioneta.put("marca", "Ford");
            camioneta.put("modelo", "F-150");
            camioneta.put("tipo", "CAMIONETA");
            camioneta.put("año", 2023);
            camioneta.put("precio", 35000.0);
            camioneta.put("disponible", true);
            vehiculos.add(camioneta);
            
            Map<String, Object> moto = new HashMap<>();
            moto.put("id", 3);
            moto.put("marca", "Honda");
            moto.put("modelo", "CBR");
            moto.put("tipo", "MOTO");
            moto.put("año", 2023);
            moto.put("precio", 15000.0);
            moto.put("disponible", true);
            vehiculos.add(moto);
            
            Map<String, Object> response = new HashMap<>();
            response.put("vehiculos", vehiculos);
            response.put("total", vehiculos.size());
            response.put("status", "success");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            errorResponse.put("status", "error");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<Map<String, Object>> getVehiculosByTipo(@PathVariable String tipo) {
        try {
            List<Map<String, Object>> vehiculos = new ArrayList<>();
            
            // Filtrar por tipo
            switch (tipo.toUpperCase()) {
                case "AUTO":
                    Map<String, Object> auto = new HashMap<>();
                    auto.put("id", 1);
                    auto.put("marca", "Toyota");
                    auto.put("modelo", "Corolla");
                    auto.put("tipo", "AUTO");
                    auto.put("precio", 25000.0);
                    vehiculos.add(auto);
                    break;
                case "CAMIONETA":
                    Map<String, Object> camioneta = new HashMap<>();
                    camioneta.put("id", 2);
                    camioneta.put("marca", "Ford");
                    camioneta.put("modelo", "F-150");
                    camioneta.put("tipo", "CAMIONETA");
                    camioneta.put("precio", 35000.0);
                    vehiculos.add(camioneta);
                    break;
                case "MOTO":
                    Map<String, Object> moto = new HashMap<>();
                    moto.put("id", 3);
                    moto.put("marca", "Honda");
                    moto.put("modelo", "CBR");
                    moto.put("tipo", "MOTO");
                    moto.put("precio", 15000.0);
                    vehiculos.add(moto);
                    break;
                default:
                    // Retornar lista vacía para tipos no reconocidos
                    break;
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("vehiculos", vehiculos);
            response.put("tipo", tipo.toUpperCase());
            response.put("total", vehiculos.size());
            response.put("status", "success");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            errorResponse.put("status", "error");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crearVehiculo(@RequestBody Map<String, Object> vehiculoData) {
        try {
            Map<String, Object> vehiculo = new HashMap<>();
            vehiculo.put("id", System.currentTimeMillis());
            vehiculo.put("marca", vehiculoData.get("marca"));
            vehiculo.put("modelo", vehiculoData.get("modelo"));
            vehiculo.put("tipo", vehiculoData.get("tipo"));
            vehiculo.put("año", vehiculoData.get("año"));
            vehiculo.put("precio", vehiculoData.get("precio"));
            vehiculo.put("disponible", true);
            vehiculo.put("fechaCreacion", new Date());
            
            Map<String, Object> response = new HashMap<>();
            response.put("vehiculo", vehiculo);
            response.put("message", "Vehículo creado exitosamente");
            response.put("status", "success");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            errorResponse.put("status", "error");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
