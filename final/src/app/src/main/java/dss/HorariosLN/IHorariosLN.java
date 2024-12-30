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

import dss.HorariosLN.SubSistemaHorarios.Horario;

public interface IHorariosLN {
    public void               iniciarSessao(String email, String password) throws LNException;
    public void               terminarSessao() throws LNException;
    public String             obterNumeroAlunoAutenticado() throws LNException;
    public String             obterIdCursoDiretorAutenticado() throws LNException;
    public void               eliminarCredenciaisDeAlunos(Collection<String> alunos);
    public void               gerarCredenciaisDeAlunos(Collection<String> alunos);
    public Collection<String> notificarAlunos(Collection<String> alunos);

    public Horario obterHorario(String numeroAluno) throws LNException;
    public boolean verificarExistenciaAluno(String numeroAluno);
    public boolean verificarSeAlunoInscritoEmCurso(String numeroAluno, String idCurso)
        throws LNException;
    public Collection<String> obterAlunosDeCurso(String idCurso) throws LNException;
    public void               eliminarDadosCurso(String idCurso) throws LNException;
    public boolean            verificarCursoTemUCs(String idCurso) throws LNException;
    public void importarUCs(String caminhoFicheiro, String idCurso) throws LNException;
    public Collection<String> obterUCsDeCurso(String idCurso) throws LNException;
    public boolean            verificarCursoTemAlunos(String idCurso) throws LNException;
    public void importarAlunos(String caminhoFicheiro, String idCurso) throws LNException;
    public void registarAluno(String idCurso, String numeroAluno) throws LNException;
    public void registarUCsDeAluno(String numeroAluno, Collection<String> nomeUCs)
        throws LNException;
    public boolean verificarUCTemPreferencias(String idCurso, String nomeUC) throws LNException;
    public void    importarPreferenciasUC(String caminhoFicheiro, String idCurso, String nomeUC)
        throws LNException;
}
