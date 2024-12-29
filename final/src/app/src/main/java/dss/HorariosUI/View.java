package dss.HorariosUI;

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
        } catch (LNException e) {
            System.err.println(e.getMessage());
        }
    }

    public void run() {
        iniciarSessao();
    }
}
