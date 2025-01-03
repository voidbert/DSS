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
            this.nextView = controlador.nextView();
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
