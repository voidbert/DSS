package dss.HorariosUI;

import dss.HorariosLN.IHorariosLN;
import dss.HorariosLN.LNException;

public class IniciarSessaoController extends Controller {
    public IniciarSessaoController(IHorariosLN modelo) {
        super(modelo);
    }

    public void iniciarSessao(String email, String password) throws LNException {
        this.getModelo().iniciarSessao(email, password);
    }

    public void terminarSessao() throws LNException {
        this.terminarSessao();
    }
}
