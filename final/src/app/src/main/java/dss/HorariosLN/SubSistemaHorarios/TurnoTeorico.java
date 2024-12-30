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
import dss.HorariosDL.SalaDAO;

public class TurnoTeorico extends Turno {
    public TurnoTeorico(String nome, DayOfWeek dia, LocalTime comeco, LocalTime fim, String sala) {
        super(nome, dia, comeco, fim, sala);
    }

    public TurnoTeorico(JsonElement json, Map<String, Sala> salaChecker) throws HorariosException {
        super(json, salaChecker);
    }

    public TurnoTeorico(TurnoTeorico turno) {
        this(turno.getNome(), turno.getDia(), turno.getComeco(), turno.getFim(), turno.getSala());
    }

    @Override
    public int getCapacidade() {
        return SalaDAO.getInstance().get(this.getSala()).getCapacidade();
    }

    @Override
    public Object clone() {
        return new TurnoTeorico(this);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public String toString() {
        return String.format("TurnoTeorico(nome=%s, dia=%s, comeco=%s, fim=%s, sala=%s)",
                             this.getNome(),
                             this.getDia().toString(),
                             this.getComeco().toString(),
                             this.getFim().toString(),
                             this.getSala());
    }
}
