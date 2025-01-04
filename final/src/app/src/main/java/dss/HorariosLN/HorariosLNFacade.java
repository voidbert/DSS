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

import dss.HorariosLN.SubSistemaHorarios.GestHorariosFacade;
import dss.HorariosLN.SubSistemaHorarios.IGestHorarios;
import dss.HorariosLN.SubSistemaHorarios.Sobreposicao;
import dss.HorariosLN.SubSistemaUtilizadores.GestUtilizadoresFacade;
import dss.HorariosLN.SubSistemaUtilizadores.IGestUtilizadores;

public class HorariosLNFacade implements IHorariosLN {
    private IGestUtilizadores ssUtilizadores;
    private IGestHorarios     ssHorarios;

    public HorariosLNFacade() {
        this.ssUtilizadores = new GestUtilizadoresFacade();
        this.ssHorarios     = new GestHorariosFacade();
    }

    public void iniciarSessao(String email, String password) throws LNException {
        this.ssUtilizadores.iniciarSessao(email, password);
    }

    public void terminarSessao() throws LNException {
        this.ssUtilizadores.terminarSessao();
    }

    public String obterNumeroAlunoAutenticado() throws LNException {
        String res = this.ssUtilizadores.obterNumeroAlunoAutenticado();
        return res;
    }

    public String obterIdCursoDiretorAutenticado() throws LNException {
        String res = this.ssUtilizadores.obterIdCursoDiretorAutenticado();
        return res;
    }

    public void eliminarCredenciaisDeAlunos(Set<String> alunos) {
        this.ssUtilizadores.eliminarCredenciaisDeAlunos(alunos);
    }

    public void gerarCredenciaisDeAlunos(Set<String> alunos) {
        this.ssUtilizadores.gerarCredenciaisDeAlunos(alunos);
    }

    public Set<String> notificarAlunos(Set<String> alunos) {
        Set<String> res = this.ssUtilizadores.notificarAlunos(alunos);
        return res;
    }

    public Map<String, Set<String>> obterHorario(String numeroAluno) throws LNException {
        Map<String, Set<String>> res = this.ssHorarios.obterHorario(numeroAluno);
        return res;
    }

    public boolean verificarExistenciaAluno(String numeroAluno) {
        boolean res = this.ssHorarios.verificarExistenciaAluno(numeroAluno);
        return res;
    }

    public boolean verificarSeAlunoInscritoEmCurso(String numeroAluno, String idCurso)
        throws LNException {
        boolean res = this.ssHorarios.verificarSeAlunoInscritoEmCurso(numeroAluno, idCurso);
        return res;
    }

    public Set<String> obterAlunosDeCurso(String idCurso) throws LNException {
        Set<String> res = this.ssHorarios.obterAlunosDeCurso(idCurso);
        return res;
    }

    public void eliminarDadosCurso(String idCurso) throws LNException {
        this.ssHorarios.eliminarDadosCurso(idCurso);
    }

    public boolean verificarCursoTemUCs(String idCurso) throws LNException {
        boolean res = this.ssHorarios.verificarCursoTemUCs(idCurso);
        return res;
    }

    public void importarUCs(String caminhoFicheiro, String idCurso) throws LNException {
        this.ssHorarios.importarUCs(caminhoFicheiro, idCurso);
    }

    public Set<String> obterUCsDeCurso(String idCurso) throws LNException {
        Set<String> res = this.ssHorarios.obterUCsDeCurso(idCurso);
        return res;
    }

    public boolean verificarCursoTemAlunos(String idCurso) throws LNException {
        boolean res = this.ssHorarios.verificarCursoTemAlunos(idCurso);
        return res;
    }

    public void importarAlunos(String caminhoFicheiro, String idCurso) throws LNException {
        this.ssHorarios.importarAlunos(caminhoFicheiro, idCurso);
    }

    public void registarAluno(String idCurso, String numeroAluno) throws LNException {
        this.ssHorarios.registarAluno(idCurso, numeroAluno);
    }

    public void registarUCsDeAluno(String idCurso, String numeroAluno, Set<String> nomeUCs)
        throws LNException {
        this.ssHorarios.registarUCsDeAluno(idCurso, numeroAluno, nomeUCs);
    }

    public void gerarHorarios(String idCurso) throws LNException {
        this.ssHorarios.gerarHorarios(idCurso);
    }

    public Collection<Sobreposicao> procurarSobreposicoes(String idCurso) throws LNException {
        Collection<Sobreposicao> res = this.ssHorarios.procurarSobreposicoes(idCurso);
        return res;
    }

    public boolean validarHorario(String numeroAluno, Map<String, Set<String>> horario)
        throws LNException {

        boolean res = this.ssHorarios.validarHorario(numeroAluno, horario);
        return res;
    }

    public void armazenarHorario(String                   idCurso,
                                 String                   numeroAluno,
                                 Map<String, Set<String>> horario) throws LNException {
        this.ssHorarios.armazenarHorario(idCurso, numeroAluno, horario);
    }
}
