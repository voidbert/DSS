package dss.HorariosUI;

import java.util.*;

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
            System.out.println("Importanção de dados abortada...");
        }
    }

    private void importarAlunosEInscricoes(){
        try {
            this.controlador.verificarCursoTemAlunos();
        }catch(LNException e){
            boolean[] sobrescreverAlunosEInscricoes = { false };
            MenuEntry[] entries = {new MenuEntry("Sobrescrever Alunos e Inscrições existentes", i -> { sobrescreverAlunosEInscricoes[0] = true; }),
                    new MenuEntry("Abortar Importação de Alunos e Inscrições", i -> { })};

            new Menu(entries, new Scanner(System.in)).run();

            if (!sobrescreverAlunosEInscricoes[0]) {
                System.out.println("Importação de Alunos e Inscrições abortada...");
                return;
            }
        }

        Menu menu = new Menu();
        String caminho = menu.readString("Caminho para o ficheiro de Alunos e Inscrições: ");

        try{
            this.controlador.importarAlunosEInscricoes(caminho);
            System.out.println("Importanção de alunos e inscrições foi realizada com sucesso!");
        } catch (LNException e) {
            System.out.println("Importação de Alunos e Inscrições abortada...");
        }
    }

    private void adicionarAluno() {
        Menu menu = new Menu();
        String numAluno = menu.readString("Numero de aluno: ");

        try {
            this.controlador.verificarExistenciaAluno(numAluno);

            List<MenuEntry> entriesList = new ArrayList<>();
            Set<String> ucsCurso = this.controlador.obterListaUCs();
            Set<String> listaInscricoes = new HashSet<>();
            boolean[] sair = { false };

            System.out.print("Inscrever aluno na UC ...");
            for (String key : ucsCurso) {
                entriesList.add(new MenuEntry(key, i -> { listaInscricoes.add(key); }));
            }
            entriesList.add(new MenuEntry("Concluir Operação", i -> { sair[0] = true; }));

            MenuEntry[] entries = new MenuEntry[entriesList.size()];
            for (int i = 0; i < entriesList.size(); i++) {
                entries[i] = entriesList.get(i);
            }

            menu = new Menu(entries, new Scanner(System.in));

            do {
                menu.run();
            } while (!sair[0]);

            this.controlador.adicionarAluno(numAluno, listaInscricoes);
        } catch (LNException e){
            System.err.println(e.getMessage());
        }
    }

    private void gerarHorariosAutomaticamente() {
        try {
            System.out.println("A gerar horarios automaticamente...");
            Collection<Sobreposicao> sobreposicoes = this.controlador.gerarHorariosAutomaticamente();

            if (sobreposicoes.size() < 1) {
                System.out.println("Foram detetadas sobreposições nos horários gerados :");

                for (Sobreposicao sobreposicao : sobreposicoes) {
                    System.out.println(sobreposicao.toString());
                }
            }

            this.controlador.armazenarHorarios();
            System.out.println("Horarios gerados com sucesso!");
        } catch (LNException e) {
            System.err.println(e.getMessage());
        }
    }

    private MenuEntry[] gerarEntradasMenuUCs(Map<String, Set<String>> horario,
                                             String[] UC,
                                             boolean[] sair,
                                             Menu menuTurnos) {

        List<MenuEntry> entries = new ArrayList<>();

        for (String key : horario.keySet()) {
            entries.add(new MenuEntry(key, i -> {
                String turnos;
                if (horario.get(key).size() < 1) {
                    turnos = "Não existem turnos associados";
                } else {
                    turnos = horario.get(key).toString();
                }

                System.out.println("Turnos associados à UC " + key + " :\n" + turnos);
                UC[0] = key;

                menuTurnos.run();
            }));
        }

        entries.add(new MenuEntry("Adicionar UC", i -> {
            String novaUC = menuTurnos.readString("Insira UC a adicionar : ");
            horario.put(novaUC, new HashSet<String>());
        }));

        entries.add(new MenuEntry("Remover UC", i -> {
            String removerUC = menuTurnos.readString("Insira UC a remover : ");
            horario.remove(removerUC);
        }));

        entries.add(new MenuEntry("Concluir Operação", i -> { sair[0] = true; }));

        MenuEntry[] UCEntriesArray = new MenuEntry[entries.size()];
        for (int i = 0; i < entries.size(); i++) {
            UCEntriesArray[i] = entries.get(i);
        }

        return UCEntriesArray;
    }

    private void modificarHorario() {
        Menu menu = new Menu();
        String numAluno = menu.readString("Número do aluno com horário para ser modificado : ");

        try {
            Map<String, Set<String>> horario = this.controlador.obterHorarioAluno(numAluno);
            String[] UC = { null };
            boolean[] sair = { false };

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
                new MenuEntry("Voltar atrás", i -> { sair[0] = true; })
            };

            Menu turnosMenu = new Menu(turnosEntries, new Scanner(System.in));
            /* */

            do {
                MenuEntry[] UCEntriesArray = gerarEntradasMenuUCs(horario, UC, sair, turnosMenu);
                new Menu(UCEntriesArray, new Scanner(System.in)).run();
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
                System.out.println("A operação foi realizada com sucesso!");
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
