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

public class UtilizadorAluno extends Utilizador {
    private String numero;

    public UtilizadorAluno(String numero) {
        super(UtilizadorAluno.gerarEmail(numero));
        this.numero = numero;
    }

    public UtilizadorAluno(String numero, String password) {
        super(UtilizadorAluno.gerarEmail(numero), password);
        this.numero = numero;
    }

    public UtilizadorAluno(UtilizadorAluno utilizador) {
        super(utilizador);
        this.numero = utilizador.getNumero();
        throw new UnsupportedOperationException();
    }

    public String getNumero() {
        return this.numero;
    }

    public Object clone() {
        return new UtilizadorAluno(this);
    }

    public boolean equals(Object o) {
        return super.equals(o); // Mesmo email -> mesmo número
    }

    public String toString() {
        return String.format("UtilizadorAluno(numero=%s, password=%s)",
                             this.numero,
                             this.getPassword());
    }

    public static String gerarEmail(String numero) {
        return numero + "@alunos.uminho.pt";
    }
}
