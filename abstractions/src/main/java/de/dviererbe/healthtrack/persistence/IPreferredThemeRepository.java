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

package de.dviererbe.healthtrack.persistence;

/**
 * A storage mechanism for the app specific user preferred theme.
 */
public interface IPreferredThemeRepository
{
    /**
     * Reads the theme the user prefers for this application.
     *
     * @return App specific theme the user prefers.
     */
    PreferredTheme GetPreferredTheme();

    /**
     * Registers a listener instance that gets notified when the
     * user preferred theme for this application changed.
     *
     * @param listener The listener instance that gets notified.
     */
    void RegisterOnPreferredThemeChangedListener(OnPreferredThemeChangedListener listener);

    /**
     * Removes a listener instance from the notification list when
     * the user preferred theme for this application changed.
     *
     * @param listener The listener instance that gets removed from the notification list.
     */
    void UnregisterOnPreferredThemeChangedListener(OnPreferredThemeChangedListener listener);

    /**
     * Themes that can be selected by the user.
     */
    enum PreferredTheme
    {
        /**
         * Use the device theme.
         */
        FollowSystem,

        /**
         * Use a light theme.
         */
        Light,

        /**
         * Use a dark theme.
         */
        Dark,
    }

    /**
     * Interface definition for a callback to be invoked when
     * the app specific user preferred theme is changed.
     */
    interface OnPreferredThemeChangedListener
    {
        /**
         * Called when the app specific user preferred locale is theme.
         *
         * @param preferredTheme The new app specific user preferred theme.
         */
        void OnPreferredThemeChanged(PreferredTheme preferredTheme);
    }
}
