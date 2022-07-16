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
 * Represents an immutable record of weight at a given date and time.
 */
public class WeightRecord
{
    /**
     * An identifier that uniquely identifies this specific record.
     */
    public final UUID Identifier;

    /**
     * The scalar quantity of the weight that was recorded.
     */
    public final double Value;

    /**
     * The unit of the weight quantity it was measured in.
     */
    public final WeightUnit Unit;

    /**
     * The date and time when the measurement was recorded.
     */
    public final LocalDateTime TimeOfMeasurement;

    /**
     * Creates a new immutable {@link WeightRecord} instance with a random identifier.
     *
     * @param value The scalar quantity of the weight that was measured.
     * @param unit The unit of the weight it was measured in.
     * @param timeOfMeasurement The date and time when the measurement was taken.
     */
    public WeightRecord(
            final double value,
            final WeightUnit unit,
            final LocalDateTime timeOfMeasurement)
    {
        this(UUID.randomUUID(), value, unit, timeOfMeasurement);
    }

    /**
     * Creates a new immutable {@link WeightRecord} instance.
     *
     * @param identifier The identifier that uniquely identifies this record.
     * @param value The scalar quantity of the weight that was measured.
     * @param unit The unit of the weight it was measured in.
     * @param timeOfMeasurement The date and time when the measurement was taken.
     */
    public WeightRecord(
            final UUID identifier,
            final double value,
            final WeightUnit unit,
            final LocalDateTime timeOfMeasurement)
    {
        Identifier = identifier;
        Value = value;
        Unit = unit;
        TimeOfMeasurement = timeOfMeasurement;
    }
}
