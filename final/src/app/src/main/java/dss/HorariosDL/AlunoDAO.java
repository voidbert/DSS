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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import dss.HorariosLN.SubSistemaHorarios.Aluno;
import dss.HorariosLN.SubSistemaHorarios.Horario;

public class AlunoDAO extends AbstractDAO<Aluno> {
    private static AlunoDAO instance = null;

    private AlunoDAO() {
        super("alunos", "numero");
    }

    public static AlunoDAO getInstance() {
        if (AlunoDAO.instance == null)
            AlunoDAO.instance = new AlunoDAO();

        return AlunoDAO.instance;
    }

    @Override
    public boolean containsValue(Object value) {
        boolean res = false;
        if (value instanceof Aluno && value != null) {
            Aluno curso = this.get(((Aluno) value).getNumero());
            res         = value.equals(curso);
        }
        return res;
    }

    @Override
    public Aluno put(String key, Aluno value) {
        boolean turnAutoCommitBackOn = false;
        try {
            turnAutoCommitBackOn = this.connection.getAutoCommit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            this.connection.setAutoCommit(false);
            Aluno res = this.remove(key);

            try (PreparedStatement statement =
                     this.connection.prepareStatement("INSERT INTO alunos(numero) VALUES (?)")) {

                statement.setString(1, value.getNumero());
                statement.executeUpdate();
            }

            Set<String> ucs = value.getNomesDeUCs();
            for (String uc : ucs) {
                try (PreparedStatement statement = this.connection.prepareStatement(
                         "INSERT INTO alunoUcs(aluno, uc) VALUES (?, ?)")) {

                    statement.setString(1, value.getNumero());
                    statement.setString(2, uc);
                    statement.executeUpdate();
                }
            }

            Map<String, Set<String>> turnos = value.getHorario().getNomesDeTurnos();
            for (String uc : turnos.keySet()) {
                for (String turno : turnos.get(uc)) {
                    try (PreparedStatement statement = this.connection.prepareStatement(
                             "INSERT INTO horarios(aluno, uc, turno) VALUES (?, ?, ?)")) {

                        statement.setString(1, value.getNumero());
                        statement.setString(2, uc);
                        statement.setString(3, turno);
                        statement.executeUpdate();
                    }
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
    protected Aluno decodeTuple(ResultSet result) throws SQLException {
        String numero = result.getString(1);

        Set<String> ucs = new HashSet<String>();
        try (PreparedStatement statement =
                 this.connection.prepareStatement("SELECT uc FROM alunoUcs WHERE aluno=?")) {

            statement.setString(1, numero);
            ResultSet set = statement.executeQuery();
            while (set.next())
                ucs.add(set.getString(1));
        }

        Map<String, Set<String>> turnos = new HashMap<String, Set<String>>();
        try (PreparedStatement statement =
                 this.connection.prepareStatement("SELECT uc, turno FROM horarios WHERE aluno=?")) {

            statement.setString(1, numero);
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                String uc    = set.getString(1);
                String turno = set.getString(2);

                Set<String> turnosUC = turnos.get(uc);
                if (turnosUC == null) {
                    turnosUC = new HashSet<String>();
                    turnos.put(uc, turnosUC);
                }

                turnosUC.add(turno);
            }
        }

        return new Aluno(numero, ucs, new Horario(turnos));
    }
}
