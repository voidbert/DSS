package dss.HorariosUI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;

import dss.HorariosLN.LNException;
import dss.HorariosLN.SubSistemaHorarios.Sobreposicao;

public class DiretorCursoView implements View{
    private DiretorCursoController controlador;

    public DiretorCursoView(DiretorCursoController controlador) {
        this.controlador = controlador;
    }

    private void reiniciarSemestre() {
        try {
            this.controlador.reiniciarSemestre();
            System.out.println("Dados associados ao curso eliminados com sucesso!");
        } catch (LNException e) {
            System.err.println(e.getMessage());
        }
    }

    private void importarUnidadesCurricularesTurnos() {
        try {
            this.controlador.verificarCursoTemUCs();
        } catch (LNException e) {
            boolean[] sobrescreverDados = { false };
            MenuEntry[] entries = {new MenuEntry("Sobrescrever Dados Existentes", i -> { sobrescreverDados[0] = true; }),
                                new MenuEntry("Abortar Importação de Dados", i -> { })};

            new Menu(entries, new Scanner(System.in)).run();

            if (sobrescreverDados[0] == false) {
                System.out.println("Importação de dados abortada...");
                return;
            }
        }

        Menu menu = new Menu();
        String caminho = menu.readString("Caminho para o ficheiro de dados : ");

        try {
            this.controlador.importarUnidadesCurricularesTurnos(caminho);
            System.out.println("A importação de dados foi realizada com sucesso!");
        } catch (LNException e) {
            System.err.println("Importanção de dados abortada...");
        }
    }

    private void importarAlunosEInscricoes() {
        try {
            this.controlador.importarAlunosEInscricoes();
        } catch (LNException e) {
            System.err.println(e.getMessage());
        }
    }

    private void definirPreferenciasUC() {
        this.controlador.definirPreferenciasUC();
    }

    private void adicionarAluno() {
        this.controlador.adicionarAluno();
    }

    private void gerarHorariosAutomaticamente() {
        try {
            System.out.println("Im here!");
            Collection<Sobreposicao> sobreposicoes = this.controlador.gerarHorariosAutomaticamente();

            if (sobreposicoes.size() < 1) {
                System.out.println("Foram detetadas sobreposições nos horários gerados :");

                for (Sobreposicao sobreposicao : sobreposicoes) {
                    System.out.println(sobreposicao.toString());
                }
            }

            this.controlador.armazenarHorarios();
        } catch (LNException e) {
            System.err.println(e.getMessage());
        }
    }

    private void modificarHorario() {
        Menu menu = new Menu();
        String numAluno = menu.readString("Número do aluno com horário para ser modificado : ");

        try {
            Map<String, Set<String>> horario = this.controlador.obterHorarioAluno(numAluno);

            List<MenuEntry> UCEntries = new ArrayList<>();
            String[] UC = { null };

            /* Preparar menu que permite a alteração de turnos */
            MenuEntry[] turnosEntries = {
                new MenuEntry("Adicionar Turno", i -> {
                    String novoTurno = menu.readString("Insira turno a ser adicionado :");
                    horario.get(UC[0]).add(novoTurno);
                }),
                new MenuEntry("Remover Turno", i -> {
                    String turnoARemover = menu.readString("Insira turno a ser removido :");
                    horario.get(UC[0]).remove(turnoARemover);
                }),
                new MenuEntry("Cancelar Operação", i -> { })
            };

            Menu turnosMenu = new Menu(turnosEntries, new Scanner(System.in));
            /* */

            for (String key : horario.keySet()) {
                UCEntries.add(new MenuEntry(key, i -> {
                    System.out.println("Turnos associados à UC " + key + " :\n" + horario.get(key).toString());
                    UC[0] = key;
                    turnosMenu.run();
                }));
            }
            boolean[] sair = { false };
            UCEntries.add(new MenuEntry("Adicionar UC", i -> {
                String novaUC = menu.readString("Insira UC a adicionar :");
                horario.put(novaUC, null);
            }));
            UCEntries.add(new MenuEntry("Concluir Operação", i -> { sair[0] = true; }));

            MenuEntry[] UCEntriesArray = new MenuEntry[UCEntries.size()];
            for (int i = 0; i < UCEntries.size(); i++) {
                UCEntriesArray[i] = UCEntries.get(i);
            }

            Menu UCMenu = new Menu(UCEntriesArray, new Scanner(System.in));

            do {
                UCMenu.run();
            } while (!sair[0]);

            this.controlador.atualizarHorario(numAluno, horario);
        } catch (LNException e) {
            System.err.println(e.getMessage());
        }
    }

    private void publicarHorarios() {
        try {
            Collection<String> falhas = this.controlador.publicarHorarios();
            if (falhas.size() < 1) {
                return;
            }

            System.out.println("As seguintes mensagens de correio eletrónico não puderam ser enviadas :");
            for (String email : falhas) {
                System.out.println(email);
            }
        } catch (LNException e) {
            System.err.println(e.getMessage());
        }
    }

    public View run() {
        boolean[]   exitRequest = { false }; // Array wrapper to allow for lambda modification
        MenuEntry[] entries =
            {new MenuEntry("Reiniciar Semestre", i -> { this.reiniciarSemestre(); }),
            new MenuEntry("Importar Unidades Curriculares e Turnos", i -> { this.importarUnidadesCurricularesTurnos(); }),
            new MenuEntry("Importar Alunos e Inscrições", i -> { this.importarAlunosEInscricoes(); }),
            new MenuEntry("Definir Preferências de uma UC", i -> { this.definirPreferenciasUC(); }),
            new MenuEntry("Adicionar Aluno", i -> { this.adicionarAluno(); }),
            new MenuEntry("Gerar Horários", i -> { this.gerarHorariosAutomaticamente(); }),
            new MenuEntry("Modificar Horário", i -> { this.modificarHorario(); }),
            new MenuEntry("Publicar Horários", i -> { this.publicarHorarios(); }),
            new MenuEntry("Voltar atrás", i -> { exitRequest[0] = true;})};

        try {
            do {
                new Menu(entries, new Scanner(System.in)).run();
            } while (!exitRequest[0]);
        } catch (NoSuchElementException e) {} // System.in closed

        return new IniciarSessaoView(new IniciarSessaoController(this.controlador.getModelo()));
    }
}
