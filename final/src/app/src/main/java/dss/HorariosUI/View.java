package dss.HorariosUI;

import java.util.NoSuchElementException;

import dss.HorariosLN.LNException;

public class View {
    Controller controller;

    public View(Controller c) {
        this.controller = c;
    }

    public void iniciarSessao() {
        UserInput input = new UserInput();
        String email = input.readString("Email > ");
        String password = input.readString("Palavra passe > ");

        try {
            this.controller.iniciarSessao(email, password);
            System.out.println("Sessão iniciada com sucesso!");
        } catch (LNException e) {
            System.err.println(e.getMessage());
        }
    }

    public void run() {
        boolean[]   exitRequest = { false }; // Array wrapper to allow for lambda modification
        MenuEntry[] entries = {new MenuEntry("Iniciar Sessão", i -> {this.iniciarSessao();}),
                            new MenuEntry("Sair", i -> { exitRequest[0] = true;})};

        try {
            do {
                new Menu(entries).run();
            } while (!exitRequest[0]);
        } catch (NoSuchElementException e) {} // System.in closed
    }
}
