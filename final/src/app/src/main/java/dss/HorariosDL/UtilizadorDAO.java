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

import dss.HorariosLN.SubSistemaUtilizadores.Utilizador;
import dss.HorariosLN.SubSistemaUtilizadores.UtilizadorAluno;
import dss.HorariosLN.SubSistemaUtilizadores.UtilizadorDiretorDeCurso;

public class UtilizadorDAO extends AbstractDAO<Utilizador> {
    private static UtilizadorDAO instance = null;

    private UtilizadorDAO() {
        super("utilizadores", "email");
    }

    public static UtilizadorDAO getInstance() {
        if (UtilizadorDAO.instance == null)
            UtilizadorDAO.instance = new UtilizadorDAO();

        return UtilizadorDAO.instance;
    }

    @Override
    public boolean containsValue(Object value) {
        boolean res = false;
        if (value instanceof Utilizador && value != null) {
            Utilizador curso = this.get(((Utilizador) value).getEmail());
            res              = value.equals(curso);
        }
        return res;
    }

    @Override
    public Utilizador put(String key, Utilizador value) {
        boolean turnAutoCommitBackOn = false;
        try {
            turnAutoCommitBackOn = this.connection.getAutoCommit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            this.connection.setAutoCommit(false);
            Utilizador res = this.remove(key);

            try (
                PreparedStatement statement = this.connection.prepareStatement(
                    "INSERT INTO utilizadores(tipo, email, password, numeroAluno, idCurso) VALUES (?, ?, ?, ?, ?)")) {
                statement.setString(2, value.getEmail());
                statement.setString(3, value.getPassword());

                if (value instanceof UtilizadorAluno) {
                    statement.setString(1, "A");
                    statement.setString(4, ((UtilizadorAluno) value).getNumero());
                    statement.setString(5, null);
                } else if (value instanceof UtilizadorDiretorDeCurso) {
                    statement.setString(1, "D");
                    statement.setString(4, null);
                    statement.setString(5, ((UtilizadorDiretorDeCurso) value).getIdCurso());
                }

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
    protected Utilizador decodeTuple(ResultSet result) throws SQLException {
        String tipo     = result.getString(1);
        String password = result.getString(3);

        Utilizador ret = null;
        if (tipo.equals("A")) {
            String numero = result.getString(4);
            ret           = new UtilizadorAluno(numero, password);
        } else if (tipo.equals("D")) {
            String email   = result.getString(2);
            String idCurso = result.getString(5);
            ret            = new UtilizadorDiretorDeCurso(email, password, idCurso);
        }

        return ret;
    }
}
