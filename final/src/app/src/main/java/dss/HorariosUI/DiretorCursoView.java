package dss.HorariosUI;

import java.util.NoSuchElementException;
import java.util.Scanner;

import dss.HorariosLN.LNException;

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
            System.out.println(e.getMessage());
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

    private void importarAlunosEInscricoes() {
        this.controlador.importarAlunosEInscricoes();
    }

    private void definirPreferenciasUC() {
        this.controlador.definirPreferenciasUC();
    }

    private void adicionarAluno() {
        this.controlador.adicionarAluno();
    }

    private void gerarHorariosAutomaticamente() {
        this.controlador.gerarHorariosAutomaticamente();
    }

    private void modificarHorario() {
        this.controlador.modificarHorario();
    }

    private void publicarHorarios() {
        this.controlador.publicarHorarios();
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
