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

package dss.HorariosLN;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import dss.HorariosLN.SubSistemaHorarios.Sobreposicao;

public interface IHorariosLN {
    // Sub sistema de utilizadores
    public void        iniciarSessao(String email, String password) throws LNException;
    public void        terminarSessao() throws LNException;
    public String      obterNumeroAlunoAutenticado() throws LNException;
    public String      obterIdCursoDiretorAutenticado() throws LNException;
    public void        eliminarCredenciaisDeAlunos(Set<String> alunos);
    public void        gerarCredenciaisDeAlunos(Set<String> alunos);
    public Set<String> notificarAlunos(Set<String> alunos);

    // Sub sistema de horários
    public Map<String, Set<String>> obterHorario(String numeroAluno) throws LNException;
    public boolean                  verificarExistenciaAluno(String numeroAluno);
    public boolean verificarSeAlunoInscritoEmCurso(String numeroAluno, String idCurso)
        throws LNException;
    public Set<String> obterAlunosDeCurso(String idCurso) throws LNException;
    public void        eliminarDadosCurso(String idCurso) throws LNException;
    public boolean     verificarCursoTemUCs(String idCurso) throws LNException;
    public void        importarUCs(String caminhoFicheiro, String idCurso) throws LNException;
    public Set<String> obterUCsDeCurso(String idCurso) throws LNException;
    public boolean     verificarCursoTemAlunos(String idCurso) throws LNException;
    public void        importarAlunos(String caminhoFicheiro, String idCurso) throws LNException;
    public void        registarAluno(String idCurso, String numeroAluno) throws LNException;
    public void registarUCsDeAluno(String numeroAluno, Set<String> nomeUCs) throws LNException;
    public void gerarHorarios(String idCurso) throws LNException;
    public Collection<Sobreposicao> procurarSobreposicoes(String idCurso) throws LNException;
    public boolean validarHorario(String numeroAluno, Map<String, Set<String>> horario)
        throws LNException;
    public void armazenarHorario(String                   idCurso,
                                 String                   numeroAluno,
                                 Map<String, Set<String>> horario) throws LNException;
}
