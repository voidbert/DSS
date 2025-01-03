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

import dss.HorariosLN.IHorariosLN;
import dss.HorariosLN.LNException;

public class IniciarSessaoController extends Controller {
    public IniciarSessaoController(IHorariosLN modelo) {
        super(modelo);
    }

    public void iniciarSessao(String email, String password) throws LNException {
        this.getModelo().iniciarSessao(email, password);
    }

    public void terminarSessao() throws LNException {
        this.getModelo().terminarSessao();
    }

    public View nextView() {
        try {
            this.getModelo().obterNumeroAlunoAutenticado();
            return new AlunoView(new AlunoController(this.getModelo()));
        } catch (LNException e) {
            return new DiretorCursoView(new DiretorCursoController(this.getModelo()));
        }
    }
}
