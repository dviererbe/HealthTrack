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
import de.dviererbe.healthtrack.persistence.IBulkDeletable;
import de.dviererbe.healthtrack.persistence.IBulkQueryable;
import de.dviererbe.healthtrack.presentation.ViewModel;

import java.util.List;

public class StepCountListViewModel extends ViewModel<StepCountListViewModel.IStepCountListViewModelEventHandler>
{
    private final static String TAG = "StepCountListViewModel";

    private final IBulkQueryable<StepCountRecord> _stepCountReader;
    private final IBulkDeletable _stepCountDeleter;
    private final IDateTimeConverter _dateTimeConverter;
    private final INumericValueConverter _numericValueConverter;
    private final ILogger _logger;

    public StepCountListViewModel(
            final IBulkQueryable<StepCountRecord> stepCountReader,
            final IBulkDeletable stepCountDeleter,
            final IDateTimeConverter dateTimeConverter,
            final INumericValueConverter numericValueConverter,
            final ILogger logger)
    {
        _stepCountReader = stepCountReader;
        _stepCountDeleter = stepCountDeleter;
        _dateTimeConverter = dateTimeConverter;
        _numericValueConverter = numericValueConverter;
        _logger = logger;
    }

    public int GetRecordCount()
    {
        try
        {
            final long recordCount = _stepCountReader.GetRecordCount();

            if (recordCount > Integer.MAX_VALUE)
                return Integer.MAX_VALUE;
            else
                return (int)recordCount;
        }
        catch (Exception exception)
        {
            _logger.LogDebug(TAG, "Failed to load record count.", exception);
            return 0;
        }
    }

    public StepCountListItemViewModel GetRecord(int offset)
    {
        final StepCountRecord stepCountRecord = TryGetRecord(offset);

        return new StepCountListItemViewModel(
            _dateTimeConverter,
            _numericValueConverter,
            stepCountRecord);
    }

    private StepCountRecord TryGetRecord(int offset)
    {
        try
        {
            // TODO: implement caching solution
            final List<StepCountRecord> records = _stepCountReader.GetRecordsDescending(offset, 1);
            return records.get(0);
        }
        catch (Exception exception)
        {
            _logger.LogDebug(TAG, "Failed to load record.", exception);
            return null;
        }
    }

    public void DeleteAll()
    {
        try
        {
            _stepCountDeleter.DeleteAllRecords();
        }
        catch (Exception exception)
        {
            _logger.LogDebug(TAG, "Failed to delete records.", exception);
            NotifyEventHandlers(IStepCountListViewModelEventHandler::RecordsCouldNotBeDeleted);
            return;
        }

        NotifyEventHandlers(IStepCountListViewModelEventHandler::RecordsHaveBeenDeleted);
        NotifyEventHandlers(IStepCountListViewModelEventHandler::ListItemsChanged);
    }

    /**
     * Represents an actor that can react to events of the {@link StepCountListViewModel}.
     */
    public interface IStepCountListViewModelEventHandler
    {
        /**
         * Called when the item list has changed.
         */
        void ListItemsChanged();

        /**
         * Called when the step count records have been deleted successfully.
         */
        void RecordsHaveBeenDeleted();

        /**
         * Called when the step count records could not be deleted because of an error.
         */
        void RecordsCouldNotBeDeleted();
    }
}