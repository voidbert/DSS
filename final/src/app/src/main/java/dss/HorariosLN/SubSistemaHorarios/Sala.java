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

public class Sala {
    private String nome;
    private int    capacidade;

    public Sala(String nome, int capacidade) {
        this.nome       = nome;
        this.capacidade = capacidade;
    }

    public Sala(Sala sala) {
        this(sala.getNome(), sala.getCapacidade());
    }

    public String getNome() {
        return this.nome;
    }

    public int getCapacidade() {
        return this.capacidade;
    }

    @Override
    public int hashCode() {
        return this.nome.hashCode();
    }

    @Override
    public Object clone() {
        return new Sala(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || this.getClass() != o.getClass())
            return false;

        Sala sala = (Sala) o;
        return this.nome.equals(sala.getNome()) && this.capacidade == sala.getCapacidade();
    }

    @Override
    public String toString() {
        return String.format("Sala(nome=%s, capacidade=%d)", this.nome, this.capacidade);
    }
}
