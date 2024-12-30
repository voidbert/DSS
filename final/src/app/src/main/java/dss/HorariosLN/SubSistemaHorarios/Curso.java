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

import java.util.Collection;
import java.util.HashSet;

import dss.HorariosDL.AlunoDAO;
import dss.HorariosDL.UCDAO;

public class Curso {
    private String             id;
    private Collection<String> alunos;
    private Collection<String> ucs;

    public Curso(String id) {
        this.id     = id;
        this.alunos = new HashSet<String>();
        this.ucs    = new HashSet<String>();
    }

    public Curso(Curso curso) {
        this(curso.getId());
        this.alunos = curso.getAlunos();
        this.ucs    = curso.getUCs();
    }

    public void adicionarAluno(String numero) {
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

    public void adicionarUC(String nome) {
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

    public Collection<String> getAlunos() {
        return new HashSet<String>(this.alunos);
    }

    public Collection<String> getUCs() {
        return new HashSet<String>(this.ucs);
    }

    public Object clone() {
        return new Curso(this);
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || this.getClass() != o.getClass())
            return false;

        Curso curso = (Curso) o;
        return this.id.equals(curso.getId()) && this.alunos.equals(curso.getAlunos()) &&
            this.ucs.equals(curso.getUCs());
    }

    public String toString() {
        return String.format("Curso(id=%s, alunos=%s, ucs=%s)",
                             this.id,
                             this.alunos.toString(),
                             this.ucs.toString());
    }
}
