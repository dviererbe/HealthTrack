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

import de.dviererbe.healthtrack.persistence.exceptions.*;

import java.time.LocalDate;

/**
 * Mechanism for querying individual records; identified by date.
 *
 * @param <TRecord> The type of the record that can be queried.
 */
public interface IQueryableByDay<TRecord>
{
    /**
     * Gets a record with a specific identifier.
     *
     * @param day The unique identifier of the record that should be retrieved.
     * @return the {@link TRecord} with the specified identifier
     * @throws DayIsNull when {@code day} is {@code null}.
     * @throws RecordNotFound when no record with the specified {@code day} was found.
     * @throws RepositoryDisposed when the repository was already disposed.
     * @throws RepositoryException when an unexpected I/O error occurs.
     */
    TRecord GetRecord(LocalDate day)
        throws
            DayIsNull,
            RecordNotFound,
            RepositoryDisposed,
            RepositoryException;
}
