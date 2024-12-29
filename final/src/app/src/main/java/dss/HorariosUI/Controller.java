package dss.HorariosUI;

import dss.HorariosLN.IHorariosLN;
import dss.HorariosLN.LNException;
import dss.HorariosLN.SubSistemaUtilizadores.UtilizadoresException;

public class Controller {
    IHorariosLN model;

    public Controller(IHorariosLN m) {
        this.model = m;
    }

    public void iniciarSessao(String email, String password) throws LNException {
        this.model.iniciarSessao(email, password);
    }
}
