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

package de.dviererbe.healthtrack.persistence.exceptions;

/**
 * The {@link InvalidArgumentException} that is thrown when
 * {@link de.dviererbe.healthtrack.persistence.IBulkQueryable#GetRecordsDescending(long, int)} is called
 * and the parameter {@code offset} is negative.
 */
public class OffsetIsNegative extends InvalidArgumentException
{
    /**
     * Initializes a new {@link OffsetIsNegative} instance.
     */
    public OffsetIsNegative()
    {
        super("offset", "Parameter offset is negative.");
    }
}
