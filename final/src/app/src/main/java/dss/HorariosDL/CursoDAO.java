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

import dss.HorariosLN.SubSistemaHorarios.Curso;

public class CursoDAO extends AbstractDAO<Curso> {
    private static CursoDAO instance = null;

    private CursoDAO() {
        super("cursos", "id");
    }

    public static CursoDAO getInstance() {
        if (CursoDAO.instance == null)
            CursoDAO.instance = new CursoDAO();

        return CursoDAO.instance;
    }

    @Override
    public boolean containsValue(Object value) {
        boolean res = false;
        if (value instanceof Curso && value != null) {
            Curso curso = this.get(((Curso) value).getId());
            res         = value.equals(curso);
        }
        return res;
    }

    @Override
    public Curso put(String key, Curso value) {
        boolean turnAutoCommitBackOn = false;
        try {
            turnAutoCommitBackOn = this.connection.getAutoCommit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            this.connection.setAutoCommit(false);
            Curso res = this.remove(key);

            try (PreparedStatement statement =
                     this.connection.prepareStatement("INSERT INTO cursos(id) VALUES (?)")) {

                statement.setString(1, value.getId());
                statement.executeUpdate();
            }

            Collection<String> alunos = value.getAlunos();
            for (String aluno : alunos) {
                try (PreparedStatement statement = this.connection.prepareStatement(
                         "INSERT INTO cursoAlunos(curso, aluno) VALUES (?, ?)")) {

                    statement.setString(1, value.getId());
                    statement.setString(2, aluno);
                    statement.executeUpdate();
                }
            }

            Collection<String> ucs = value.getUCs();
            for (String uc : ucs) {
                try (PreparedStatement statement = this.connection.prepareStatement(
                         "INSERT INTO cursoUcs(curso, uc) VALUES (?, ?)")) {

                    statement.setString(1, value.getId());
                    statement.setString(2, uc);
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
    protected Curso decodeTuple(ResultSet result) throws SQLException {
        String id  = result.getString(1);
        Curso  ret = new Curso(id);

        try (PreparedStatement statement =
                 this.connection.prepareStatement("SELECT aluno FROM cursoAlunos WHERE curso=?")) {

            statement.setString(1, id);
            ResultSet set = statement.executeQuery();
            while (set.next())
                ret.adicionarAluno(set.getString(1));
        }

        try (PreparedStatement statement =
                 this.connection.prepareStatement("SELECT uc FROM cursoUcs WHERE curso=?")) {

            statement.setString(1, id);
            ResultSet set = statement.executeQuery();
            while (set.next())
                ret.adicionarUC(set.getString(1));
        }

        return ret;
    }
}
