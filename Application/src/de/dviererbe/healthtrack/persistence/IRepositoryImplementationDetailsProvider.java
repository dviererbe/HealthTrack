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

import java.util.List;
import java.util.Map;

/**
 * Interface that provides the implementation details of a repository.
 */
public interface IRepositoryImplementationDetailsProvider
{
    /**
     * Gets the
     *
     * @return A {@link List} that contains {@link Map}
     */
    List<Map<String, String>> GetImplementationDetails()
        throws
            RepositoryDisposed,
            RepositoryException;
}
