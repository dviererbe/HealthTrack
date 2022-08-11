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

import de.dviererbe.healthtrack.domain.StepCountRecord;
import de.dviererbe.healthtrack.infrastructure.IDateTimeConverter;
import de.dviererbe.healthtrack.infrastructure.ILogger;
import de.dviererbe.healthtrack.infrastructure.INumericValueConverter;
import de.dviererbe.healthtrack.persistence.IDeletableById;
import de.dviererbe.healthtrack.persistence.IQueryableById;
import de.dviererbe.healthtrack.presentation.ConversionHelper;
import de.dviererbe.healthtrack.presentation.ViewModel;

import java.util.UUID;

public class StepCountDetailsViewModel extends ViewModel<StepCountDetailsViewModel.IStepCountDetailsViewModelEventHandler>
{
    private static final String TAG = "StepCountDetailsViewModel";

    private static final String NullValue = "(null)";
    private static final String ErrorValue = "(error)";
    private final IDeletableById _stepCountRecordDeleter;

    private final ILogger _logger;
    private final UUID _identifier;

    public final String StepCount;
    public final String Goal;
    public final int GoalReachedPercentage;
    public final String GoalReachedPercentageText;
    public final String DateTime;

    public StepCountDetailsViewModel(
        final IQueryableById<StepCountRecord> stepCountRecordReader,
        final IDeletableById stepCountRecordDeleter,
        final IDateTimeConverter dateTimeConverter,
        final INumericValueConverter numericValueConverter,
        final ILogger logger,
        final UUID identifier)
    {
        _stepCountRecordDeleter = stepCountRecordDeleter;
        _logger = logger;
        _identifier = identifier;

        final StepCountRecord record;

        try
        {
            record = stepCountRecordReader.GetRecord(identifier);
        }
        catch (Exception exception)
        {
            _logger.LogDebug(TAG, "Failed to get record.", exception);
            StepCount = Goal = DateTime = GoalReachedPercentageText = NullValue;
            GoalReachedPercentage = 0;
            return;
        }

        StepCount = ConversionHelper.TryConvertToString(record.StepCount, ErrorValue, numericValueConverter);
        Goal =  ConversionHelper.TryConvertToString(record.Goal, ErrorValue, numericValueConverter);
        DateTime = ConversionHelper.TryConvertToString(record.TimeOfMeasurement, ErrorValue, dateTimeConverter);

        GoalReachedPercentage = record.CalculatePercentageOfGoalReached();
        GoalReachedPercentageText = ConversionHelper.TryConvertToString(GoalReachedPercentage, ErrorValue, numericValueConverter);
    }

    public void Delete()
    {
        try
        {
            _stepCountRecordDeleter.DeleteRecord(_identifier);
        }
        catch (Exception exception)
        {
            _logger.LogDebug(TAG, "Failed to delete record.", exception);
            NotifyEventHandlers(IStepCountDetailsViewModelEventHandler::RecordCouldNotBeDeleted);
            return;
        }

        NotifyEventHandlers(IStepCountDetailsViewModelEventHandler::RecordDeleted);
    }

    /**
     * Represents an actor that can react to events of the {@link StepCountDetailsViewModel}.
     */
    public interface IStepCountDetailsViewModelEventHandler
    {
        /**
         * Called when the record could not be deleted because of an error.
         */
        void RecordCouldNotBeDeleted();

        /**
         * Called when the record could be deleted successfully.
         */
        void RecordDeleted();

    }
}
