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

package de.dviererbe.healthtrack;

import de.dviererbe.healthtrack.application.SetUIThemeWhenUserPreferenceChangesBehaviour;
import de.dviererbe.healthtrack.persistence.IPreferredThemeRepository;
import de.dviererbe.healthtrack.presentation.IUIThemeSetter;

public class ServiceContainer
{
    private final SetUIThemeWhenUserPreferenceChangesBehaviour _setUIThemeWhenUserPreferenceChanges;

    public ServiceContainer(IDependencyResolver dependencyResolver)
    {
        IUIThemeSetter uiThemeSetter = dependencyResolver.GetUIThemeSetter();
        IPreferredThemeRepository preferredThemeRepository = dependencyResolver.GetPreferredThemeRepository();

        _setUIThemeWhenUserPreferenceChanges = new SetUIThemeWhenUserPreferenceChangesBehaviour(
                uiThemeSetter,
                preferredThemeRepository);
    }
}
