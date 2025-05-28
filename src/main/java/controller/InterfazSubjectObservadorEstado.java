package controller;

public interface InterfazSubjectObservadorEstado {
    void adjuntar(Observador observador);
    void quitar(Observador observador);
    void notificarObservadores(Pedido pedido, String estadoInfo, String areaResponsable);
}
