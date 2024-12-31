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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

    public Map<String, Set<String>> obterHorario(String numeroAluno) throws HorariosException {
        Aluno aluno = this.alunos.get(numeroAluno);
        if (aluno == null)
            throw new HorariosException("Aluno não existe");

        Horario                  horario = aluno.getHorario();
        Map<String, Set<String>> turnos  = horario.getNomesDeTurnos();
        return turnos;
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

    public Set<String> obterAlunosDeCurso(String idCurso) throws HorariosException {
        Curso curso = this.cursos.get(idCurso);
        if (curso == null)
            throw new HorariosException("Curso não existe");

        Set<String> numeros = curso.getNumerosDeAlunos();
        return numeros;
    }

    public void eliminarDadosCurso(String idCurso) throws HorariosException {
        this.eliminarAlunosDeCurso(idCurso);
        this.eliminarUCsDeCurso(idCurso);
    }

    private void eliminarAlunosDeCurso(String idCurso) throws HorariosException {
        Curso curso = this.cursos.get(idCurso);
        if (curso == null)
            throw new HorariosException("Curso não existe");

        Collection<String> alunosCurso = curso.getNumerosDeAlunos();
        curso.removerAlunos();
        this.cursos.put(idCurso, curso);

        for (String aluno : alunosCurso)
            this.alunos.remove(aluno);
    }

    private void eliminarUCsDeCurso(String idCurso) throws HorariosException {
        Curso curso = this.cursos.get(idCurso);
        if (curso == null)
            throw new HorariosException("Curso não existe");

        Set<String> ucsCurso = curso.getNomesDeUCs();
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
        Curso curso = this.cursos.get(idCurso); // Já sabemos que o curso não é null

        Map<String, Sala>        readOnlySalas = Collections.unmodifiableMap(this.salas);
        Map<String, UC>          novasUCs      = new HashMap<String, UC>();
        Map<String, JsonElement> json          = this.lerJson(caminhoFicheiro);

        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            String      nome    = entry.getKey();
            JsonElement element = entry.getValue();
            UC          uc      = new UC(element, readOnlySalas);

            novasUCs.put(nome, uc);
            curso.adicionarUC(uc);
        }

        this.ucs.putAll(novasUCs);
        this.cursos.put(idCurso, curso);
    }

    public Set<String> obterUCsDeCurso(String idCurso) throws HorariosException {
        Curso curso = this.cursos.get(idCurso);
        if (curso == null)
            throw new HorariosException("Curso não existe");

        Set<String> nomes = curso.getNomesDeUCs();
        return nomes;
    }

    public Set<String> obterTurnosDeUC(String nomeUC) throws HorariosException {
        UC uc = this.ucs.get(nomeUC);
        if (uc == null)
            throw new HorariosException("UC não existe");

        Map<String, Turno> praticos      = uc.getPraticos();
        Set<String>        nomesPraticos = praticos.keySet();

        Map<String, Turno> teoricos      = uc.getTeoricos();
        Set<String>        nomesTeoricos = teoricos.keySet();

        nomesPraticos.addAll(nomesTeoricos);
        return nomesPraticos;
    }

    public boolean verificarCursoTemAlunos(String idCurso) throws HorariosException {
        Curso curso = this.cursos.get(idCurso);
        if (curso == null)
            throw new HorariosException("Curso não existe");

        boolean res = curso.contemAlunos();
        return res;
    }

    public void importarAlunos(String caminhoFicheiro, String idCurso) throws HorariosException {
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
            curso.adicionarAluno(aluno);
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
        curso.adicionarAluno(aluno);
        this.cursos.put(idCurso, curso);
    }

    public void registarUCsDeAluno(String numeroAluno, Set<String> nomeUCs)
        throws HorariosException {

        Aluno aluno = this.alunos.get(numeroAluno);
        if (aluno == null)
            throw new HorariosException("Aluno não existe");

        Set<UC> ucs = new HashSet<UC>();
        for (String nome : nomeUCs) {
            UC uc = this.ucs.get(nomeUCs);
            if (uc == null)
                throw new HorariosException("UC " + nome + " não existe");

            ucs.add(uc);
        }

        aluno.setUCs(ucs);
        this.alunos.put(numeroAluno, aluno);
    }

    public void gerarHorarios(String idCurso) throws HorariosException {
        Collection<String> nomesUCs = this.obterUCsDeCurso(idCurso);
        GeradorDeHorarios  gerador  = new GeradorDeHorarios(nomesUCs);

        Curso             curso = this.cursos.get(idCurso); // Já sabemos que o curso não é null
        Collection<Aluno> valoresAlunos = curso.getAlunos();

        for (Aluno aluno : valoresAlunos)
            gerador.adicionarAluno(aluno);

        Map<String, UC> readOnlyUCs = Collections.unmodifiableMap(this.ucs);

        try {
            Map<String, Horario>            horarios = gerador.run(readOnlyUCs);
            Set<Map.Entry<String, Horario>> entradas = horarios.entrySet();

            for (Map.Entry<String, Horario> entrada : entradas) {
                String  numero  = entrada.getKey();
                Horario horario = entrada.getValue();

                Aluno aluno = this.alunos.get(numero);
                aluno.setHorario(horario);
                this.alunos.put(numero, aluno);
            }
        } catch (IOException e) {
            throw new HorariosException(e.getMessage());
        } catch (InterruptedException e) {
            throw new HorariosException(e.getMessage());
        } finally {
            this.cursos.put(idCurso, curso);
        }
    }

    public Collection<Sobreposicao> procurarSobreposicoes(String idCurso) throws HorariosException {
        Curso curso = this.cursos.get(idCurso);
        if (curso == null)
            throw new HorariosException("Curso não existe");

        Set<Aluno>               alunos = curso.getAlunos();
        Collection<Sobreposicao> ret    = new ArrayList<Sobreposicao>();
        for (Aluno aluno : alunos) {
            Collection<Sobreposicao> sa = aluno.procurarSobreposicoes();
            ret.addAll(sa);
        }

        return ret;
    }

    public boolean validarHorario(String numeroAluno, Map<String, Set<String>> horario)
        throws HorariosException {

        Aluno aluno = this.alunos.get(numeroAluno);
        if (aluno == null)
            throw new HorariosException("Aluno não existe");

        Horario horarioObj = new Horario(horario);
        boolean res        = horarioObj.validar(aluno);
        return res;
    }

    public void armazenarHorario(String                   idCurso,
                                 String                   numeroAluno,
                                 Map<String, Set<String>> horario) throws HorariosException {

        Aluno aluno = this.alunos.get(numeroAluno);
        if (aluno == null)
            throw new HorariosException("Aluno não existe");

        Horario horarioObj = new Horario(horario);
        aluno.setHorario(horarioObj);
        this.alunos.put(numeroAluno, aluno);

        Curso curso = this.cursos.get(idCurso);
        if (curso == null)
            throw new HorariosException("Curso não existe");

        this.cursos.put(idCurso, curso);
    }

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
