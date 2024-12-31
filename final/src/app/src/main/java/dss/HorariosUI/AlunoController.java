package dss.HorariosUI;

import dss.HorariosLN.IHorariosLN;
import dss.HorariosLN.LNException;
import dss.HorariosLN.SubSistemaHorarios.Horario;

public class AlunoController extends Controller {
    public AlunoController(IHorariosLN modelo) {
        super(modelo);
    }

    public Horario obterHorario() throws LNException {
        String res = this.getModelo().obterNumeroAlunoAutenticado();
        return this.getModelo().obterHorario(res);
    }
}
