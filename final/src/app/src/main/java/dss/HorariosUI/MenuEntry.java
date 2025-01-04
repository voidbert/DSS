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

import java.util.function.Consumer;

public class MenuEntry {
    private String            text;
    private Consumer<Integer> handler;

    public MenuEntry() {
        this.text    = "";
        this.handler = (i) -> {};
    }

    public MenuEntry(String text, Consumer<Integer> handler) {
        this.text    = text;
        this.handler = handler;
    }

    public MenuEntry(MenuEntry entry) {
        this.text    = entry.getText();
        this.handler = entry.handler;
    }

    public String getText() {
        return this.text;
    }

    public Consumer<Integer> getHandler() {
        return this.handler;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setHandler(Consumer<Integer> handler) {
        this.handler = handler;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || this.getClass() != obj.getClass())
            return false;

        MenuEntry entry = (MenuEntry) obj;
        return this.text.equals(entry.getText()) && this.handler.equals(entry.getHandler());
    }

    @Override
    public MenuEntry clone() {
        return new MenuEntry(this);
    }

    @Override
    public String toString() {
        return String.format("MenuEntry(text = \"%s\", handler = %s)",
                             this.text,
                             this.handler.toString());
    }
}
