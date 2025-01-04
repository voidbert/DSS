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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class UC {
    private String             nome;
    private Map<String, Turno> praticos;
    private Map<String, Turno> teoricos;

    public UC(String nome, Map<String, Turno> praticos, Map<String, Turno> teoricos) {
        this.nome     = nome;
        this.praticos = praticos.entrySet().stream().collect(
            Collectors.toMap(e -> e.getKey(), e -> (Turno) e.getValue().clone()));
        this.teoricos = teoricos.entrySet().stream().collect(
            Collectors.toMap(e -> e.getKey(), e -> (Turno) e.getValue().clone()));
    }

    public UC(JsonElement json, Map<String, Sala> salaChecker) throws HorariosException {
        try {
            JsonObject               object = json.getAsJsonObject();
            Map<String, JsonElement> map    = object.asMap();

            JsonElement nomeElement = map.get("nome");
            if (nomeElement == null)
                throw new HorariosException("Dados inválidos (UC sem propriedade nome)");

            String nome = nomeElement.getAsString();
            this.nome   = nome;

            this.importarPraticos(map, salaChecker);
            this.importarTeoricos(map, salaChecker);
        } catch (ClassCastException e) {
            throw new HorariosException("Dados inválidos");
        } catch (IllegalStateException e) {
            throw new HorariosException("Dados inválidos");
        }
    }

    public UC(UC uc) {
        this(uc.getNome(), uc.getPraticos(), uc.getTeoricos());
    }

    private void importarPraticos(Map<String, JsonElement> map, Map<String, Sala> salaChecker)
        throws HorariosException {
        JsonElement praticosElement = map.get("praticos");
        if (praticosElement == null)
            throw new HorariosException("Dados inválidos (UC sem propriedade praticos)");

        JsonObject               praticosObject = praticosElement.getAsJsonObject();
        Map<String, JsonElement> praticosJson   = praticosObject.asMap();
        this.praticos                           = new HashMap<String, Turno>();

        for (Map.Entry<String, JsonElement> entry : praticosJson.entrySet()) {
            String      nomeTurno    = entry.getKey();
            JsonElement turnoElement = entry.getValue();

            Turno pratico = new TurnoPratico(turnoElement, salaChecker);
            this.praticos.put(nomeTurno, pratico);
        }
    }

    private void importarTeoricos(Map<String, JsonElement> map, Map<String, Sala> salaChecker)
        throws HorariosException {
        JsonElement teoricosElement = map.get("teoricos");
        if (teoricosElement == null)
            throw new HorariosException("Dados inválidos (UC sem propriedade teoricos)");

        JsonObject               teoricosObject = teoricosElement.getAsJsonObject();
        Map<String, JsonElement> teoricosJson   = teoricosObject.asMap();
        this.teoricos                           = new HashMap<String, Turno>();

        for (Map.Entry<String, JsonElement> entry : teoricosJson.entrySet()) {
            String      nomeTurno    = entry.getKey();
            JsonElement turnoElement = entry.getValue();

            Turno teorico = new TurnoTeorico(turnoElement, salaChecker);
            this.teoricos.put(nomeTurno, teorico);
        }
    }

    public String getNome() {
        return this.nome;
    }

    public Map<String, Turno> getPraticos() {
        return this.praticos.entrySet().stream().collect(
            Collectors.toMap(e -> e.getKey(), e -> (Turno) e.getValue().clone()));
    }

    public Set<String> getNomesDePraticos() {
        return new HashSet(this.praticos.keySet());
    }

    public Set<String> getNomesDeTeoricos() {
        return new HashSet(this.teoricos.keySet());
    }

    public Set<String> getNomesDeTurnos() {
        Set<String> p = this.getNomesDePraticos();
        Set<String> t = this.getNomesDeTeoricos();
        p.addAll(t);
        return t;
    }

    public Map<String, Turno> getTeoricos() {
        return this.teoricos.entrySet().stream().collect(
            Collectors.toMap(e -> e.getKey(), e -> (Turno) e.getValue().clone()));
    }

    public Turno getTurno(String nome) {
        Turno ret = this.praticos.get(nome);
        if (ret == null)
            ret = this.teoricos.get(nome);

        if (ret != null)
            ret = (Turno) ret.clone();
        return ret;
    }

    @Override
    public int hashCode() {
        return this.nome.hashCode();
    }

    @Override
    public Object clone() {
        return new UC(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || this.getClass() != o.getClass())
            return false;

        UC uc = (UC) o;
        return this.nome.equals(uc.getNome());
    }

    @Override
    public String toString() {
        return String.format("UC(nome=%s, praticos=%s, teoricos=%s)",
                             this.nome,
                             this.praticos.toString(),
                             this.teoricos.toString());
    }
}
