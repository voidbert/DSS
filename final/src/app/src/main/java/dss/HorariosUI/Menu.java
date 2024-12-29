package dss.HorariosUI;

import java.util.Arrays;

/** An options menu for the application. */
public class Menu {
    /** Entries in this menu. */
    private MenuEntry[] entries;

    /** Creates a new empty menu. */
    public Menu() {
        this.entries = new MenuEntry[0];
    }

    /**
     * Creates a menu from the value of its fields.
     *
     * @param entries Entries in the menu.
     */
    public Menu(MenuEntry[] entries) {
        this.setEntries(entries);
    }

    /**
     * Copy constructor of a menu.
     *
     * @param menu Menu to be copied.
     */
    public Menu(Menu menu) {
        this.entries = menu.getEntries();
    }

    /**
     * Gets this menu's entries.
     *
     * @return This menu's entries.
     */
    public MenuEntry[] getEntries() {
        return Arrays.stream(this.entries).map(MenuEntry::clone).toArray(MenuEntry[]::new);
    }

    /**
     * Sets this menu's entries.
     *
     * @param entries This menu's entries.
     */
    public void setEntries(MenuEntry[] entries) {
        this.entries = Arrays.stream(entries).map(MenuEntry::clone).toArray(MenuEntry[]::new);
    }

    /** Runs this menu. */
    public void run() {
        UserInput input = new UserInput();

        System.out.println("\nChoose an option ...\n");
        for (int i = 0; i < this.entries.length; ++i) {
            System.out.println(String.format("  %d -> %s", i + 1, entries[i].getText()));
        }
        System.out.println("");

        int option =
                input.readInt(
                        "Option > ",
                        String.format("Must be an integer betwewn 1 and %d!", this.entries.length),
                        i -> i > 0 && i <= this.entries.length);
        this.entries[option - 1].getHandler().accept(option - 1);
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
