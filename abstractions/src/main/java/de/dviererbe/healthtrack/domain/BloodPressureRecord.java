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
 * Represents an immutable record of blood pressure at a given date and time.
 */
public class BloodPressureRecord
{
    /**
     * An identifier that uniquely identifies this specific record.
     */
    public final UUID Identifier;

    /**
     * The scalar quantity of the systolic blood pressure that was recorded.
     */
    public final int Systolic;

    /**
     * The scalar quantity of the diastolic blood pressure that was recorded.
     */
    public final int Diastolic;

    /**
     * The unit of the pressure the blood pressure was measured in.
     */
    public final BloodPressureUnit Unit;

    /**
     * The scalar quantity of the puls in beats per minute when the blood pressure was measured.
     */
    public final int Pulse;

    /**
     * The state of blood pressure related medication.
     */
    public final MedicationState Medication;

    /**
     * The date and time when the measurement was recorded.
     */
    public final LocalDateTime TimeOfMeasurement;

    /**
     * Additional notes related to the measurement.
     */
    public final String Note;

    /**
     * Creates a new immutable {@link BloodPressureRecord} instance with a random identifier.
     *
     * @param systolic The scalar quantity of the systolic blood pressure that was recorded.
     * @param diastolic The scalar quantity of the diastolic blood pressure that was recorded.
     * @param unit The unit of the pressure the blood pressure was measured in.
     * @param pulse The scalar quantity of the puls in beats per minute when the blood pressure was measured.
     * @param medication The state of blood pressure related medication.
     * @param timeOfMeasurement The date and time when the measurement was recorded.
     * @param note Additional notes related to the measurement.
     */
    public BloodPressureRecord(
            final int systolic,
            final int diastolic,
            final BloodPressureUnit unit,
            final int pulse,
            final MedicationState medication,
            final LocalDateTime timeOfMeasurement,
            final String note)
    {
        this(UUID.randomUUID(), systolic, diastolic, unit, pulse, medication, timeOfMeasurement, note);
    }

    /**
     * Creates a new immutable {@link BloodPressureRecord} instance.
     *
     * @param identifier The identifier that uniquely identifies this record.
     * @param systolic The scalar quantity of the systolic blood pressure that was recorded.
     * @param diastolic The scalar quantity of the diastolic blood pressure that was recorded.
     * @param unit The unit of the pressure the blood pressure was measured in.
     * @param pulse The scalar quantity of the puls in beats per minute when the blood pressure was measured.
     * @param medication The state of blood pressure related medication.
     * @param timeOfMeasurement The date and time when the measurement was recorded.
     * @param note Additional notes related to the measurement.
     */
    public BloodPressureRecord(
            final UUID identifier,
            final int systolic,
            final int diastolic,
            final BloodPressureUnit unit,
            final int pulse,
            final MedicationState medication,
            final LocalDateTime timeOfMeasurement,
            final String note)
    {
        Identifier = identifier;
        Systolic = systolic;
        Diastolic = diastolic;
        Unit = unit;
        Pulse = pulse;
        Medication = medication;
        TimeOfMeasurement = timeOfMeasurement;
        Note = note;
    }
}
