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

package dss.HorariosLN.SubSistemaHorarios;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import dss.HorariosDL.UCDAO;

public class Horario {
    private Map<String, Set<String>> turnos;

    public Horario() {
        this.turnos = new HashMap<String, Set<String>>();
    }

    public Horario(Map<String, Set<String>> turnos) {
        this.turnos = turnos.entrySet().stream().collect(
            Collectors.toMap(e -> e.getKey(), e -> new HashSet(e.getValue())));
    }

    public Horario(Horario horario) {
        this.turnos = horario.getNomesDeTurnos();
    }

    public void adicionarTurno(UC uc, Turno turno) {
        String nomeUC = uc.getNome();

        Set<String> turnosUC = this.turnos.get(nomeUC);
        if (turnosUC == null) {
            turnosUC = new HashSet<String>();
            turnos.put(nomeUC, turnosUC);
        }

        String nomeTurno = turno.getNome();
        turnosUC.add(nomeTurno);
    }

    public Collection<Sobreposicao> procurarSobreposicoes(String aluno) {
        Collection<Sobreposicao> ret = new ArrayList<Sobreposicao>();

        Map<String, Set<Turno>>            turnosObj = this.getTurnos();
        Set<Map.Entry<String, Set<Turno>>> entradas  = turnosObj.entrySet();

        for (Map.Entry<String, Set<Turno>> entrada1 : entradas) {
            String     uc1     = entrada1.getKey();
            Set<Turno> turnos1 = entrada1.getValue();

            for (Turno turno1 : turnos1) {
                String nome1 = turno1.getNome();

                for (Map.Entry<String, Set<Turno>> entrada2 : entradas) {
                    String     uc2     = entrada2.getKey();
                    Set<Turno> turnos2 = entrada2.getValue();

                    for (Turno turno2 : turnos2) {
                        String  nome2 = turno2.getNome();
                        boolean s     = turno1.sobrepoe(turno2);
                        int     cmp   = nome1.compareTo(nome2);

                        if (s && cmp < 0) {
                            Sobreposicao sobre = new Sobreposicao(aluno, uc1, nome1, uc2, nome2);
                            ret.add(sobre);
                        }
                    }
                }
            }
        }

        return ret;
    }

    // TODO - melhorar isto
    public boolean validar(Aluno aluno) {
        Set<String> ucsHorario = this.turnos.keySet();
        Set<String> ucsAluno   = aluno.getNomesDeUCs();

        boolean ret = ucsHorario.equals(ucsAluno);
        return ret;
    }

    public Map<String, Set<Turno>> getTurnos() {
        UCDAO                   dao = UCDAO.getInstance();
        Map<String, Set<Turno>> ret = new HashMap<String, Set<Turno>>();

        Set<String> ucs = this.turnos.keySet();
        for (String nomeUC : ucs) {
            UC uc = dao.get(nomeUC);

            Set<String> nomeTurnos = this.turnos.get(nomeUC);
            Set<Turno>  turnos     = new HashSet<Turno>();

            for (String nomeTurno : nomeTurnos) {
                Turno turno = uc.getTurno(nomeTurno);
                turnos.add(turno);
            }

            ret.put(nomeUC, turnos);
        }

        return ret;
    }

    public Map<String, Set<String>> getNomesDeTurnos() {
        return this.turnos.entrySet().stream().collect(
            Collectors.toMap(e -> e.getKey(), e -> new HashSet(e.getValue())));
    }

    @Override
    public Object clone() {
        return new Horario(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || this.getClass() != o.getClass())
            return false;

        Horario horario = (Horario) o;
        return this.turnos.equals(horario.getTurnos());
    }

    @Override
    public String toString() {
        return String.format("Horario(turnos=%s)", this.turnos.toString());
    }
}
