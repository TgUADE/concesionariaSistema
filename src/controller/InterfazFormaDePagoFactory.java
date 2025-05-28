package controller;

public interface InterfazFormaDePagoFactory {
    IFormaDePago crearFormaDePago(TipoFormaPago tipo);
    // Optionally, the factory method could take parameters if needed for constructing specific FormaDePago types,
    // e.g., crearFormaDePago(TipoFormaPago tipo, Map<String, Object> params)
    // For now, a simple type-based creation is fine.
}
