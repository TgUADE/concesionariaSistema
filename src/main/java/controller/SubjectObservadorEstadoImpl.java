package controller;

import java.util.ArrayList;
import java.util.List;

public class SubjectObservadorEstadoImpl implements InterfazSubjectObservadorEstado {
    private List<Observador> observadores = new ArrayList<>();

    @Override
    public void adjuntar(Observador observador) {
        if (observador != null && !observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    @Override
    public void quitar(Observador observador) {
        observadores.remove(observador);
    }

    @Override
    public void notificarObservadores(Pedido pedido, String estadoInfo, String areaResponsable) {
        for (Observador observador : new ArrayList<>(observadores)) { // Iterate on a copy to allow modification during notification
            observador.actualizar(pedido, estadoInfo, areaResponsable);
        }
    }
}
