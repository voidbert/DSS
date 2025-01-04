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

package dss.HorariosLN.SubSistemaHorarios;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface IGestHorarios {
    public Map<String, Set<String>> obterHorario(String numeroAluno) throws HorariosException;
    public boolean                  verificarExistenciaAluno(String numeroAluno);
    public boolean verificarSeAlunoInscritoEmCurso(String numeroAluno, String idCurso)
        throws HorariosException;
    public Set<String> obterAlunosDeCurso(String idCurso) throws HorariosException;
    public void        eliminarDadosCurso(String idCurso) throws HorariosException;
    public boolean     verificarCursoTemUCs(String idCurso) throws HorariosException;
    public void        importarUCs(String caminhoFicheiro, String idCurso) throws HorariosException;
    public Set<String> obterUCsDeCurso(String idCurso) throws HorariosException;
    public boolean     verificarCursoTemAlunos(String idCurso) throws HorariosException;
    public void importarAlunos(String caminhoFicheiro, String idCurso) throws HorariosException;
    public void registarAluno(String idCurso, String numeroAluno) throws HorariosException;
    public void registarUCsDeAluno(String idCurso, String numeroAluno, Set<String> nomeUCs)
        throws HorariosException;
    public void                     gerarHorarios(String idCurso) throws HorariosException;
    public Collection<Sobreposicao> procurarSobreposicoes(String idCurso) throws HorariosException;
    public boolean validarHorario(String numeroAluno, Map<String, Set<String>> horario)
        throws HorariosException;
    public void armazenarHorario(String                   idCurso,
                                 String                   numeroAluno,
                                 Map<String, Set<String>> horario) throws HorariosException;
}
