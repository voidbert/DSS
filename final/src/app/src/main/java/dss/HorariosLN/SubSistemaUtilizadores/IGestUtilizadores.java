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

import java.util.Set;

public interface IGestUtilizadores {
    public void        iniciarSessao(String email, String password) throws UtilizadoresException;
    public void        terminarSessao() throws UtilizadoresException;
    public String      obterNumeroAlunoAutenticado() throws UtilizadoresException;
    public String      obterIdCursoDiretorAutenticado() throws UtilizadoresException;
    public void        eliminarCredenciaisDeAlunos(Set<String> alunos);
    public void        gerarCredenciaisDeAlunos(Set<String> alunos);
    public Set<String> notificarAlunos(Set<String> alunos);
}
