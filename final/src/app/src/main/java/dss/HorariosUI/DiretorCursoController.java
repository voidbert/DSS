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

    public void verificarCursoTemAlunos() throws LNException{
        String idCurso = this.getModelo().obterIdCursoDiretorAutenticado();
        if (this.getModelo().verificarCursoTemAlunos(idCurso)) {
            throw new LNException();
        }
    }

    public void importarAlunosEInscricoes(String caminhoFicheiro) throws LNException {
        String idCurso = this.getModelo().obterIdCursoDiretorAutenticado();
        this.getModelo().importarAlunos(caminhoFicheiro, idCurso);
    }

    public String obterListaUCs() throws LNException {
        String idCurso = this.getModelo().obterIdCursoDiretorAutenticado();
        Collection<String> ucs = this.getModelo().obterUCsDeCurso(idCurso);
        if(ucs.isEmpty()){
            throw new LNException();
        }
        StringBuilder ans = new StringBuilder();
        int x = 1;
        for (String uc : ucs) {
            ans.append(x).append(" - ").append(uc).append("\n");
        }
        return String.valueOf(ans);
    }

    public void verificarUCTemPreferencias(String nomeUC) throws LNException{
        String idCurso = this.getModelo().obterIdCursoDiretorAutenticado();
        //TODO metodo desapareceu?
//        if (this.getModelo().verificarUCTemPreferencias(idCurso, nomeUC)) {
            throw new LNException();
//        }
    }

    public void definirPreferenciasUC(String caminhoFicheiro, String nomeUC) throws LNException{
        String idCurso = this.getModelo().obterIdCursoDiretorAutenticado();
        //TODO metodo desapareceu?
//        this.getModelo().importarPreferenciasUC(caminhoFicheiro, idCurso, nomeUC);
    }

    public void verificarExistenciaAluno(String numeroAluno) throws LNException{
        if(this.getModelo().verificarExistenciaAluno(numeroAluno)){
            throw new LNException();
        }
    }

    public void adicionarAluno(String numeroAluno, Set<String> nomeUCs) throws LNException {
        String idCurso = this.getModelo().obterIdCursoDiretorAutenticado();
        try {
            this.getModelo().registarAluno(idCurso, numeroAluno);
        } catch (LNException e) {
            throw new LNException(e.getMessage());
        }

        try {
            this.getModelo().registarUCsDeAluno(numeroAluno, nomeUCs);
        } catch (LNException e) {
            throw new LNException(e.getMessage());
        }
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