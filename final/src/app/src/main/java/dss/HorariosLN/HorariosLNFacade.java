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

import dss.HorariosLN.SubSistemaUtilizadores.GestUtilizadoresFacade;
import dss.HorariosLN.SubSistemaUtilizadores.IGestUtilizadores;

public class HorariosLNFacade implements IHorariosLN {
    private IGestUtilizadores ssUtilizadores;

    public HorariosLNFacade() {
        this.ssUtilizadores = new GestUtilizadoresFacade();
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

    public void eliminarCredenciaisDeAlunos(Collection<String> alunos) {
        this.ssUtilizadores.eliminarCredenciaisDeAlunos(alunos);
    }

    public void gerarCredenciaisDeAlunos(Collection<String> alunos) {
        this.ssUtilizadores.gerarCredenciaisDeAlunos(alunos);
    }

    public Collection<String> notificarAlunos(Collection<String> alunos) {
        Collection<String> res = this.ssUtilizadores.notificarAlunos(alunos);
        return res;
    }
}
