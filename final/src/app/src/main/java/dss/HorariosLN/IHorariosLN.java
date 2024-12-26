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

package dss.HorariosLN;

import java.util.Collection;

public interface IHorariosLN {
    public void               iniciarSessao(String email, String password) throws LNException;
    public void               terminarSessao() throws LNException;
    public String             obterNumeroAlunoAutenticado() throws LNException;
    public String             obterIdCursoDiretorAutenticado() throws LNException;
    public void               eliminarCredenciaisDeAlunos(Collection<String> alunos);
    public void               gerarCredenciaisDeAlunos(Collection<String> alunos);
    public Collection<String> notificarAlunos(Collection<String> alunos);
}
