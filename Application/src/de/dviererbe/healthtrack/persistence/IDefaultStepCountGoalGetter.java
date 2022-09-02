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

import de.dviererbe.healthtrack.persistence.exceptions.RepositoryDisposed;
import de.dviererbe.healthtrack.persistence.exceptions.RepositoryException;

/**
 * Mechanism for getting the default step count goal for a day.
 */
public interface IDefaultStepCountGoalGetter
{
    /**
     * Gets the default step count goal for a day.
     *
     * @return scalar count of the default step count goal for a day.
     * @throws RepositoryDisposed when the repository was already disposed.
     * @throws RepositoryException when an unexpected I/O error occurs.
     */
    int GetDefaultStepCountGoal()
        throws
            RepositoryDisposed,
            RepositoryException;
}
