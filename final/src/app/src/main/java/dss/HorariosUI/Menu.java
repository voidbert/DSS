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

/** An options menu for the application. */
public class Menu {
    /** Entries in this menu. */
    private MenuEntry[] entries;
    private Scanner scanner;

    /** Creates a new empty menu that can read from standard input. */
    public Menu() {
        this.entries = new MenuEntry[0];
        this.scanner = new Scanner(System.in);
    }

    /**
     * Creates a menu from the value of its fields.
     *
     * @param entries Entries in the menu.
     */
    public Menu(MenuEntry[] entries, Scanner scanner) {
        this.setEntries(entries);
        this.setScanner(scanner);
    }

    /**
     * Copy constructor of a menu.
     *
     * @param menu Menu to be copied.
     */
    public Menu(Menu menu) {
        this.entries = menu.getEntries();
        this.scanner = menu.getScanner();
    }

    /**
     * Gets this menu's entries.
     *
     * @return This menu's entries.
     */
    public MenuEntry[] getEntries() {
        return Arrays.stream(this.entries).map(MenuEntry::clone).toArray(MenuEntry[]::new);
    }

    public Scanner getScanner() {
        return this.scanner;
    }

    /**
     * Sets this menu's entries.
     *
     * @param entries This menu's entries.
     */
    public void setEntries(MenuEntry[] entries) {
        this.entries = Arrays.stream(entries).map(MenuEntry::clone).toArray(MenuEntry[]::new);
    }

    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    /** Runs this menu. */
    public void run() {
        System.out.println("\nChoose an option ...\n");
        for (int i = 0; i < this.entries.length; ++i) {
            System.out.println(String.format("  %d -> %s", i + 1, entries[i].getText()));
        }
        System.out.println("");

        int option =
                this.readInt(
                        "Option > ",
                        String.format("Must be an integer betwewn 1 and %d!", this.entries.length),
                        i -> i > 0 && i <= this.entries.length);
        this.entries[option - 1].getHandler().accept(option - 1);
    }

    public Object read(String                   prompt,
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

    public int readInt(String prompt, String error, Predicate<Integer> validate) {
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

    /**
     * Calculates the hash code of this menu.
     *
     * @return The hash code of this menu.
     */
    @Override
    public int hashCode() {
        return this.entries.hashCode();
    }

    /**
     * Checks if this menu is equal to another object.
     *
     * @param obj Object to be compared with this menu.
     * @return Whether <code>this</code> is equal to <code>obj</code>.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || this.getClass() != obj.getClass()) return false;

        Menu menu = (Menu) obj;
        return Arrays.equals(this.entries, menu.getEntries());
    }

    /**
     * Creates a deep copy of this menu.
     *
     * @return A deep copy of this menu.
     */
    @Override
    public Menu clone() {
        return new Menu(this);
    }
}
