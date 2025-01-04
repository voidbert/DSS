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
import java.util.stream.Collectors;

import dss.HorariosLN.IHorariosLN;
import dss.HorariosLN.LNException;
import dss.HorariosLN.SubSistemaHorarios.Sobreposicao;

public class DiretorCursoControlador extends Controlador {
    public DiretorCursoControlador(IHorariosLN modelo) {
        super(modelo);
    }

    public void reiniciarSemestre() throws UIException {
        try {
            String      idCurso = this.obterModelo().obterIdCursoDiretorAutenticado();
            Set<String> alunos  = this.obterModelo().obterAlunosDeCurso(idCurso);

            this.obterModelo().eliminarCredenciaisDeAlunos(alunos);
            this.obterModelo().eliminarDadosCurso(idCurso);
        } catch (LNException e) {
            throw new UIException(e.getMessage());
        }
    }

    public boolean verificarCursoTemUCs() {
        try {
            String idCurso = this.obterModelo().obterIdCursoDiretorAutenticado();
            return this.obterModelo().verificarCursoTemUCs(idCurso);
        } catch (LNException e) {
            return false; // Can't happen
        }
    }

    public void importarUnidadesCurricularesTurnos(String caminhoFicheiro) throws UIException {
        try {
            String idCurso = this.obterModelo().obterIdCursoDiretorAutenticado();
            this.obterModelo().importarUCs(caminhoFicheiro, idCurso);
        } catch (LNException e) {
            throw new UIException(e.getMessage());
        }
    }

    public boolean verificarCursoTemAlunos() {
        try {
            String idCurso = this.obterModelo().obterIdCursoDiretorAutenticado();
            return this.obterModelo().verificarCursoTemAlunos(idCurso);
        } catch (LNException e) {
            return false; // Can't happen
        }
    }

    public void importarAlunosEInscricoes(String caminhoFicheiro) throws UIException {
        try {
            String idCurso = this.obterModelo().obterIdCursoDiretorAutenticado();
            this.obterModelo().importarAlunos(caminhoFicheiro, idCurso);
        } catch (LNException e) {
            throw new UIException(e.getMessage());
        }
    }

    public Set<String> obterListaUCs() throws UIException {
        try {
            String idCurso = this.obterModelo().obterIdCursoDiretorAutenticado();
            return this.obterModelo().obterUCsDeCurso(idCurso);
        } catch (LNException e) {
            throw new UIException(e.getMessage());
        }
    }

    public void verificarExistenciaAluno(String numeroAluno) throws UIException {
        if (this.obterModelo().verificarExistenciaAluno(numeroAluno))
            throw new UIException("Aluno com o mesmo número já existe.");
    }

    public void adicionarAluno(String numeroAluno, Set<String> nomeUCs) throws UIException {
        try {
            String idCurso = this.obterModelo().obterIdCursoDiretorAutenticado();
            this.obterModelo().registarAluno(idCurso, numeroAluno);
            this.obterModelo().registarUCsDeAluno(idCurso, numeroAluno, nomeUCs);
        } catch (LNException e) {
            throw new UIException(e.getMessage());
        }
    }

    public Collection<String> gerarHorariosAutomaticamente() throws UIException {
        try {
            String idCurso = this.obterModelo().obterIdCursoDiretorAutenticado();
            this.obterModelo().gerarHorarios(idCurso);

            Collection<Sobreposicao> s = this.obterModelo().procurarSobreposicoes(idCurso);
            return s.stream().map(Object::toString).collect(Collectors.toList());
        } catch (LNException e) {
            throw new UIException(e.getMessage());
        }
    }

    public Map<String, Set<String>> obterHorarioAluno(String numAluno) throws UIException {
        try {
            String idCurso = this.obterModelo().obterIdCursoDiretorAutenticado();
            if (!this.obterModelo().verificarSeAlunoInscritoEmCurso(numAluno, idCurso))
                throw new UIException("Aluno não encontrado no seu curso.");

            return this.obterModelo().obterHorario(numAluno);
        } catch (LNException e) {
            throw new UIException(e.getMessage());
        }
    }

    public void atualizarHorario(String numAluno, Map<String, Set<String>> horario)
        throws UIException {

        try {
            if (!this.obterModelo().validarHorario(numAluno, horario))
                throw new UIException("Horario modificado não é válido.");

            String idCurso = this.obterModelo().obterIdCursoDiretorAutenticado();
            this.obterModelo().armazenarHorario(idCurso, numAluno, horario);
        } catch (LNException e) {
            throw new UIException(e.getMessage());
        }
    }

    public Collection<String> publicarHorarios() throws UIException {
        try {
            String      idCurso = this.obterModelo().obterIdCursoDiretorAutenticado();
            Set<String> alunos  = this.obterModelo().obterAlunosDeCurso(idCurso);

            this.obterModelo().gerarCredenciaisDeAlunos(alunos);
            Collection<String> falhas = this.obterModelo().notificarAlunos(alunos);

            return falhas;
        } catch (LNException e) {
            throw new UIException(e.getMessage());
        }
    }

    public void terminarSessao() {
        try {
            this.obterModelo().terminarSessao();
        } catch (LNException e) {}
    }
}
