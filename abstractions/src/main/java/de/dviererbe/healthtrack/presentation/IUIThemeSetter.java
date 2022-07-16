/*
    Health Track
    Copyright (C) 2022  Dominik Viererbe

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation, either version 3 of the
    License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package de.dviererbe.healthtrack.presentation;

/**
 * Provides a mechanism for changing the UI theme.
 */
public interface IUIThemeSetter
{
    /**
     * Sets the UI style to a light theme.
     */
    void SetLightTheme();

    /**
     * Sets the UI style to a dark theme.
     */
    void SetDarkTheme();

    /**
     * Sets the UI to the devices' theme and updates the
     * theme automatically if the device theme is changed.
     */
    void SetAndFollowSystemTheme();
}
