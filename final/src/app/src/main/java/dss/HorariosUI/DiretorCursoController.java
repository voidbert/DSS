package dss.HorariosUI;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import dss.HorariosLN.IHorariosLN;
import dss.HorariosLN.LNException;
import dss.HorariosLN.SubSistemaHorarios.Horario;
import dss.HorariosLN.SubSistemaHorarios.Sobreposicao;

public class DiretorCursoController extends Controller {
    public DiretorCursoController(IHorariosLN modelo) {
        super(modelo);
    }

    public void reiniciarSemestre() throws LNException{
        String idCurso = this.getModelo().obterIdCursoDiretorAutenticado();
        Set<String> alunos = this.getModelo().obterAlunosDeCurso(idCurso);

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

    public Collection<Sobreposicao> gerarHorariosAutomaticamente() throws LNException {
        String idCurso = this.getModelo().obterIdCursoDiretorAutenticado();
        this.getModelo().gerarHorarios(idCurso);
        return this.getModelo().procurarSobreposicoes(idCurso);
    }

    public void armazenarHorarios() throws LNException {
        String idCurso = this.getModelo().obterIdCursoDiretorAutenticado();
        Set<String> alunos = this.getModelo().obterAlunosDeCurso(idCurso);

        for (String aluno : alunos) {
            Map<String, Set<String>> horario = this.getModelo().obterHorario(aluno);
            this.getModelo().armazenarHorario(idCurso, aluno, horario);
        }
    }

    public Map<String, Set<String>> obterHorarioAluno(String numAluno) throws LNException {
        String idCurso = this.getModelo().obterIdCursoDiretorAutenticado();

        if (this.getModelo().verificarSeAlunoInscritoEmCurso(numAluno, idCurso)) {
            /* TODO - Atualizar para UIException */
            throw new LNException("Aluno não se encontra inscrito num curso");
        }

        return this.getModelo().obterHorario(numAluno);
    }

    public void atualizarHorario(String numAluno, Map<String, Set<String>> horario) throws LNException {
        if (!this.getModelo().validarHorario(numAluno, horario)) {
            /* TODO - Atualizar para UIException */
            throw new LNException("Horario modificado não é válido");
        }
        String idCurso = this.getModelo().obterIdCursoDiretorAutenticado();
        this.getModelo().armazenarHorario(idCurso, numAluno, horario);
    }

    public Collection<String> publicarHorarios() throws LNException {
        String idCurso = this.getModelo().obterIdCursoDiretorAutenticado();
        Set<String> alunos = this.getModelo().obterAlunosDeCurso(idCurso);

        this.getModelo().gerarCredenciaisDeAlunos(alunos);
        Collection<String> falhas = this.getModelo().notificarAlunos(alunos);

        return falhas;
    }
}