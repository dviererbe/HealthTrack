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

package de.dviererbe.healthtrack.presentation.main.stepcount;

import de.dviererbe.healthtrack.IDisposable;
import de.dviererbe.healthtrack.domain.StepCountRecord;
import de.dviererbe.healthtrack.infrastructure.IDateTimeConverter;
import de.dviererbe.healthtrack.infrastructure.INumericValueConverter;
import de.dviererbe.healthtrack.presentation.ConversionHelper;

import java.util.UUID;

public class StepCountListItemViewModel implements IDisposable
{
    private static final String NullValue = "(null)";
    private static final String ErrorValue = "(error)";

    public final UUID Identifier;

    public final String StepCount;
    public final String Goal;
    public final int GoalReachedPercentage;
    public final String GoalReachedPercentageText;
    public final String Date;

    public StepCountListItemViewModel(
            final INumericValueConverter numericValueConverter,
            final int defaultStepCountGoal)
    {
        Identifier = null;

        final String zero = ConversionHelper.TryConvertToString(0, ErrorValue, numericValueConverter);

        StepCount = zero;
        Goal = ConversionHelper.TryConvertToString(defaultStepCountGoal, ErrorValue, numericValueConverter);
        GoalReachedPercentageText = zero;
        Date = "";
        GoalReachedPercentage = 0;
    }

    public StepCountListItemViewModel(
            final IDateTimeConverter dateTimeConverter,
            final INumericValueConverter numericValueConverter,
            final StepCountRecord stepCountRecord)
    {
        if (stepCountRecord == null)
        {
            Identifier = null;
            StepCount = Goal = GoalReachedPercentageText = Date = NullValue;
            GoalReachedPercentage = 0;

            return;
        }

        Identifier = stepCountRecord.Identifier;
        StepCount = ConversionHelper.TryConvertToString(stepCountRecord.StepCount, ErrorValue, numericValueConverter);
        Goal =  ConversionHelper.TryConvertToString(stepCountRecord.Goal, ErrorValue, numericValueConverter);
        Date = ConversionHelper.TryConvertToString(stepCountRecord.TimeOfMeasurement.toLocalDate(), ErrorValue, dateTimeConverter);

        GoalReachedPercentage = stepCountRecord.CalculatePercentageOfGoalReached();
        GoalReachedPercentageText = ConversionHelper.TryConvertToString(GoalReachedPercentage, ErrorValue, numericValueConverter);
    }

    /**
     * Performs application-defined tasks associated with freeing, releasing, or resetting resources.
     */
    @Override
    public void Dispose()
    {
    }
}
