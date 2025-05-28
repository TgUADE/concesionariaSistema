package com.concesionaria;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.concesionaria", "controller"})
public class ConcesionariaApplication {

	public static void main(String[] args) {
		System.out.println("🚗 Iniciando Sistema de Concesionaria Spring Boot...");
		SpringApplication.run(ConcesionariaApplication.class, args);
		System.out.println("✅ Sistema de Concesionaria iniciado correctamente!");
	}
}
