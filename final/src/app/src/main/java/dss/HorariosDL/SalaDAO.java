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
import java.util.Collection;
import java.util.HashSet;

import dss.HorariosLN.SubSistemaHorarios.Sala;

public class SalaDAO extends AbstractDAO<Sala> {
    private static SalaDAO instance = null;

    private SalaDAO() {
        super("salas", "nome");
    }

    public static SalaDAO getInstance() {
        if (SalaDAO.instance == null)
            SalaDAO.instance = new SalaDAO();

        return SalaDAO.instance;
    }

    @Override
    public boolean containsValue(Object value) {
        boolean res = false;
        if (value instanceof Sala && value != null) {
            Sala sala = this.get(((Sala) value).getNome());
            res       = value.equals(sala);
        }
        return res;
    }

    @Override
    public Sala put(String key, Sala value) {
        boolean turnAutoCommitBackOn = false;
        try {
            turnAutoCommitBackOn = this.connection.getAutoCommit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            this.connection.setAutoCommit(false);
            Sala res = this.remove(key);

            try (PreparedStatement statement = this.connection.prepareStatement(
                     "INSERT INTO salas(nome, capacidade) VALUES (?, ?)")) {

                statement.setString(1, value.getNome());
                statement.setInt(2, value.getCapacidade());
                statement.executeUpdate();
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
    protected Sala decodeTuple(ResultSet result) throws SQLException {
        String nome       = result.getString(1);
        int    capacidade = result.getInt(2);
        Sala   ret        = new Sala(nome, capacidade);
        return ret;
    }
}
