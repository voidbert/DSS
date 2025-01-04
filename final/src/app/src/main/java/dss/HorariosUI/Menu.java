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

import java.util.Arrays;
import java.util.Scanner;
import java.util.function.Function;
import java.util.function.Predicate;

public class Menu {
    private MenuEntry[] entries;
    private Scanner     scanner;

    public Menu() {
        this.entries = new MenuEntry[0];
        this.scanner = new Scanner(System.in);
    }

    public Menu(MenuEntry[] entries, Scanner scanner) {
        this.setEntries(entries);
        this.setScanner(scanner);
    }

    public Menu(Menu menu) {
        this.entries = menu.getEntries();
        this.scanner = menu.getScanner();
    }

    public MenuEntry[] getEntries() {
        return Arrays.stream(this.entries).map(MenuEntry::clone).toArray(MenuEntry[] ::new);
    }

    public Scanner getScanner() {
        return this.scanner;
    }

    public void setEntries(MenuEntry[] entries) {
        this.entries = Arrays.stream(entries).map(MenuEntry::clone).toArray(MenuEntry[] ::new);
    }

    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    public void run() {
        System.out.println("\nEscolha uma opção ...\n");
        for (int i = 0; i < this.entries.length; ++i)
            System.out.println(String.format("  %d -> %s", i + 1, entries[i].getText()));
        System.out.println("");

        int option =
            this.readInt("Opção > ",
                         String.format("Tem de ser um inteiro entre 1 e %d!", this.entries.length),
                         i -> i > 0 && i <= this.entries.length);
        this.entries[option - 1].getHandler().accept(option - 1);
    }

    private Object read(String                   prompt,
                        String                   error,
                        Predicate<String>        validate,
                        Function<String, Object> convert) {

        String ret = null;
        do {
            System.out.print(prompt);
            String line = scanner.nextLine();
            if (validate.test(line))
                ret = line;
            else if (error != null)
                System.err.println(error);
        } while (ret == null);
        return convert.apply(ret);
    }

    private int readInt(String prompt, String error, Predicate<Integer> validate) {
        return (Integer) this.read(prompt, error, s -> {
            try {
                int i = Integer.parseInt(s);
                return validate.test(i);
            } catch (NumberFormatException e) {
                return false;
            }
        }, s -> Integer.parseInt(s));
    }

    public String readString(String prompt) {
        return (String) this.read(prompt, null, s -> true, s -> s);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || this.getClass() != obj.getClass())
            return false;

        Menu menu = (Menu) obj;
        return Arrays.equals(this.entries, menu.getEntries());
    }

    @Override
    public Menu clone() {
        return new Menu(this);
    }
}
