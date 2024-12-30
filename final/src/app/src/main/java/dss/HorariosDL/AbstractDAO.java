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

public abstract class AbstractDAO<V> implements Map<String, V> {
    protected Connection connection;
    private String       tableName;
    private String       keyName;

    protected AbstractDAO(String tableName, String keyName) {
        this.tableName = tableName;
        this.keyName   = keyName;

        try {
            this.connection =
                DriverManager.getConnection(Config.URL, Config.USERNAME, Config.PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void clear() {
        try (Statement statement = this.connection.createStatement()) {
            statement.executeUpdate("DELETE FROM " + this.tableName + " WHERE TRUE");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean containsKey(Object key) {
        boolean res = false;

        if (key instanceof String && key != null) {
            try (PreparedStatement statement = this.connection.prepareStatement(
                     "SELECT " + this.keyName + " FROM " + this.tableName + " WHERE " +
                     this.keyName + "=?")) {
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

    public abstract boolean containsValue(Object value);

    public Set<Entry<String, V>> entrySet() {
        Set<Entry<String, V>> entries = new HashSet<Entry<String, V>>();
        try (Statement statement = this.connection.createStatement();
             ResultSet result    = statement.executeQuery("SELECT * FROM " + this.tableName)) {

            while (result.next())
                entries.add(Map.entry(result.getString(1), this.decodeTuple(result)));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return entries;
    }

    public V get(Object key) {
        V res = null;

        if (key instanceof String && key != null) {
            try (PreparedStatement statement = this.connection.prepareStatement(
                     "SELECT * FROM " + this.tableName + " WHERE " + this.keyName + "=?")) {
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
             ResultSet result =
                 statement.executeQuery("SELECT " + this.keyName + " FROM " + this.tableName)) {

            while (result.next())
                ret.add(result.getString(1));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return ret;
    }

    public abstract V put(String key, V value);

    public void putAll(Map<? extends String, ? extends V> map) {
        boolean turnAutoCommitBackOn = false;
        try {
            turnAutoCommitBackOn = this.connection.getAutoCommit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            this.connection.setAutoCommit(false);

            for (Map.Entry<? extends String, ? extends V> entry : map.entrySet())
                this.put(entry.getKey(), entry.getValue());

            this.connection.commit();
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

    public int size() {
        try (Statement statement = this.connection.createStatement();
             ResultSet result = statement.executeQuery("SELECT COUNT(*) FROM " + this.tableName)) {

            result.next();
            return result.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public V remove(Object key) {
        boolean turnAutoCommitBackOn = false;
        try {
            turnAutoCommitBackOn = this.connection.getAutoCommit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            this.connection.setAutoCommit(false);

            V ret = this.get(key);

            try (PreparedStatement statement = this.connection.prepareStatement(
                     "DELETE FROM " + this.tableName + " WHERE " + this.keyName + "=?")) {

                statement.setString(1, (String) key);
                statement.executeUpdate();
            }

            this.connection.commit();
            return ret;
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

    public Collection<V> values() {
        Collection<V> ucs = new ArrayList<V>();
        try (Statement statement = this.connection.createStatement();
             ResultSet result    = statement.executeQuery("SELECT * FROM " + this.tableName)) {

            while (result.next())
                ucs.add(this.decodeTuple(result));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return ucs;
    }

    protected abstract V decodeTuple(ResultSet tuple) throws SQLException;
}
