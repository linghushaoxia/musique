/*
 * Copyright (c) 2008, 2009, 2010, 2011 Denis Tulskiy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with this work.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.tulskiy.musique.gui.library;

import javax.swing.*;

import com.tulskiy.musique.gui.language.LanguageConfigconst;
import com.tulskiy.musique.gui.language.LanguageUtil;

/**
 * Author: Denis Tulskiy
 * Date: 2/26/11
 */
public enum LibraryAction {
    SEND_TO_CURRENT(LanguageUtil.getLocalText(LanguageConfigconst.TASK_SEND_TO_CURRENT_PLAYLIST), KeyStroke.getKeyStroke("ENTER")),
    SEND_TO_NEW(LanguageUtil.getLocalText(LanguageConfigconst.TASK_SEND_TO_NEW_PLAYLIST), KeyStroke.getKeyStroke("ctrl ENTER")),
    ADD_TO_CURRENT(LanguageUtil.getLocalText(LanguageConfigconst.TASK_ADD_TO_CURRENT_PLAYLIST), KeyStroke.getKeyStroke("shift ENTER")),
    EXPAND_COLLAPSE(LanguageUtil.getLocalText(LanguageConfigconst.TASK_EXPAND_OR_COLLAPSE), null);

    String name;
    KeyStroke hotKey;

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public KeyStroke getKeyStroke() {
        return hotKey;
    }

    LibraryAction(String name, KeyStroke hotKey) {
        this.name = name;
        this.hotKey = hotKey;
    }
}
