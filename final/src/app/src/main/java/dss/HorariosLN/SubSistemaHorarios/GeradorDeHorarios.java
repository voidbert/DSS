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

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class GeradorDeHorarios {
    private Map<String, Integer> assocUCs;
    private Map<String, String>  assocUCsInverso;

    private Set<String>                     variaveis;
    private Collection<Collection<String>>  frequencias;
    private List<String>                    sobreposicoes1;
    private List<String>                    sobreposicoes2;
    private Map<String, Collection<String>> capacidadesVars;
    private Map<String, Integer>            capacidadesVals;

    public GeradorDeHorarios(Collection<String> ucs) {
        this.assocUCs        = new HashMap<String, Integer>();
        this.assocUCsInverso = new HashMap<String, String>();

        this.variaveis       = new HashSet<String>();
        this.frequencias     = new ArrayList<Collection<String>>();
        this.sobreposicoes1  = new ArrayList<String>();
        this.sobreposicoes2  = new ArrayList<String>();
        this.capacidadesVars = new HashMap<String, Collection<String>>();
        this.capacidadesVals = new HashMap<String, Integer>();

        int i = 0;
        for (String uc : ucs) {
            assocUCs.put(uc, i);
            assocUCsInverso.put(Integer.toString(i), uc);
            i++;
        }
    }

    public void adicionarAluno(Aluno aluno) {
        Collection<UC> ucs = aluno.getUCs();

        List<Turno> turnosAluno = new ArrayList<Turno>();
        List<UC>    ucsAluno    = new ArrayList<UC>();

        for (UC uc : ucs) {
            Collection<Turno> praticos = uc.getPraticos().values();
            this.adicionarFrequencias(aluno, uc, praticos);

            Collection<Turno> teoricos = uc.getTeoricos().values();
            this.adicionarFrequencias(aluno, uc, teoricos);

            Collection<Turno> todos = new ArrayList<Turno>();
            todos.addAll(praticos);
            todos.addAll(teoricos);

            turnosAluno.addAll(todos);
            for (Turno turno : todos)
                ucsAluno.add(uc);

            this.adicionarCapacidades(aluno, uc, todos);
        }

        this.adicionarSobreposicoes(aluno, ucsAluno, turnosAluno);
    }

    private void adicionarFrequencias(Aluno aluno, UC uc, Collection<Turno> turnos) {
        Collection<String> variaveis = new ArrayList<String>();
        for (Turno turno : turnos) {
            String var = this.codificarVariavel(aluno, uc, turno);
            variaveis.add(var);
        }

        boolean vazio = variaveis.isEmpty();
        if (!vazio)
            frequencias.add(variaveis);
    }

    private void adicionarCapacidades(Aluno aluno, UC uc, Collection<Turno> turnos) {
        for (Turno turno : turnos) {
            String nome       = turno.getNome();
            String nomeUC     = uc.getNome();
            String nomeLimite = nomeUC + "_" + nome;

            Collection<String> inscritos = this.capacidadesVars.get(nomeLimite);
            if (inscritos == null) {
                inscritos = new ArrayList<String>();
                this.capacidadesVars.put(nomeLimite, inscritos);

                int capacidade = turno.getCapacidade();
                this.capacidadesVals.put(nomeLimite, capacidade);
            }

            String var = this.codificarVariavel(aluno, uc, turno);
            inscritos.add(var);
        }
    }

    private void adicionarSobreposicoes(Aluno aluno, List<UC> ucs, List<Turno> turnos) {
        int numeroTurnos = turnos.size();
        for (int i = 0; i < numeroTurnos; ++i) {
            Turno  t1    = turnos.get(i);
            UC     uc1   = ucs.get(i);
            String nome1 = t1.getNome();

            for (int j = 0; j < numeroTurnos; ++j) {
                Turno  t2    = turnos.get(j);
                UC     uc2   = ucs.get(j);
                String nome2 = t2.getNome();

                boolean s = t1.sobrepoe(t2);

                if (s && nome1.compareTo(nome2) < 0) {
                    String var1 = this.codificarVariavel(aluno, uc1, t1);
                    String var2 = this.codificarVariavel(aluno, uc2, t2);

                    this.sobreposicoes1.add(var1);
                    this.sobreposicoes2.add(var2);
                    this.variaveis.add("SOB_" + var1 + "_" + var2);
                }
            }
        }
    }

    private String codificarVariavel(Aluno aluno, UC uc, Turno turno) {
        String  nomeUC   = uc.getNome();
        Integer numeroUC = this.assocUCs.get(nomeUC);
        String  ret      = aluno.getNumero() + "_" + numeroUC.toString() + "_" + turno.getNome();
        this.variaveis.add(ret);
        return ret;
    }

    public Map<String, Horario> run(Map<String, UC> ucs) throws IOException, InterruptedException {
        PrintWriter writer = new PrintWriter("/tmp/modelo.mod");
        String      modelo = this.toString();
        writer.print(modelo);
        writer.close();

        ProcessBuilder builder = new ProcessBuilder("glpsol",
                                                    "--pcost",
                                                    "--math",
                                                    "--tmlim",
                                                    "120",
                                                    "/tmp/modelo.mod",
                                                    "--log",
                                                    "/tmp/sol.txt");
        builder.redirectOutput(ProcessBuilder.Redirect.DISCARD);
        builder.redirectError(ProcessBuilder.Redirect.DISCARD);

        Process process = builder.start();
        process.waitFor();

        Path                 output = Paths.get("/tmp/sol.txt");
        List<String>         linhas = Files.readAllLines(output);
        Map<String, Horario> ret    = this.parseResultado(linhas, ucs);
        return ret;
    }

    private Map<String, Horario> parseResultado(List<String> linhas, Map<String, UC> ucs) {
        Map<String, Horario> ret = new HashMap<String, Horario>();

        for (String linha : linhas) {
            boolean eVariavel     = linha.contains(".val =");
            boolean eSobreposicao = linha.startsWith("SOB_");

            if (eVariavel && !eSobreposicao) {
                int      fimVariavel = linha.indexOf(".");
                String   variavel    = linha.substring(0, fimVariavel);
                String[] campos      = variavel.split("_");

                String aluno     = campos[0];
                String ucNumero  = campos[1];
                String ucNome    = this.assocUCsInverso.get(ucNumero);
                String nomeTurno = campos[2];

                int inicioValor = linha.indexOf("=");
                inicioValor++;
                String valor = linha.substring(inicioValor);

                Horario horario = ret.get(aluno);
                if (horario == null) {
                    horario = new Horario();
                    ret.put(aluno, horario);
                }

                if (valor.equals(" 1")) {
                    UC    uc    = ucs.get(ucNome);
                    Turno turno = uc.getTurno(nomeTurno);
                    horario.adicionarTurno(uc, turno);
                }
            }
        }

        return ret;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        this.exportarVariaveis(builder);
        this.exportarObjetivo(builder);
        this.exportarFrequencias(builder);
        this.exportarSobreposicoes(builder);
        this.exportarCapacidades(builder);

        builder.append("solve;\ndisplay ");
        String display = String.join(", ", this.variaveis);
        builder.append(display);
        builder.append(";\nend;\n");
        return builder.toString();
    }

    private void exportarVariaveis(StringBuilder builder) {
        for (String var : this.variaveis) {
            builder.append("var ");
            builder.append(var);
            builder.append(" binary;\n");
        }
    }

    private void exportarObjetivo(StringBuilder builder) {
        builder.append("minimize obj: ");

        boolean primeiro = true;
        for (String var : this.variaveis) {
            if (var.startsWith("SOB_")) {
                if (primeiro) {
                    primeiro = false;
                } else {
                    builder.append(" + ");
                }

                builder.append(var);
            }
        }
        builder.append(";\n");
    }

    private void exportarFrequencias(StringBuilder builder) {
        int indice = 0;
        for (Collection<String> vars : this.frequencias) {
            builder.append("s.t. f");
            builder.append(indice);
            builder.append(": ");
            String soma = String.join(" + ", vars);
            builder.append(soma);
            builder.append(" = 1;\n");

            indice++;
        }
    }

    private void exportarSobreposicoes(StringBuilder builder) {
        int indice        = 0;
        int sobreposicoes = this.sobreposicoes1.size();
        for (int i = 0; i < sobreposicoes; ++i) {
            builder.append("s.t. s");
            builder.append(indice);
            builder.append(": ");

            String var1 = this.sobreposicoes1.get(i);
            String var2 = this.sobreposicoes2.get(i);

            builder.append("SOB_" + var1 + "_" + var2);
            builder.append(" >= ");
            String soma = String.join(" + ", var1, var2);
            builder.append(soma);
            builder.append(" - 1;\n");

            indice++;
        }
    }

    private void exportarCapacidades(StringBuilder builder) {
        int indice = 0;
        for (String ucTurno : this.capacidadesVars.keySet()) {
            builder.append("s.t. c");
            builder.append(indice);
            builder.append(": ");

            String             limite = this.capacidadesVals.get(ucTurno).toString();
            Collection<String> vars   = this.capacidadesVars.get(ucTurno);
            String             soma   = String.join(" + ", vars);

            builder.append(soma);
            builder.append(" <= ");
            builder.append(limite);
            builder.append(";\n");

            indice++;
        }
    }
}
