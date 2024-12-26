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

package dss.HorariosLN.SubSistemaUtilizadores;

import java.util.ArrayList;
import java.util.Collection;

import dss.HorariosDL.UtilizadorDAO;

public class GestUtilizadoresFacade implements IGestUtilizadores {
    private Utilizador    sessaoIniciada;
    private UtilizadorDAO utilizadores;
    private Mailer        mailer;

    public GestUtilizadoresFacade() {
        this.sessaoIniciada = null;
        this.utilizadores   = UtilizadorDAO.getInstance();
        this.mailer         = new Mailer();
    }

    public void iniciarSessao(String email, String password) throws UtilizadoresException {
        if (sessaoIniciada != null)
            throw new UtilizadoresException("Sessão já iniciada");

        Utilizador utilizador = this.utilizadores.get(email);
        if (utilizador == null)
            throw new UtilizadoresException("Utilizador não existe");

        boolean correta = utilizador.passwordCerta(password);
        if (!correta)
            throw new UtilizadoresException("Password incorreta");

        this.sessaoIniciada = utilizador;
    }

    public void terminarSessao() throws UtilizadoresException {
        if (sessaoIniciada == null)
            throw new UtilizadoresException("Não há sessão iniciada");

        this.sessaoIniciada = null;
    }

    public String obterNumeroAlunoAutenticado() throws UtilizadoresException {
        if (sessaoIniciada == null)
            throw new UtilizadoresException("Não há sessão iniciada");
        if (!(sessaoIniciada instanceof UtilizadorAluno))
            throw new UtilizadoresException("Sessão iniciada não é de aluno");

        String res = ((UtilizadorAluno) this.sessaoIniciada).getNumero();
        return res;
    }

    public String obterIdCursoDiretorAutenticado() throws UtilizadoresException {
        if (sessaoIniciada == null)
            throw new UtilizadoresException("Não há sessão iniciada");
        if (!(sessaoIniciada instanceof UtilizadorDiretorDeCurso))
            throw new UtilizadoresException("Sessão iniciada não é de diretor de curso");

        String res = ((UtilizadorDiretorDeCurso) this.sessaoIniciada).getIdCurso();
        return res;
    }

    public void eliminarCredenciaisDeAlunos(Collection<String> alunos) {
        for (String numero : alunos) {
            String email = UtilizadorAluno.gerarEmail(numero);
            this.utilizadores.remove(email);
        }
    }

    public void gerarCredenciaisDeAlunos(Collection<String> alunos) {
        for (String numero : alunos) {
            UtilizadorAluno aluno = new UtilizadorAluno(numero);
            String          email = aluno.getEmail();
            this.utilizadores.put(email, aluno);
        }
    }

    public Collection<String> notificarAlunos(Collection<String> alunos) {
        Collection<String> falhas = new ArrayList<String>();
        for (String numero : alunos) {
            String     email      = UtilizadorAluno.gerarEmail(numero);
            Utilizador utilizador = this.utilizadores.get(email);

            if (utilizador == null) {
                falhas.add(email);
            } else {
                boolean sucesso = this.mailer.enviarCredenciais(utilizador);
                if (!sucesso)
                    falhas.add(email);
            }
        }
        return falhas;
    }
}
