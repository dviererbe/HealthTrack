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

import de.dviererbe.healthtrack.persistence.exceptions.CountIsNotPositive;
import de.dviererbe.healthtrack.persistence.exceptions.OffsetIsNegative;
import de.dviererbe.healthtrack.persistence.exceptions.RepositoryDisposed;
import de.dviererbe.healthtrack.persistence.exceptions.RepositoryException;

import java.util.List;

/**
 * Mechanism for querying records in bulk.
 *
 * @param <TRecord> The type of the record that can be queried in bulk.
 */
public interface IBulkQueryable<TRecord>
{
    /**
     * Gets the count of all stored records.
     *
     * @return scalar count of all stored records.
     * @throws RepositoryDisposed when the repository was already disposed.
     * @throws RepositoryException when an unexpected I/O error occurs.
     */
    long GetRecordCount()
        throws
            RepositoryDisposed,
            RepositoryException;

    /**
     * Gets a sequence of records with the maximum length of {@code count}
     * in descending order sorted by the time of measurement beginning at the {@code offset}.
     *
     * @param offset zero based n-th element in descending order where to begin reading records
     * @param count maximum number of record to be read from the repository
     * @return read sequence of records
     * @throws OffsetIsNegative when {@code offset} is negative.
     * @throws CountIsNotPositive when {@code count} is not positive.
     * @throws RepositoryDisposed when the repository was already disposed.
     * @throws RepositoryException when an unexpected I/O error occurs.
     */
    List<TRecord> GetRecordsDescending(long offset, int count)
        throws
            OffsetIsNegative,
            CountIsNotPositive,
            RepositoryDisposed,
            RepositoryException;
}
