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
import java.util.Set;

import dss.HorariosLN.IHorariosLN;
import dss.HorariosLN.LNException;

public class AlunoControlador extends Controlador {
    public AlunoControlador(IHorariosLN modelo) {
        super(modelo);
    }

    public Map<String, Set<String>> obterHorario() throws UIException {
        try {
            String res = this.obterModelo().obterNumeroAlunoAutenticado();
            return this.obterModelo().obterHorario(res);
        } catch (LNException e) {
            throw new UIException(e.getMessage());
        }
    }

    public void terminarSessao() {
        try {
            this.obterModelo().terminarSessao();
        } catch (LNException e) {}
    }
}
