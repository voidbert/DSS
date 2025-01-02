package dss.HorariosUI;

import java.util.Collection;

import dss.HorariosLN.IHorariosLN;
import dss.HorariosLN.LNException;
import dss.HorariosLN.SubSistemaHorarios.Horario;

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

    public void importarAlunosEInscricoes() throws LNException {
        String idCurso = this.getModelo().obterIdCursoDiretorAutenticado();
        this.getModelo().importarAlunos("/home/sushe/Área de Trabalho/uni/2_year/dss/DSS2425-Grupo-13/data/alunos.json", idCurso);
    }

    public void definirPreferenciasUC() {
    }

    public void adicionarAluno() {
    }

    public void gerarHorariosAutomaticamente() throws LNException {
        String idCurso = this.getModelo().obterIdCursoDiretorAutenticado();
        throw new LNException("Not implemented yet");

    }

    public Horario obterHorarioAluno(String numAluno) throws LNException {
        String idCurso = this.getModelo().obterIdCursoDiretorAutenticado();

        if (!this.getModelo().verificarSeAlunoInscritoEmCurso(numAluno, idCurso)) {
            throw new LNException("Aluno não se encontra inscrito num curso");
        }

        return this.getModelo().obterHorario(numAluno);
    }

    public void modificarHorario() {
    }

    public Collection<String> publicarHorarios() throws LNException {
        String idCurso = this.getModelo().obterIdCursoDiretorAutenticado();
        Collection<String> alunos = this.getModelo().obterAlunosDeCurso(idCurso);

        this.getModelo().gerarCredenciaisDeAlunos(alunos);
        Collection<String> falhas = this.getModelo().notificarAlunos(alunos);

        return falhas;
    }
}