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
import dss.HorariosDL.SalaDAO;

public class TurnoPratico extends Turno {
    private int capacidade;

    public TurnoPratico(String    nome,
                        DayOfWeek dia,
                        LocalTime comeco,
                        LocalTime fim,
                        String    sala,
                        int       capacidade) {

        super(nome, dia, comeco, fim, sala);
        this.capacidade = capacidade;
    }

    public TurnoPratico(TurnoPratico turno) {
        this(turno.getNome(),
             turno.getDia(),
             turno.getComeco(),
             turno.getFim(),
             turno.getSala(),
             turno.getCapacidadeDefinida());
    }

    public TurnoPratico(JsonElement json, Map<String, Sala> salaChecker) throws HorariosException {
        super(json, salaChecker);

        try {
            JsonObject               object = json.getAsJsonObject();
            Map<String, JsonElement> map    = object.asMap();

            JsonElement capacidadeElement = map.get("capacidade");
            if (capacidadeElement == null)
                throw new HorariosException("Dados inválidos (turno sem propriedade capacidade)");
            this.capacidade = capacidadeElement.getAsInt();
        } catch (ClassCastException e) {
            throw new HorariosException("Dados inválidos");
        } catch (IllegalStateException e) {
            throw new HorariosException("Dados inválidos");
        }
    }

    public int getCapacidadeDefinida() {
        return this.capacidade;
    }

    @Override
    public int getCapacidade() {
        int capacidadeDaSala = SalaDAO.getInstance().get(this.getSala()).getCapacidade();
        return Math.min(this.capacidade, capacidadeDaSala);
    }

    @Override
    public Object clone() {
        return new TurnoPratico(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || this.getClass() != o.getClass())
            return false;

        TurnoPratico turno = (TurnoPratico) o;
        return super.equals(o) && this.capacidade == turno.getCapacidadeDefinida();
    }

    @Override
    public String toString() {
        return String.format(
            "TurnoPratico(nome=%s, dia=%s, comeco=%s, fim=%s, sala=%s, capacidade=%d)",
            this.getNome(),
            this.getDia().toString(),
            this.getComeco().toString(),
            this.getFim().toString(),
            this.getSala(),
            this.capacidade);
    }
}
