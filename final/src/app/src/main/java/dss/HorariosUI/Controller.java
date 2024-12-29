package dss.HorariosUI;

import dss.HorariosLN.IHorariosLN;
import dss.HorariosLN.LNException;

public class Controller {
    IHorariosLN model;

    public Controller(IHorariosLN m) {
        this.model = m;
    }

    public void iniciarSessao(String email, String password) throws LNException {
        try {
            this.model.iniciarSessao(email, password);
        } catch (LNException e) {
            throw new LNException("Credenciais Inválidas");
        }
    }
}
