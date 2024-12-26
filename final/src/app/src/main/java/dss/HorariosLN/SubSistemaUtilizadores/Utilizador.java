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

import org.apache.commons.lang3.RandomStringUtils;

public abstract class Utilizador {
    private String email;
    private String password;

    public Utilizador(String email) {
        this.email    = email;
        this.password = RandomStringUtils.randomAlphanumeric(8);
    }

    public Utilizador(String email, String password) {
        this.email    = email;
        this.password = password;
    }

    public Utilizador(Utilizador utilizador) {
        this.email    = utilizador.getEmail();
        this.password = utilizador.getPassword();
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }

    public boolean passwordCerta(String password) {
        boolean res = this.password.equals(password);
        return res;
    }

    public abstract Object clone();

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || this.getClass() != o.getClass())
            return false;

        Utilizador utilizador = (Utilizador) o;
        return this.email.equals(utilizador.getEmail()) &&
            this.password.equals(utilizador.getPassword());
    }
}
