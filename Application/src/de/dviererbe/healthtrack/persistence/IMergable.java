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

import de.dviererbe.healthtrack.persistence.exceptions.OneOrMorePropertiesAreInvalid;
import de.dviererbe.healthtrack.persistence.exceptions.RecordIsNull;
import de.dviererbe.healthtrack.persistence.exceptions.RepositoryDisposed;
import de.dviererbe.healthtrack.persistence.exceptions.RepositoryException;

/**
 * Mechanism for creating or updating a record.
 *
 * @param <TRecord> The type of the record that should be created or updated.
 */
public interface IMergable<TRecord>
{
    /**
     * Created or updates an existing record for the specified identifier with the specified measurements.
     *
     * @param record The record that should be created or updated.
     * @throws RecordIsNull when {@code record} is {@code null}.
     * @throws OneOrMorePropertiesAreInvalid when {@code record} contains invalid properties.
     * @throws RepositoryDisposed when the repository was already disposed.
     * @throws RepositoryException when an unexpected I/O error occurs.
     */
    void CreateOrUpdateRecord(final TRecord record)
        throws
            RecordIsNull,
            OneOrMorePropertiesAreInvalid,
            RepositoryDisposed,
            RepositoryException;
}
