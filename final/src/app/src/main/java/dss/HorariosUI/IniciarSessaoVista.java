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

public class IniciarSessaoVista implements Vista {
    private IniciarSessaoControlador controlador;
    private Vista proximaVista;

    public IniciarSessaoVista(IniciarSessaoControlador controlador) {
        this.controlador = controlador;
        this.proximaVista = null;
    }

    private void iniciarSessao() {
        Menu menu = new Menu();
        String email = menu.readString("Email > ");
        String palavraPasse = menu.readString("Palavra passe > ");

        try {
            this.controlador.iniciarSessao(email, palavraPasse);
            System.out.println("Sessão iniciada com sucesso!");
            this.proximaVista = controlador.proximaVista();
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

    public Vista run() {
        boolean[]   sair = { false };
        MenuEntry[] entradas = {new MenuEntry("Iniciar Sessão", i -> {this.iniciarSessao();}),
                            new MenuEntry("Terminar Sessão", i -> {this.terminarSessao();}),
                            new MenuEntry("Sair", i -> { sair[0] = true;})};

        try {
            do {
                new Menu(entradas, new Scanner(System.in)).run();
            } while (!sair[0] && this.proximaVista == null);
        } catch (NoSuchElementException e) {}

        return this.proximaVista;
    }
}
