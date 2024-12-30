package dss.HorariosUI;

import java.util.NoSuchElementException;
import java.util.Scanner;

import dss.HorariosLN.LNException;

public class IniciarSessaoView implements View {
    private IniciarSessaoController controlador;
    private View nextView;

    public IniciarSessaoView(IniciarSessaoController controlador) {
        this.controlador = controlador;
        this.nextView = null;
    }

    private void iniciarSessao() {
        Menu menu = new Menu();
        String email = menu.readString("Email > ");
        String password = menu.readString("Palavra passe > ");

        try {
            this.controlador.iniciarSessao(email, password);
            System.out.println("Sessão iniciada com sucesso!");
            /* Ask Controller for right view!! */
            this.nextView = new AlunoView(new AlunoController(this.controlador.getModelo()));
        } catch (LNException e) {
            System.err.println(e.getMessage());
        }

    }

    private void terminarSessao() {
        try {
            this.controlador.terminarSessao();
            System.out.println("Sessão terminada com sucesso!");
        } catch (LNException e) {
            System.err.println(e.getMessage());
        }
    }

    public View run() {
        boolean[]   exitRequest = { false }; // Array wrapper to allow for lambda modification
        MenuEntry[] entries = {new MenuEntry("Iniciar Sessão", i -> {this.iniciarSessao();}),
                            new MenuEntry("Terminar Sessão", i -> {this.terminarSessao();}),
                            new MenuEntry("Sair", i -> { exitRequest[0] = true;})};

        try {
            do {
                new Menu(entries, new Scanner(System.in)).run();
            } while (!exitRequest[0] && this.nextView == null);
        } catch (NoSuchElementException e) {} // System.in closed

        return this.nextView;
    }
}
