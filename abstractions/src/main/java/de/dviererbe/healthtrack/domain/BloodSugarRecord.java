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

package de.dviererbe.healthtrack.domain;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents an immutable record of blood sugar at a given date and time.
 */
public class BloodSugarRecord
{
    /**
     * An identifier that uniquely identifies this specific record.
     */
    public final UUID Identifier;

    /**
     * The date and time when record was created.
     */
    public final LocalDateTime TimeOfMeasurement;

    public BloodSugarRecord(
            final UUID identifier,
            final LocalDateTime timeOfMeasurement)
    {
        Identifier = identifier;
        TimeOfMeasurement = timeOfMeasurement;
    }
}
