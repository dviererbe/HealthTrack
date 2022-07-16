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
 * Represents an immutable record of steps count at a given date and time.
 */
public class StepCountRecord
{
    /**
     * The default step count goal for a day.
     */
    public static final int DefaultStepCountGoal = 5000;

    /**
     * The scalar steps count quantity that was recorded.
     */
    public final int StepCount;

    /**
     * The scalar steps count quantity goal for the day that intended.
     */
    public final int Goal;

    /**
     * The date and time when the measurement was recorded.
     */
    public final LocalDateTime TimeOfMeasurement;

    /**
     * Creates a new immutable {@link StepCountRecord} instance for specified step count,
     * date and time with a random identifier.
     *
     * @param stepCount The count of steps that is recorded at the {@code timeOfMeasurement}.
     * @param goal what the step count goal of the day was.
     * @param timeOfMeasurement The date and time when the measurement was taken.
     */
    public StepCountRecord(
            final int stepCount,
            final int goal,
            final LocalDateTime timeOfMeasurement)
    {

        StepCount = stepCount;
        Goal = goal;
        TimeOfMeasurement = timeOfMeasurement;
    }

    public int CalculatePercentageOfGoalReached()
    {
        if (StepCount >= Goal) return 100;
        if (StepCount <= 0) return 0;

        final int percentage = Math.floorDiv(StepCount * 100, Goal);
        return percentage > 0 ? percentage : 1;
    }
}