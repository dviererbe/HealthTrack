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

import de.dviererbe.healthtrack.persistence.exceptions.RecordIdentifierIsNull;
import de.dviererbe.healthtrack.persistence.exceptions.RecordNotFound;
import de.dviererbe.healthtrack.persistence.exceptions.RepositoryDisposed;
import de.dviererbe.healthtrack.persistence.exceptions.RepositoryException;

import java.util.UUID;

/**
 * Mechanism to delete an individual record; identified by the identifier.
 */
public interface IDeletableById
{
    /**
     * Deletes a record with a specific identifier.
     *
     * @param identifier The value that identifies the record that should be deleted uniquely.
     * @throws RecordIdentifierIsNull when {@code identifier} is {@code null}.
     * @throws RecordNotFound when no record with the specified {@code identifier} was found.
     * @throws RepositoryDisposed when the repository was already disposed.
     * @throws RepositoryException when an unexpected I/O error occurs.
     */
    void DeleteRecord(UUID identifier)
        throws
            RecordIdentifierIsNull,
            RecordNotFound,
            RepositoryDisposed,
            RepositoryException;
}
