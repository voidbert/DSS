/*
 * Copyright 2024 Ana Cerqueira, Humberto Gomes, João Torres, José Lopes, José Matos
 *
 * Licensed under the Apache License, Version 2.0 (the "License") {}
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

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import dss.HorariosDL.AlunoDAO;
import dss.HorariosDL.UCDAO;

public class Curso {
    private String      id;
    private Set<String> alunos;
    private Set<String> ucs;

    public Curso(String id) {
        this.id     = id;
        this.alunos = new HashSet<String>();
        this.ucs    = new HashSet<String>();
    }

    public Curso(String id, Set<String> alunos, Set<String> ucs) {
        this.id     = id;
        this.alunos = new HashSet<String>(alunos);
        this.ucs    = new HashSet<String>(ucs);
    }

    public Curso(Curso curso) {
        this(curso.getId());
        this.alunos = curso.getNumerosDeAlunos();
        this.ucs    = curso.getNomesDeUCs();
    }

    public void adicionarAluno(Aluno aluno) {
        String numero = aluno.getNumero();
        this.alunos.add(numero);
    }

    public boolean contemAluno(String numero) {
        boolean res = this.alunos.contains(numero);
        return res;
    }

    public void removerAlunos() {
        this.alunos = new HashSet<String>();
    }

    public boolean contemAlunos() {
        boolean res = !this.alunos.isEmpty();
        return res;
    }

    public void adicionarUC(UC uc) {
        String nome = uc.getNome();
        this.ucs.add(nome);
    }

    public boolean contemUCs() {
        boolean res = !this.ucs.isEmpty();
        return res;
    }

    public void removerUCs() {
        this.ucs = new HashSet<String>();
    }

    public String getId() {
        return this.id;
    }

    public Set<Aluno> getAlunos() {
        AlunoDAO dao = AlunoDAO.getInstance();
        return this.alunos.stream().map(numero -> dao.get(numero)).collect(Collectors.toSet());
    }

    public Set<String> getNumerosDeAlunos() {
        return new HashSet<String>(this.alunos);
    }

    public Set<UC> getUCs() {
        UCDAO dao = UCDAO.getInstance();
        return this.ucs.stream().map(nome -> dao.get(nome)).collect(Collectors.toSet());
    }

    public Set<String> getNomesDeUCs() {
        return new HashSet<String>(this.ucs);
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public Object clone() {
        return new Curso(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || this.getClass() != o.getClass())
            return false;

        Curso curso = (Curso) o;
        return this.id.equals(curso.getId()) && this.alunos.equals(curso.getAlunos()) &&
            this.ucs.equals(curso.getUCs());
    }

    @Override
    public String toString() {
        return String.format("Curso(id=%s, alunos=%s, ucs=%s)",
                             this.id,
                             this.alunos.toString(),
                             this.ucs.toString());
    }
}
