package dss.HorariosUI;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class DiretorCursoView implements View{
    private DiretorCursoController controlador;

    public DiretorCursoView(DiretorCursoController controlador) {
        this.controlador = controlador;
    }

    private void reiniciarSemestre() {
        this.controlador.reiniciarSemestre();
    }

    private void importarUnidadesCurricularesTurnos() {
        this.controlador.importarUnidadesCurricularesTurnos();
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
