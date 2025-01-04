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

package dss.HorariosUI;

import java.util.*;

public class DiretorCursoVista implements Vista {
    private DiretorCursoControlador controlador;

    public DiretorCursoVista(DiretorCursoControlador controlador) {
        this.controlador = controlador;
    }

    private void reiniciarSemestre() {
        try {
            this.controlador.reiniciarSemestre();
            System.out.println("Dados associados ao curso eliminados com sucesso!");
        } catch (UIException e) {
            System.err.println(e.getMessage());
        }
    }

    private void importarUnidadesCurricularesTurnos() {
        if (this.controlador.verificarCursoTemUCs()) {
            boolean[]   sobrescreverDados = { false };
            MenuEntry[] entradas          = { new MenuEntry("Sobrescrever dados existentes", i -> {
                                                  sobrescreverDados[0] = true;
                                     }), new MenuEntry("Abortar importação de dados", i -> {}) };

            new Menu(entradas, new Scanner(System.in)).run();

            if (!sobrescreverDados[0]) {
                System.out.println("Importação de dados abortada...");
                return;
            }
        }

        String caminho = (new Menu()).readString("Caminho para o ficheiro de dados: ");
        try {
            this.controlador.importarUnidadesCurricularesTurnos(caminho);
            System.out.println("A importação de dados foi realizada com sucesso!");
        } catch (UIException e) {
            System.err.println(e.getMessage() + ". Importanção de dados abortada...");
        }
    }

    private void importarAlunosEInscricoes() {
        if (!this.controlador.verificarCursoTemUCs()) {
            System.out.println("Ainda não foram importadas UCs e turnos");
            return;
        }

        if (this.controlador.verificarCursoTemAlunos()) {
            boolean[]   sobrescreverAlunosEInscricoes = { false };
            MenuEntry[] entradas = { new MenuEntry("Sobrescrever dados existentes", i -> {
                                         sobrescreverAlunosEInscricoes[0] = true;
                                     }), new MenuEntry("Abortar importação de dados", i -> {}) };

            new Menu(entradas, new Scanner(System.in)).run();

            if (!sobrescreverAlunosEInscricoes[0]) {
                System.out.println("Importação de dados abortada...");
                return;
            }
        }

        String caminho = (new Menu()).readString("Caminho para o ficheiro de dados: ");

        try {
            this.controlador.importarAlunosEInscricoes(caminho);
            System.out.println("Importação de dados foi realizada com sucesso!");
        } catch (UIException e) {
            System.err.println(e.getMessage() + ". Importação de dados abortada...");
        }
    }

    private void adicionarAluno() {
        Menu   menu     = new Menu();
        String numAluno = menu.readString("Numero de aluno: ");

        try {
            this.controlador.verificarExistenciaAluno(numAluno);

            List<MenuEntry> entradas        = new ArrayList<>();
            Set<String>     ucsCurso        = this.controlador.obterListaUCs();
            Set<String>     listaInscricoes = new HashSet<>();
            boolean[]       sair            = { false };

            System.out.print("\nAdicione as UC's nas quais o aluno se encontra inscrito:");
            for (String key : ucsCurso)
                entradas.add(new MenuEntry(key, i -> listaInscricoes.add(key)));
            entradas.add(new MenuEntry("Concluir Operação", i -> { sair[0] = true; }));

            menu = new Menu(entradas.toArray(new MenuEntry[0]), new Scanner(System.in));

            do {
                menu.run();
            } while (!sair[0]);

            this.controlador.adicionarAluno(numAluno, listaInscricoes);
            System.out.println("Adição de aluno realizada com sucesso!");
        } catch (UIException e) {
            System.err.println(e.getMessage() + " Operação cancelada.");
        }
    }

    private void gerarHorariosAutomaticamente() {
        try {
            System.out.println("A gerar horários automaticamente (isto vai demorar) ...");
            Collection<String> sobreposicoes = this.controlador.gerarHorariosAutomaticamente();

            if (sobreposicoes.size() != 0) {
                System.out.println("Foram detetadas sobreposições nos horários gerados: ");

                for (String sobreposicao : sobreposicoes)
                    System.out.println(sobreposicao);
            }

            System.out.println("Horários gerados com sucesso!");
        } catch (UIException e) {
            System.err.println(e.getMessage());
        }
    }

    private MenuEntry[] gerarEntradasMenuUCs(Map<String, Set<String>> horario,
                                             String[]                 UC,
                                             boolean[]                sair,
                                             Menu                     menuTurnos) {

        List<MenuEntry> entradas = new ArrayList<>();

        for (String chave : horario.keySet()) {
            entradas.add(new MenuEntry(chave, i -> {
                String turnos;
                if (horario.get(chave).size() < 1) {
                    turnos = "Não existem turnos associados";
                } else {
                    turnos = horario.get(chave).toString();
                }

                System.out.println("Turnos associados à UC " + chave + ": " + turnos);
                UC[0] = chave;

                menuTurnos.run();
            }));
        }

        entradas.add(new MenuEntry("Adicionar UC", i -> {
            String novaUC = menuTurnos.readString("Insira UC a adicionar: ");
            horario.put(novaUC, new HashSet<String>());
        }));

        entradas.add(new MenuEntry("Remover UC", i -> {
            String removerUC = menuTurnos.readString("Insira UC a remover: ");
            horario.remove(removerUC);
        }));

        entradas.add(new MenuEntry("Concluir Operação", i -> { sair[0] = true; }));

        return entradas.toArray(new MenuEntry[0]);
    }

    private void modificarHorario() {
        Menu menuGeral = new Menu();
        String numAluno = menuGeral.readString("Número do aluno com horário para ser modificado: ");

        try {
            Map<String, Set<String>> horario       = this.controlador.obterHorarioAluno(numAluno);
            String[]                 UCSelecionada = { null };
            boolean[]                sair          = { false };

            /* Preparar menu que permite a alteração de turnos */
            MenuEntry[] entradasTurnos = { new MenuEntry("Adicionar Turno", i -> {
                                               String novoTurno = menuGeral.readString(
                                                   "Insira turno a ser adicionado: ");
                                               horario.get(UCSelecionada[0]).add(novoTurno);
                                           }), new MenuEntry("Remover Turno", i -> {
                                               String turnoARemover  = menuGeral.readString(
                                                   "Insira turno a ser removido: ");
                                               horario.get(UCSelecionada[0]).remove(turnoARemover);
                                           }), new MenuEntry("Voltar atrás", i -> {
                                               sair[0] = true;
                                           }) };

            Menu menuTurnos = new Menu(entradasTurnos, new Scanner(System.in));

            do {
                MenuEntry[] entradas =
                    gerarEntradasMenuUCs(horario, UCSelecionada, sair, menuTurnos);
                new Menu(entradas, new Scanner(System.in)).run();
            } while (!sair[0]);

            this.controlador.atualizarHorario(numAluno, horario);
            System.out.println("Horário atualizado com sucesso!");
        } catch (UIException e) {
            System.err.println(e.getMessage());
        }
    }

    private void publicarHorarios() {
        try {
            Collection<String> falhas = this.controlador.publicarHorarios();
            if (falhas.size() == 0) {
                System.out.println("Horários publicados com sucesso!");
            } else {
                System.out.println("Os seguintes emails não puderam ser enviados: ");
                for (String email : falhas)
                    System.out.println(email);
            }
        } catch (UIException e) {
            System.err.println(e.getMessage());
        }
    }

    private void terminarSessao() {
        this.controlador.terminarSessao();
    }

    public Vista run() {
        boolean[]   sair     = { false };
        MenuEntry[] entradas = { new MenuEntry("Reiniciar Semestre", i -> this.reiniciarSemestre()),
                                 new MenuEntry("Importar Unidades Curriculares e Turnos",
                                               i -> this.importarUnidadesCurricularesTurnos()),
                                 new MenuEntry("Importar Alunos e Inscrições",
                                               i -> this.importarAlunosEInscricoes()),
                                 new MenuEntry("Adicionar Aluno", i -> this.adicionarAluno()),
                                 new MenuEntry("Gerar Horários",
                                               i -> this.gerarHorariosAutomaticamente()),
                                 new MenuEntry("Modificar Horário", i -> this.modificarHorario()),
                                 new MenuEntry("Publicar Horários", i -> this.publicarHorarios()),
                                 new MenuEntry("Terminar sessão", i -> {
                                     this.terminarSessao();
                                     sair[0] = true;
                                 }) };

        try {
            do {
                new Menu(entradas, new Scanner(System.in)).run();
            } while (!sair[0]);
        } catch (NoSuchElementException e) {}

        return new IniciarSessaoVista(new IniciarSessaoControlador(this.controlador.obterModelo()));
    }
}
