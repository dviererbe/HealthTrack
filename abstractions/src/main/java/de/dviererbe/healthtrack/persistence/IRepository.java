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

import java.util.List;

/**
 * Represents a repository that stores and manages {@link TRecord}'s.
 *
 * @param <TRecord> The type of the record that is stored.
 * @param <TRepositoryException> The general repository exception when an error occurred during an operation.
 */
public interface IRepository<TRecord, TRepositoryException extends Exception>
{
    /**
     * Gets the name of the repository provider.
     *
     * @return {@link String} representation of the repository provider name.
     */
    String GetProviderName();

    /**
     * Gets the version of the repository provider.
     *
     * @return {@link String} representation of the repository provider version.
     */
    String GetProviderVersion();

    /**
     * Gets the count of all stored records.
     *
     * @return scalar count of all stored records.
     * @throws TRepositoryException
     *      when the repository was already
     *      disposed or an unexpected I/O error occurs.
     */
    long GetRecordCount() throws TRepositoryException;

    /**
     * Gets a sequence of {@link TRecord} with the maximum length of {@code count}
     * in descending order sorted by the time of measurement beginning at the {@code offset}.
     *
     * @param offset zero based n-th element in descending order where to begin reading records
     * @param count maximum number of record to be read from the repository
     * @return read sequence of blood pressure records
     * @throws TRepositoryException when {@code offset} is negative, {@code count} not positive,
     * the repository was already disposed or an unexpected I/O error occurs.
     */
    List<TRecord> GetRecordsDescending(long offset, int count) throws TRepositoryException;
}
