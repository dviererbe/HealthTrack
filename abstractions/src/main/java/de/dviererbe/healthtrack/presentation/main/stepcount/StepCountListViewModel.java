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

import android.util.Log;
import de.dviererbe.healthtrack.IDisposable;
import de.dviererbe.healthtrack.domain.StepCountRecord;
import de.dviererbe.healthtrack.infrastructure.IDateTimeConverter;
import de.dviererbe.healthtrack.infrastructure.INumericValueConverter;
import de.dviererbe.healthtrack.persistence.IStepWidgetRepository;

import java.time.LocalDate;
import java.util.List;

public class StepCountListViewModel implements IDisposable
{
    private final static String TAG = "StepCountListViewModel";

    private final IStepCountListView _view;
    private final IStepWidgetRepository _repository;
    private final IDateTimeConverter _dateTimeConverter;
    private final INumericValueConverter _numericValueConverter;

    public StepCountListViewModel(
            final IStepCountListView view,
            final IStepWidgetRepository repository,
            final IDateTimeConverter dateTimeConverter,
            final INumericValueConverter numericValueConverter)
    {
        _view = view;
        _repository = repository;
        _dateTimeConverter = dateTimeConverter;
        _numericValueConverter = numericValueConverter;
    }

    public int GetRecordCount()
    {
        try
        {
            return _repository.GetRecordCount();
        }
        catch (Exception exception)
        {
            Log.d(TAG, "Failed to load record count.", exception);
            return 0;
        }
    }

    public StepCountListItemViewModel GetRecord(int offset)
    {
        final StepCountRecord stepCountRecord = TryGetRecord(offset);

        return new StepCountListItemViewModel(
                _view,
                _dateTimeConverter,
                _numericValueConverter,
                stepCountRecord);
    }

    private StepCountRecord TryGetRecord(int offset)
    {
        try
        {
            // TODO: implement caching solution
            final List<StepCountRecord> records = _repository.GetRecordsDescending(offset, 1);
            return records.get(0);
        }
        catch (Exception exception)
        {
            Log.d(TAG, "Failed to load record.", exception);
            return null;
        }
    }

    public void CreateRecord()
    {
        _view.NavigateToCreateView();
    }

    public void DeleteAll()
    {
        _view.ShowConfirmDeleteAllDialog(confirmedDelete ->
        {
            if (confirmedDelete)
            {
                try
                {
                    _repository.DeleteAllRecords();
                }
                catch (Exception exception)
                {
                    Log.d(TAG, "Failed to delete records.", exception);
                    _view.NotifyUserThatRecordsCouldNotBeDeleted();
                    return;
                }

                _view.OnListItemsChanged();
                _view.NotifyUserThatRecordsHaveBeenDeleted();
            }
        });
    }

    /**
     * Performs application-defined tasks associated with freeing, releasing, or resetting resources.
     */
    @Override
    public void Dispose()
    {
        // Nothing to do here.
    }

    /**
     * Interface for a step count record list user interface.
     */
    public interface IStepCountListView
    {
        /**
         * Notifies the {@link IStepCountListView} that the item list has changed.
         */
        void OnListItemsChanged();

        /**
         * Navigates the user to a UI for creating a specific record.
         */
        void NavigateToCreateView();

        /**
         * Navigates the user to a UI for showing the details of a specific day.
         */
        void NavigateToDetailsView(LocalDate day);

        /**
         * Shows the user a UI that asks for confirmation to delete all records.
         *
         * @param callback a reference to a callback mechanism when the user made a decision.
         */
        void ShowConfirmDeleteAllDialog(IConfirmDeleteAllDialogObserver callback);

        /**
         * Notifies the user that the step count records have been deleted successfully.
         */
        void NotifyUserThatRecordsHaveBeenDeleted();

        /**
         * Notifies the user that the step count records could not be deleted because of an error.
         */
        void NotifyUserThatRecordsCouldNotBeDeleted();

        /**
         * Callback mechanism for when the confirm delete all dialog exited.
         */
        interface IConfirmDeleteAllDialogObserver
        {
            /**
             * Called when the user made a decision and the confirm delete all dialog exited.
             *
             * @param confirmedDelete {@code true} when all step count records should be deleted; otherweise {@code false}.
             */
            void OnCompleted(boolean confirmedDelete);
        }
    }
}