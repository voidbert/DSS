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

class Mailer {
    public Mailer() {}

    public boolean enviar(String destino, String assunto, String mensagem) {
        return true; // Mailer de stub (para não enviarmos mails a ninguém sem querer)
    }

    public boolean enviarCredenciais(Utilizador utilizador) {
        String email    = utilizador.getEmail();
        String password = utilizador.getPassword();
        String mensagem = "A sua password é " + password;

        boolean sucesso = this.enviar(email, "Password para horários", mensagem);
        return sucesso;
    }

    public Object clone() {
        return new Mailer();
    }

    public boolean equals(Object o) {
        return o != null && this.getClass() == o.getClass();
    }

    public String toString() {
        return "Mailer()";
    }
}
