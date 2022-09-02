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

import de.dviererbe.healthtrack.persistence.exceptions.DayIsNull;
import de.dviererbe.healthtrack.persistence.exceptions.RepositoryDisposed;
import de.dviererbe.healthtrack.persistence.exceptions.RepositoryException;

import java.time.LocalDate;
import java.util.List;

/**
 * Mechanism for querying records in bulk for a specific day.
 *
 * @param <TRecord> The type of the record that can be queried in bulk.
 */
public interface IPerDayBulkQueryable<TRecord>
{
    /**
     * Gets all records for a specified day in descending order sorted by the time of measurement.
     *
     * @param day The specific day to get the records for.
     * @return All records for the specified day in descending order sorted by the time of measurement.
     * @throws DayIsNull when {@code day} is {@code null}.
     * @throws RepositoryDisposed when the repository was already disposed.
     * @throws RepositoryException when an unexpected I/O error occurs.
     */
    List<TRecord> GetRecordsForDayDescending(final LocalDate day)
        throws
            DayIsNull,
            RepositoryDisposed,
            RepositoryException;
}
