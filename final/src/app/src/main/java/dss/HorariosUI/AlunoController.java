package dss.HorariosUI;

import java.util.Map;
import java.util.Set;

import dss.HorariosLN.IHorariosLN;
import dss.HorariosLN.LNException;

public class AlunoController extends Controller {
    public AlunoController(IHorariosLN modelo) {
        super(modelo);
    }

    public Map<String, Set<String>> obterHorario() throws LNException {
        String res = this.getModelo().obterNumeroAlunoAutenticado();
        return this.getModelo().obterHorario(res);
    }
}
