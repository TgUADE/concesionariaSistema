package controller;

public class ConfiguracionConcesionaria {
    // Private static final instance - Eager initialization
    private static final ConfiguracionConcesionaria instancia = new ConfiguracionConcesionaria();

    // Attributes
    private final String nombreConcesionaria;
    private final String cuitConcesionaria;
    private final String reportePath;
    // Potentially other global configurations like default currency, email server settings, etc.

    // Private constructor to prevent instantiation from outside
    private ConfiguracionConcesionaria() {
        // Initialize with default values
        // In a real application, these might come from a properties file, environment variables, etc.
        this.nombreConcesionaria = "Concesionaria XYZ";
        this.cuitConcesionaria = "30-12345678-9";
        this.reportePath = "/mnt/data/reportes/"; // Example path
    }

    // Public static method to get the instance
    public static ConfiguracionConcesionaria getInstance() {
        return instancia;
    }

    // Public getter methods for the attributes
    public String getNombreConcesionaria() {
        return nombreConcesionaria;
    }

    public String getCuitConcesionaria() {
        return cuitConcesionaria;
    }

    public String getReportePath() {
        return reportePath;
    }

    // Example of how it might be used:
    // public static void main(String[] args) {
    //     ConfiguracionConcesionaria config = ConfiguracionConcesionaria.getInstance();
    //     System.out.println("Nombre: " + config.getNombreConcesionaria());
    //     System.out.println("CUIT: " + config.getCuitConcesionaria());
    //     System.out.println("Path de Reportes: " + config.getReportePath());

    //     ConfiguracionConcesionaria config2 = ConfiguracionConcesionaria.getInstance();
    //     // Check if it's the same instance
    //     System.out.println("Misma instancia? " + (config == config2));
    // }
}
