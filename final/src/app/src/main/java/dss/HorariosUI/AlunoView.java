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
