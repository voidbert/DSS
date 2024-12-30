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

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import dss.HorariosDL.AlunoDAO;
import dss.HorariosDL.CursoDAO;
import dss.HorariosDL.SalaDAO;
import dss.HorariosDL.UCDAO;

public class GestHorariosFacade implements IGestHorarios {
    private AlunoDAO alunos;
    private CursoDAO cursos;
    private UCDAO    ucs;
    private SalaDAO  salas;

    public GestHorariosFacade() {
        this.alunos = AlunoDAO.getInstance();
        this.cursos = CursoDAO.getInstance();
        this.ucs    = UCDAO.getInstance();
        this.salas  = SalaDAO.getInstance();
    }

    public Horario obterHorario(String numeroAluno) {
        throw new UnsupportedOperationException();
    }

    public boolean verificarExistenciaAluno(String numeroAluno) {
        boolean res = this.alunos.containsKey(numeroAluno);
        return res;
    }

    public boolean verificarSeAlunoInscritoEmCurso(String numeroAluno, String idCurso)
        throws HorariosException {

        Curso curso = this.cursos.get(idCurso);
        if (curso == null)
            throw new HorariosException("Curso não existe");

        boolean res = curso.contemAluno(numeroAluno);
        return res;
    }

    public Collection<String> obterAlunosDeCurso(String idCurso) throws HorariosException {
        Curso curso = this.cursos.get(idCurso);
        if (curso == null)
            throw new HorariosException("Curso não existe");

        Collection<String> numeros = curso.getAlunos();
        return numeros;
    }

    public void eliminarDadosCurso(String idCurso) throws HorariosException {
        // TODO - apagar mais coisas
        this.eliminarAlunosDeCurso(idCurso);
        this.eliminarUCsDeCurso(idCurso);
    }

    private void eliminarAlunosDeCurso(String idCurso) throws HorariosException {
        Curso curso = this.cursos.get(idCurso);
        if (curso == null)
            throw new HorariosException("Curso não existe");

        Collection<String> alunosCurso = curso.getAlunos();
        curso.removerAlunos();
        this.cursos.put(idCurso, curso);

        for (String aluno : alunosCurso)
            this.alunos.remove(aluno);
    }

    private void eliminarUCsDeCurso(String idCurso) throws HorariosException {
        Curso curso = this.cursos.get(idCurso);
        if (curso == null)
            throw new HorariosException("Curso não existe");

        Collection<String> ucsCurso = curso.getUCs();
        curso.removerUCs();
        this.cursos.put(idCurso, curso);

        for (String uc : ucsCurso)
            this.ucs.remove(uc);

        this.cursos.put(idCurso, curso);
    }

    public boolean verificarCursoTemUCs(String idCurso) throws HorariosException {
        Curso curso = this.cursos.get(idCurso);
        if (curso == null)
            throw new HorariosException("Curso não existe");

        return curso.contemUCs();
    }

    public void importarUCs(String caminhoFicheiro, String idCurso) throws HorariosException {
        this.eliminarDadosCurso(idCurso);

        Curso curso = this.cursos.get(idCurso);
        if (curso == null)
            throw new HorariosException("Curso não existe");

        Map<String, Sala>        readOnlySalas = Collections.unmodifiableMap(this.salas);
        Map<String, UC>          novasUCs      = new HashMap<String, UC>();
        Map<String, JsonElement> json          = this.lerJson(caminhoFicheiro);

        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            String      nome    = entry.getKey();
            JsonElement element = entry.getValue();
            UC          uc      = new UC(element, readOnlySalas);

            novasUCs.put(nome, uc);
            curso.adicionarUC(nome);
        }

        this.ucs.putAll(novasUCs);
        this.cursos.put(idCurso, curso);
    }

    public Collection<String> obterUCsDeCurso(String idCurso) throws HorariosException {
        Curso curso = this.cursos.get(idCurso);
        if (curso == null)
            throw new HorariosException("Curso não existe");

        Collection<String> nomes = curso.getUCs();
        return nomes;
    }

    public boolean verificarCursoTemAlunos(String idCurso) throws HorariosException {
        Curso curso = this.cursos.get(idCurso);
        if (curso == null)
            throw new HorariosException("Curso não existe");

        boolean res = curso.contemAlunos();
        return res;
    }

    public void importarAlunos(String caminhoFicheiro, String idCurso) throws HorariosException {
        // TODO - apagar mais coisas
        this.eliminarAlunosDeCurso(idCurso);

        Curso curso = this.cursos.get(idCurso);
        if (curso == null)
            throw new HorariosException("Curso não existe");

        Map<String, UC>          readOnlyUCs = Collections.unmodifiableMap(this.ucs);
        Map<String, Aluno>       novosAlunos = new HashMap<String, Aluno>();
        Map<String, JsonElement> json        = this.lerJson(caminhoFicheiro);

        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            String      numero  = entry.getKey();
            JsonElement element = entry.getValue();
            Aluno       aluno   = new Aluno(element, readOnlyUCs);

            novosAlunos.put(numero, aluno);
            curso.adicionarAluno(numero);
        }

        this.alunos.putAll(novosAlunos);
        this.cursos.put(idCurso, curso);
    }

    public void registarAluno(String idCurso, String numeroAluno) throws HorariosException {
        Curso curso = this.cursos.get(idCurso);
        if (curso == null)
            throw new HorariosException("Curso não existe");

        Aluno aluno = new Aluno(numeroAluno);
        this.alunos.put(numeroAluno, aluno);
        curso.adicionarAluno(numeroAluno);
        this.cursos.put(idCurso, curso);
    }

    // TODO - pensar como fazer isto
    public void registarUCsDeAluno(String numeroAluno, Collection<String> nomeUCs) {}

    public boolean verificarUCTemPreferencias(String idCurso, String nomeUC) {
        throw new UnsupportedOperationException();
    }

    // Dá para fazer isto a tempo?
    public void importarPreferenciasUC(String caminhoFicheiro, String idCurso, String nomeUC) {}

    private Map<String, JsonElement> lerJson(String caminhoFicheiro) throws HorariosException {
        try {
            FileReader               fileReader = new FileReader(caminhoFicheiro);
            JsonReader               jsonReader = new JsonReader(fileReader);
            JsonElement              parsed     = JsonParser.parseReader(jsonReader);
            JsonObject               object     = parsed.getAsJsonObject();
            Map<String, JsonElement> map        = object.asMap();
            return map;
        } catch (Exception e) {
            throw new HorariosException("Dados inválidos");
        }
    }
}
