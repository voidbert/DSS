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

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public abstract class Turno {
    private String    nome;
    private DayOfWeek dia;
    private LocalTime comeco, fim;
    private String    sala;

    protected Turno(String nome, DayOfWeek dia, LocalTime comeco, LocalTime fim, String sala) {
        this.nome   = nome;
        this.dia    = dia;
        this.comeco = comeco;
        this.fim    = fim;
        this.sala   = sala;
    }

    protected Turno(JsonElement json, Map<String, Sala> salaChecker) throws HorariosException {
        try {
            JsonObject               object = json.getAsJsonObject();
            Map<String, JsonElement> map    = object.asMap();

            JsonElement nomeElement = map.get("nome");
            if (nomeElement == null)
                throw new HorariosException("Dados inválidos (turno sem propriedade nome)");

            this.nome = nomeElement.getAsString();

            JsonElement diaElement = map.get("dia");
            if (diaElement == null)
                throw new HorariosException("Dados inválidos (turno sem propriedade dia)");

            int       diaInt = diaElement.getAsInt();
            DayOfWeek diaDia = DayOfWeek.of(diaInt);
            this.dia         = diaDia;

            JsonElement comecoElement = map.get("comeco");
            if (comecoElement == null)
                throw new HorariosException("Dados inválidos (turno sem propriedade comeco)");

            String    comecoString = comecoElement.getAsString();
            LocalTime comecoHora   = LocalTime.parse(comecoString);
            this.comeco            = comecoHora;

            JsonElement fimElement = map.get("fim");
            if (fimElement == null)
                throw new HorariosException("Dados inválidos (turno sem propriedade fim)");

            String    fimString = fimElement.getAsString();
            LocalTime fimHora   = LocalTime.parse(fimString);
            this.fim            = fimHora;

            JsonElement salaElement = map.get("sala");
            if (salaElement == null)
                throw new HorariosException("Dados inválidos (turno sem propriedade sala)");

            String  salaNome   = salaElement.getAsString();
            boolean salaExiste = salaChecker.containsKey(salaNome);
            if (!salaExiste)
                throw new HorariosException("Dados inválidos (Sala " + salaNome + " inexistente)");

            this.sala = salaNome;
        } catch (ClassCastException e) {
            throw new HorariosException("Dados inválidos");
        } catch (IllegalStateException e) {
            System.err.println(e.getMessage());
            throw new HorariosException("Dados inválidos");
        }
    }

    public String getNome() {
        return this.nome;
    }

    public DayOfWeek getDia() {
        return this.dia;
    }

    public LocalTime getComeco() {
        return this.comeco;
    }

    public LocalTime getFim() {
        return this.fim;
    }

    public String getSala() {
        return this.sala;
    }

    public abstract int getCapacidade();

    @Override
    public abstract Object clone();

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || this.getClass() != o.getClass())
            return false;

        Turno turno = (Turno) o;
        return this.nome.equals(turno.getNome()) && this.dia.equals(turno.getDia()) &&
            this.comeco.equals(turno.getComeco()) && this.fim.equals(turno.getFim()) &&
            this.sala.equals(turno.getSala());
    }
}
