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

package dss;

import java.util.Arrays;
import java.util.List;

import dss.HorariosLN.HorariosLNFacade;
import dss.HorariosLN.IHorariosLN;

public class Program {
    public static void main(String[] args) throws Exception {
        IHorariosLN horarios = new HorariosLNFacade();
        horarios.iniciarSessao("dc@dc.pt", "atuapass");
        System.out.println(horarios.obterIdCursoDiretorAutenticado());
        horarios.terminarSessao();
        horarios.iniciarSessao("a104348@alunos.uminho.pt", "passdealuno");
        System.out.println(horarios.obterNumeroAlunoAutenticado());

        List<String> alunos = Arrays.asList("a100", "a200", "a600");
        horarios.gerarCredenciaisDeAlunos(alunos);

        horarios.eliminarCredenciaisDeAlunos(Arrays.asList("a500"));

        System.out.println(horarios.notificarAlunos(Arrays.asList("a100")));
    }
}
