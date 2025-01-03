/*
 * Copyright 2024 Ana Cerqueira, Humberto Gomes, João Torres, José Lopes, José Matos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dss.HorariosUI;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import dss.HorariosLN.IHorariosLN;
import dss.HorariosLN.LNException;
import dss.HorariosLN.SubSistemaHorarios.Sobreposicao;

public class DiretorCursoControlador extends Controlador {
    public DiretorCursoControlador(IHorariosLN modelo) {
        super(modelo);
    }

    public void reiniciarSemestre() throws LNException{
        String idCurso = this.obterModelo().obterIdCursoDiretorAutenticado();
        Set<String> alunos = this.obterModelo().obterAlunosDeCurso(idCurso);

        this.obterModelo().eliminarCredenciaisDeAlunos(alunos);
        this.obterModelo().eliminarDadosCurso(idCurso);
    }

    public void verificarCursoTemUCs() throws LNException{
        String idCurso = this.obterModelo().obterIdCursoDiretorAutenticado();
        if (this.obterModelo().verificarCursoTemUCs(idCurso)) {
            throw new UIException("Curso não tem unidades curriculares.");
        }
    }

    public void importarUnidadesCurricularesTurnos(String caminhoFicheiro) throws LNException{
        String idCurso = this.obterModelo().obterIdCursoDiretorAutenticado();
        this.obterModelo().importarUCs(caminhoFicheiro, idCurso);
    }

    public void verificarCursoTemAlunos() throws LNException{
        String idCurso = this.obterModelo().obterIdCursoDiretorAutenticado();
        if (this.obterModelo().verificarCursoTemAlunos(idCurso)) {
            throw new UIException("Curso não tem alunos.");
        }
    }

    public void importarAlunosEInscricoes(String caminhoFicheiro) throws LNException {
        String idCurso = this.obterModelo().obterIdCursoDiretorAutenticado();
        this.obterModelo().importarAlunos(caminhoFicheiro, idCurso);
    }

    public Set<String> obterListaUCs() throws LNException {
        String idCurso = this.obterModelo().obterIdCursoDiretorAutenticado();
        return this.obterModelo().obterUCsDeCurso(idCurso);
    }

    public void verificarExistenciaAluno(String numeroAluno) throws LNException{
        if(this.obterModelo().verificarExistenciaAluno(numeroAluno)){
            throw new UIException("Aluno com o mesmo número já existe.");
        }
    }

    public void adicionarAluno(String numeroAluno, Set<String> nomeUCs) throws LNException {
        String idCurso = this.obterModelo().obterIdCursoDiretorAutenticado();
        this.obterModelo().registarAluno(idCurso, numeroAluno);
        this.obterModelo().registarUCsDeAluno(numeroAluno, nomeUCs);
    }
    public Collection<Sobreposicao> gerarHorariosAutomaticamente() throws LNException {
        String idCurso = this.obterModelo().obterIdCursoDiretorAutenticado();
        this.obterModelo().gerarHorarios(idCurso);
        return this.obterModelo().procurarSobreposicoes(idCurso);
    }

    public void armazenarHorarios() throws LNException {
        String idCurso = this.obterModelo().obterIdCursoDiretorAutenticado();
        Set<String> alunos = this.obterModelo().obterAlunosDeCurso(idCurso);

        for (String aluno : alunos) {
            Map<String, Set<String>> horario = this.obterModelo().obterHorario(aluno);
            this.obterModelo().armazenarHorario(idCurso, aluno, horario);
        }
    }

    public Map<String, Set<String>> obterHorarioAluno(String numAluno) throws LNException {
        String idCurso = this.obterModelo().obterIdCursoDiretorAutenticado();

        if (!this.obterModelo().verificarSeAlunoInscritoEmCurso(numAluno, idCurso)) {
            throw new UIException("Aluno não se encontra inscrito em nenhum curso.");
        }

        return this.obterModelo().obterHorario(numAluno);
    }

    public void atualizarHorario(String numAluno, Map<String, Set<String>> horario) throws LNException {
        if (!this.obterModelo().validarHorario(numAluno, horario)) {
            throw new UIException("Horario modificado não é válido.");
        }
        String idCurso = this.obterModelo().obterIdCursoDiretorAutenticado();
        this.obterModelo().armazenarHorario(idCurso, numAluno, horario);
    }

    public Collection<String> publicarHorarios() throws LNException {
        String idCurso = this.obterModelo().obterIdCursoDiretorAutenticado();
        Set<String> alunos = this.obterModelo().obterAlunosDeCurso(idCurso);

        this.obterModelo().gerarCredenciaisDeAlunos(alunos);
        Collection<String> falhas = this.obterModelo().notificarAlunos(alunos);

        return falhas;
    }
}