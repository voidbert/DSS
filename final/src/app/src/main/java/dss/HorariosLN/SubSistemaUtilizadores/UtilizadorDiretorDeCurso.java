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

package dss.HorariosLN.SubSistemaUtilizadores;

public class UtilizadorDiretorDeCurso extends Utilizador {
    private String idCurso;

    public UtilizadorDiretorDeCurso(String email, String password, String idCurso) {
        super(email, password);
        this.idCurso = idCurso;
    }

    public UtilizadorDiretorDeCurso(UtilizadorDiretorDeCurso utilizador) {
        super(utilizador.getEmail(), utilizador.getPassword());
        this.idCurso = utilizador.getIdCurso();
    }

    public String getIdCurso() {
        return this.idCurso;
    }

    public Object clone() {
        return new UtilizadorDiretorDeCurso(this);
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || this.getClass() != o.getClass())
            return false;

        UtilizadorDiretorDeCurso utilizador = (UtilizadorDiretorDeCurso) o;
        return super.equals(o) && this.idCurso.equals(utilizador.getIdCurso());
    }

    public String toString() {
        return String.format("UtilizadorDiretorDeCurso(email=%s, password=%s, idCurso=%s)",
                             this.getEmail(),
                             this.getPassword(),
                             this.idCurso);
    }
}
