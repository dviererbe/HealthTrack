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

package de.dviererbe.healthtrack.application;

import de.dviererbe.healthtrack.persistence.IPreferredThemeRepository;
import de.dviererbe.healthtrack.presentation.IUIThemeSetter;

/**
 * Encapsulates the logic to change the UI theme when the user preference changes.
 */
public class SetUIThemeWhenUserPreferenceChangesBehaviour
{
    private final IUIThemeSetter UIThemeSetter;
    private final IPreferredThemeRepository PreferredThemeRepository;

    /**
     * Creates a new {@link SetUIThemeWhenUserPreferenceChangesBehaviour} instance using the provided dependencies.
     *
     * @param uiThemeSetter a reference to an implementation that can set the UI theme.
     * @param preferredThemeRepository a reference to an implementation that can read the UI theme preference.
     */
    public SetUIThemeWhenUserPreferenceChangesBehaviour(
            IUIThemeSetter uiThemeSetter,
            IPreferredThemeRepository preferredThemeRepository)
    {
        UIThemeSetter = uiThemeSetter;
        PreferredThemeRepository = preferredThemeRepository;

        SetTheme(PreferredThemeRepository.GetPreferredTheme());
        preferredThemeRepository.RegisterOnPreferredThemeChangedListener(this::SetTheme);
    }

    private void SetTheme(IPreferredThemeRepository.PreferredTheme preferredTheme)
    {
        switch (preferredTheme)
        {
            case Light:
                UIThemeSetter.SetLightTheme();
                return;
            case Dark:
                UIThemeSetter.SetDarkTheme();
                return;
            case FollowSystem:
                UIThemeSetter.SetAndFollowSystemTheme();
                return;
        }
    }
}
