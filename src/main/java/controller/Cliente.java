package controller;

// No specific imports needed for this class beyond standard Java types

public class Cliente implements ICliente { // Retaining ICliente for compatibility with Pedido
    private int id;
    private String nombre;
    private String apellido;
    private String documento;
    private String correo;
    private String telefono;

    public Cliente(int id, String nombre, String apellido, String documento, String correo, String telefono) throws ExcepcionValidacion { // Added throws
        this.id = id;
        // Basic validation example:
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ExcepcionValidacion("El nombre del cliente no puede ser nulo o vacío.");
        }
        this.nombre = nombre;
        this.apellido = apellido;
        this.documento = documento; // Could add validation for format
        this.correo = correo;       // Could add validation for email format
        this.telefono = telefono;
    }

    // Getters
    public int getId() {
        return id;
    }

    @Override // From ICliente
    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getDocumento() {
        return documento;
    }

    public String getCorreo() {
        return correo;
    }

    public String getTelefono() {
        return telefono;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setNombre(String nombre) throws ExcepcionValidacion { // Added throws
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ExcepcionValidacion("El nombre del cliente no puede ser nulo o vacío.");
        }
        this.nombre = nombre;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
