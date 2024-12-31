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

package dss.HorariosDL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import dss.HorariosLN.SubSistemaHorarios.Turno;
import dss.HorariosLN.SubSistemaHorarios.TurnoPratico;
import dss.HorariosLN.SubSistemaHorarios.TurnoTeorico;
import dss.HorariosLN.SubSistemaHorarios.UC;

public class UCDAO extends AbstractDAO<UC> {
    private static UCDAO instance = null;

    private UCDAO() {
        super("ucs", "nome");
    }

    public static UCDAO getInstance() {
        if (UCDAO.instance == null)
            UCDAO.instance = new UCDAO();

        return UCDAO.instance;
    }

    @Override
    public boolean containsValue(Object value) {
        boolean res = false;
        if (value instanceof UC && value != null) {
            UC uc = this.get(((UC) value).getNome());
            res   = value.equals(uc);
        }
        return res;
    }

    @Override
    public UC put(String key, UC value) {
        boolean turnAutoCommitBackOn = false;
        try {
            turnAutoCommitBackOn = this.connection.getAutoCommit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            this.connection.setAutoCommit(false);
            UC res = this.remove(key);

            try (PreparedStatement statement =
                     this.connection.prepareStatement("INSERT INTO ucs(nome) VALUES (?)")) {

                statement.setString(1, value.getNome());
                statement.executeUpdate();
            }

            Map<String, Turno> praticos = value.getPraticos();
            for (Map.Entry<String, Turno> entry : praticos.entrySet()) {
                TurnoPratico turno = (TurnoPratico) entry.getValue();

                try (
                    PreparedStatement statement = this.connection.prepareStatement(
                        "INSERT INTO turnos(uc, nome, tipo, dia, comeco, fim, sala, capacidade) VALUES (?, ?, 'P', ?, ?, ?, ?, ?)")) {

                    statement.setString(1, value.getNome());
                    statement.setString(2, turno.getNome());
                    statement.setInt(3, turno.getDia().getValue());
                    statement.setString(4, turno.getComeco().toString());
                    statement.setString(5, turno.getFim().toString());
                    statement.setString(6, turno.getNomeDeSala());
                    statement.setInt(7, turno.getCapacidadeDefinida());
                    statement.executeUpdate();
                }
            }

            Map<String, Turno> teoricos = value.getTeoricos();
            for (Map.Entry<String, Turno> entry : teoricos.entrySet()) {
                TurnoTeorico turno = (TurnoTeorico) entry.getValue();

                try (
                    PreparedStatement statement = this.connection.prepareStatement(
                        "INSERT INTO turnos(uc, nome, tipo, dia, comeco, fim, sala) VALUES (?, ?, 'T', ?, ?, ?, ?)")) {

                    statement.setString(1, value.getNome());
                    statement.setString(2, turno.getNome());
                    statement.setInt(3, turno.getDia().getValue());
                    statement.setString(4, turno.getComeco().toString());
                    statement.setString(5, turno.getFim().toString());
                    statement.setString(6, turno.getNomeDeSala());
                    statement.executeUpdate();
                }
            }

            this.connection.commit();
            return res;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (turnAutoCommitBackOn)
                    this.connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    protected UC decodeTuple(ResultSet result) throws SQLException {
        String nome = result.getString(1);

        Map<String, Turno> praticos = new HashMap<String, Turno>();
        try (PreparedStatement statement =
                 this.connection.prepareStatement("SELECT * FROM turnos WHERE uc=? AND tipo='P'")) {

            statement.setString(1, nome);
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                Turno pratico = this.decodeTurno(set);
                praticos.put(pratico.getNome(), pratico);
            }
        }

        Map<String, Turno> teoricos = new HashMap<String, Turno>();
        try (PreparedStatement statement =
                 this.connection.prepareStatement("SELECT * FROM turnos WHERE uc=? AND tipo='T'")) {

            statement.setString(1, nome);
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                Turno teorico = this.decodeTurno(set);
                teoricos.put(teorico.getNome(), teorico);
            }
        }

        return new UC(nome, praticos, teoricos);
    }

    private Turno decodeTurno(ResultSet result) throws SQLException {
        String    nome   = result.getString(2);
        DayOfWeek dia    = DayOfWeek.of(result.getInt(4));
        LocalTime comeco = LocalTime.parse(result.getString(5));
        LocalTime fim    = LocalTime.parse(result.getString(6));
        String    sala   = result.getString(7);

        String tipo = result.getString(3);
        if (tipo.equals("P")) {
            int capacidade = result.getInt(8);
            return new TurnoPratico(nome, dia, comeco, fim, sala, capacidade);
        } else {
            return new TurnoTeorico(nome, dia, comeco, fim, sala);
        }
    }
}
