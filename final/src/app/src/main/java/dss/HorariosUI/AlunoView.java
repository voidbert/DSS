package dss.HorariosUI;

import java.util.NoSuchElementException;
import java.util.Scanner;

import dss.HorariosLN.LNException;

public class AlunoView implements View{
    private AlunoController controlador;

    public AlunoView(AlunoController controlador) {
        this.controlador = controlador;
    }

    private void visualizarHorario() {
        try {
            System.out.println(this.controlador.obterHorario());
        } catch (LNException e){
            System.err.println(e.getMessage());
        }
    }

    public View run() {
        boolean[]   exitRequest = { false }; // Array wrapper to allow for lambda modification
        MenuEntry[] entries =
            {new MenuEntry("Visualizar Horário", i -> {this.visualizarHorario();}),
            new MenuEntry("Voltar atrás", i -> { exitRequest[0] = true;})};

        try {
            do {
                new Menu(entries, new Scanner(System.in)).run();
            } while (!exitRequest[0]);
        } catch (NoSuchElementException e) {} // System.in closed

        return new IniciarSessaoView(new IniciarSessaoController(this.controlador.getModelo()));
    }
}
