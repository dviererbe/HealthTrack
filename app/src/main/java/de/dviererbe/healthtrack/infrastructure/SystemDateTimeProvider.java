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

package de.dviererbe.healthtrack.infrastructure;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Uses the computer date and time to provide the current date and time.
 */
public class SystemDateTimeProvider implements IDateTimeProvider
{
    /**
     * Gets a {@link LocalDateTime} instance that is set to the current date and time, expressed as the local time.
     *
     * @return An {@link LocalDateTime} instance whose value is the current local date and time.
     */
    @Override
    public LocalDateTime Now()
    {
        return LocalDateTime.now();
    }

    /**
     * Gets a {@link LocalDate} instance that is set to the current date, expressed as the local time.
     *
     * @return An {@link LocalDate} instance whose value is the current local date.
     */
    @Override
    public LocalDate Today()
    {
        return LocalDate.now();
    }
}
