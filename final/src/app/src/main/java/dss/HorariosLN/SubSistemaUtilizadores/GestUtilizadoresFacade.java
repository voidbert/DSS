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

        // Mailer de stub (para não enviarmos mails a ninguém sem querer)
        this.mailer = (email, subject, message) -> true;
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

        String res = null;
        if (this.sessaoIniciada instanceof UtilizadorAluno) {
            UtilizadorAluno utilizador = (UtilizadorAluno) this.sessaoIniciada;
            res                        = utilizador.getNumero();
        } else {
            throw new UtilizadoresException("Sessão iniciada não é de aluno");
        }
        return res;
    }

    public String obterIdCursoDiretorAutenticado() throws UtilizadoresException {
        if (sessaoIniciada == null)
            throw new UtilizadoresException("Não há sessão iniciada");

        String res = null;
        if (this.sessaoIniciada instanceof UtilizadorDiretorDeCurso) {
            UtilizadorDiretorDeCurso utilizador = (UtilizadorDiretorDeCurso) this.sessaoIniciada;
            res                                 = utilizador.getIdCurso();
        } else {
            throw new UtilizadoresException("Sessão iniciada não é de aluno");
        }
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

            if (utilizador == null || !(utilizador instanceof UtilizadorAluno)) {
                falhas.add(email);
            } else {
                String password = ((UtilizadorAluno) utilizador).getPassword();
                String mensagem = "A tua password é " + password;

                boolean sucesso = this.mailer.send(email, "Password para horários", mensagem);
                if (!sucesso)
                    falhas.add(email);
            }
        }
        return falhas;
    }
}
