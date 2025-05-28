package controller;

public class FormaDePagoFactoryImpl implements InterfazFormaDePagoFactory {
    @Override
    public IFormaDePago crearFormaDePago(TipoFormaPago tipo) throws ExcepcionValidacion { // Added throws
        if (tipo == null) {
            throw new ExcepcionValidacion("El tipo de forma de pago no puede ser nulo.");
        }
        switch (tipo) {
            case CONTADO:
                // Contado now has a default constructor. Specifics like moneda
                // would need to be set post-creation using a setter if not passed to factory.
                return new Contado(); 
            case TRANSFERENCIA:
                // Transferencia has a default constructor. Specifics like tipoDeTransferencia, banco
                // would need to be set post-creation using setters.
                return new Transferencia();
            case TARJETA_CREDITO:
                // TarjetaDeCredito has a default constructor. Specifics like cuotas, banco
                // would need to be set post-creation using setters.
                return new TarjetaDeCredito();
            default:
                throw new ExcepcionValidacion("Tipo de forma de pago no soportado: " + tipo);
        }
    }
}
