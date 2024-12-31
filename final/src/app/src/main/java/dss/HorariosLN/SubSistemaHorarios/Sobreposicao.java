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

public class Sobreposicao {
    private String aluno, uc1, turno1, uc2, turno2;

    public Sobreposicao(String aluno, String uc1, String turno1, String uc2, String turno2) {
        this.aluno  = aluno;
        this.uc1    = uc1;
        this.turno1 = turno1;

        this.uc2    = uc2;
        this.turno2 = turno2;
    }

    public Sobreposicao(Sobreposicao sobreposicao) {
        this(sobreposicao.getAluno(),
             sobreposicao.getUC1(),
             sobreposicao.getTurno1(),
             sobreposicao.getUC2(),
             sobreposicao.getTurno2());
    }

    public String getAluno() {
        return this.aluno;
    }

    public String getUC1() {
        return this.uc1;
    }

    public String getTurno1() {
        return this.turno1;
    }

    public String getUC2() {
        return this.uc2;
    }

    public String getTurno2() {
        return this.turno2;
    }

    @Override
    public Object clone() {
        return new Sobreposicao(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || this.getClass() != o.getClass())
            return false;

        Sobreposicao sobreposicao = (Sobreposicao) o;
        return this.aluno.equals(sobreposicao.getAluno()) &&
            this.uc1.equals(sobreposicao.getUC1()) &&
            this.turno1.equals(sobreposicao.getTurno1()) &&
            this.uc2.equals(sobreposicao.getUC2()) && this.turno2.equals(sobreposicao.getTurno2());
    }

    @Override
    public String toString() {
        return String.format("Sobreposicao(aluno=%s, uc1=%s, turno1=%s, uc2=%s, turno2=%s)",
                             this.aluno,
                             this.uc1,
                             this.turno1,
                             this.uc2,
                             this.turno2);
    }
}
