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
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import dss.HorariosLN.SubSistemaUtilizadores.Utilizador;
import dss.HorariosLN.SubSistemaUtilizadores.UtilizadorAluno;
import dss.HorariosLN.SubSistemaUtilizadores.UtilizadorDiretorDeCurso;

public class UtilizadorDAO implements Map<String, Utilizador> {
    private static UtilizadorDAO instance = null;
    private Connection           connection;

    private UtilizadorDAO() {
        try {
            this.connection =
                DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);

            try (Statement statement = this.connection.createStatement()) {
                statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS utilizadores(
                        tipo        CHAR(1),
                        email       VARCHAR(75) NOT NULL PRIMARY KEY,
                        password    VARCHAR(75) NOT NULL,
                        numeroAluno VARCHAR(10),
                        idCurso     VARCHAR(10),
                        CONSTRAINT chkTipo CHECK (tipo in ('A', 'D'))
                    )""");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static UtilizadorDAO getInstance() {
        if (UtilizadorDAO.instance == null)
            UtilizadorDAO.instance = new UtilizadorDAO();

        return UtilizadorDAO.instance;
    }

    public void clear() {
        try (Statement statement = this.connection.createStatement()) {
            statement.executeUpdate("DELETE FROM utilizadores WHERE TRUE");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean containsKey(Object key) {
        boolean res = false;

        if (key instanceof String) {
            try (PreparedStatement statement = this.connection.prepareStatement(
                     "SELECT email FROM utilizadores WHERE email=?")) {
                statement.setString(1, (String) key);

                try (ResultSet result = statement.executeQuery()) {
                    res = result.next();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return res;
    }

    public boolean containsValue(Object value) {
        boolean res = false;
        if (value instanceof Utilizador) {
            Utilizador utilizador = this.get(((Utilizador) value).getEmail());
            res                   = utilizador.equals(value);
        }
        return res;
    }

    public Set<Entry<String, Utilizador>> entrySet() {
        Set<Entry<String, Utilizador>> entries = new HashSet<Entry<String, Utilizador>>();
        try (Statement statement = this.connection.createStatement();
             ResultSet result    = statement.executeQuery("SELECT * FROM utilizadores")) {

            while (result.next()) {
                Utilizador utilizador = this.decodeTuple(result);
                entries.add(Map.entry(utilizador.getEmail(), utilizador));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return entries;
    }

    public Utilizador get(Object key) {
        Utilizador res = null;

        if (key instanceof String) {
            try (PreparedStatement statement =
                     this.connection.prepareStatement("SELECT * FROM utilizadores WHERE email=?")) {
                statement.setString(1, (String) key);

                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        res = this.decodeTuple(result);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return res;
    }

    public boolean isEmpty() {
        return this.size() == 0;
    }

    public Set<String> keySet() {
        HashSet<String> ret = new HashSet<String>();
        try (Statement statement = this.connection.createStatement();
             ResultSet result    = statement.executeQuery("SELECT email FROM utilizadores")) {

            while (result.next())
                ret.add(result.getString(1));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return ret;
    }

    public Utilizador put(String key, Utilizador value) {
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
                this.connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void putAll(Map<? extends String, ? extends Utilizador> map) {
        try {
            this.connection.setAutoCommit(false);

            for (Utilizador utilizador : map.values())
                this.put(utilizador.getEmail(), utilizador);

            this.connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                this.connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Utilizador remove(Object key) {
        Utilizador ret = this.get(key);

        try (PreparedStatement statement =
                 this.connection.prepareStatement("DELETE FROM utilizadores WHERE email=?")) {
            statement.setString(1, (String) key);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return ret;
    }

    public int size() {
        try (Statement statement = this.connection.createStatement();
             ResultSet result    = statement.executeQuery("SELECT COUNT(*) FROM utilizadores")) {

            result.next();
            return result.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Collection<Utilizador> values() {
        Collection<Utilizador> utilizadores = new ArrayList<Utilizador>();
        try (Statement statement = this.connection.createStatement();
             ResultSet result    = statement.executeQuery("SELECT * FROM utilizadores")) {

            while (result.next())
                utilizadores.add(this.decodeTuple(result));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return utilizadores;
    }

    private Utilizador decodeTuple(ResultSet result) throws SQLException {
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
