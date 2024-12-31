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

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dss.HorariosDL.UCDAO;

public class Aluno {
    private String      numero;
    private Set<String> ucs;
    private Horario     horario;

    public Aluno(String numero) {
        this.numero  = numero;
        this.ucs     = new HashSet<String>();
        this.horario = new Horario();
    }

    public Aluno(String numero, Set<String> ucs, Horario horario) {
        this.numero  = numero;
        this.ucs     = new HashSet<String>(ucs);
        this.horario = (Horario) horario.clone();
    }

    public Aluno(JsonElement json, Map<String, UC> ucChecker) throws HorariosException {
        this.horario = new Horario();

        try {
            JsonObject               object = json.getAsJsonObject();
            Map<String, JsonElement> map    = object.asMap();

            JsonElement numeroElement = map.get("numero");
            if (numeroElement == null)
                throw new HorariosException("Dados inválidos (aluno sem propriedade numero)");

            this.numero = numeroElement.getAsString();
            this.ucs    = new HashSet<String>();

            JsonElement arraysElement = map.get("ucs");
            if (arraysElement == null)
                throw new HorariosException("Dados inválidos (aluno sem propriedade ucs)");

            JsonArray jsonUcs = arraysElement.getAsJsonArray();

            for (JsonElement element : jsonUcs) {
                String uc = element.getAsString();

                boolean ucExiste = ucChecker.containsKey(uc);
                if (!ucExiste)
                    throw new HorariosException("Dados inválidos (UC " + uc + " inexistente)");

                this.ucs.add(uc);
            }
        } catch (ClassCastException e) {
            throw new HorariosException("Dados inválidos");
        } catch (IllegalStateException e) {
            throw new HorariosException("Dados inválidos");
        }
    }

    public Aluno(Aluno aluno) {
        this(aluno.getNumero(), aluno.getNomesDeUCs(), aluno.getHorario());
    }

    public String getNumero() {
        return this.numero;
    }

    public Set<UC> getUCs() {
        UCDAO dao = UCDAO.getInstance();
        return this.ucs.stream().map(nome -> dao.get(nome)).collect(Collectors.toSet());
    }

    public Set<String> getNomesDeUCs() {
        return new HashSet<String>(this.ucs);
    }

    public Horario getHorario() {
        return (Horario) this.horario.clone();
    }

    public Collection<Sobreposicao> procurarSobreposicoes() {
        Collection<Sobreposicao> res = this.horario.procurarSobreposicoes(this.numero);
        return res;
    }

    public void setUCs(Set<UC> ucs) {
        this.ucs = ucs.stream().map(uc -> uc.getNome()).collect(Collectors.toSet());
    }

    public void setHorario(Horario horario) {
        this.horario = (Horario) horario.clone();
    }

    @Override
    public int hashCode() {
        return this.numero.hashCode();
    }

    @Override
    public Object clone() {
        return new Aluno(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || this.getClass() != o.getClass())
            return false;

        Aluno aluno = (Aluno) o;
        return this.numero.equals(aluno.getNumero()) && this.ucs.equals(aluno.getUCs()) &&
            this.horario.equals(aluno.getHorario());
    }

    @Override
    public String toString() {
        return String.format("Aluno(numero=%s, ucs=%s)", this.numero, this.ucs.toString());
    }
}
