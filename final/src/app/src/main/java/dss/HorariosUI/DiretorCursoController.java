package dss.HorariosUI;

import java.util.Collection;

import dss.HorariosLN.IHorariosLN;
import dss.HorariosLN.LNException;

public class DiretorCursoController extends Controller {
    public DiretorCursoController(IHorariosLN modelo) {
        super(modelo);
    }

    public void reiniciarSemestre() throws LNException{
        String idCurso = this.getModelo().obterIdCursoDiretorAutenticado();
        Collection<String> alunos = this.getModelo().obterAlunosDeCurso(idCurso);

        this.getModelo().eliminarCredenciaisDeAlunos(alunos);
        this.getModelo().eliminarDadosCurso(idCurso);
    }

    public void verificarCursoTemUCs() throws LNException{
        String idCurso = this.getModelo().obterIdCursoDiretorAutenticado();
        if (this.getModelo().verificarCursoTemUCs(idCurso)) {
            throw new LNException();
        }
    }

    public void importarUnidadesCurricularesTurnos(String caminhoFicheiro) throws LNException{
        String idCurso = this.getModelo().obterIdCursoDiretorAutenticado();
        this.getModelo().importarUCs(caminhoFicheiro, idCurso);
    }

    public void importarAlunosEInscricoes() {
    }

    public void definirPreferenciasUC() {
    }

    public void adicionarAluno() {
    }

    public void gerarHorariosAutomaticamente() {
    }

    public void modificarHorario() {
    }

    public void publicarHorarios() {
    }
}