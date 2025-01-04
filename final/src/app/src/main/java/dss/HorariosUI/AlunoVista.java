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

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;

public class AlunoVista implements Vista {
    private AlunoControlador controlador;

    public AlunoVista(AlunoControlador controlador) {
        this.controlador = controlador;
    }

    private void visualizarHorario() {
        try {
            Map<String, Set<String>> horario = this.controlador.obterHorario();
            for (Map.Entry<String, Set<String>> entry : horario.entrySet()) {
                String uc = entry.getKey();

                for (String turno : entry.getValue()) {
                    System.out.println(uc + " " + turno);
                }
            }
            System.out.println();
        } catch (UIException e) {
            System.err.println(e.getMessage());
        }
    }

    private void terminarSessao() {
        this.controlador.terminarSessao();
    }

    public Vista run() {
        boolean[] sair = { false };
        MenuEntry[] entradas = { new MenuEntry("Visualizar Horário", i -> this.visualizarHorario()),
                                 new MenuEntry("Terminar sessão", i -> {
                                     this.terminarSessao();
                                     sair[0] = true;
                                 }) };

        try {
            do {
                new Menu(entradas, new Scanner(System.in)).run();
            } while (!sair[0]);
        } catch (NoSuchElementException e) {}

        return new IniciarSessaoVista(new IniciarSessaoControlador(this.controlador.obterModelo()));
    }
}
