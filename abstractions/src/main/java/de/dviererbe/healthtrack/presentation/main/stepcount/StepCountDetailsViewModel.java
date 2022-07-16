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
import de.dviererbe.healthtrack.presentation.ConversionHelper;
import de.dviererbe.healthtrack.presentation.main.weight.WeightDetailsViewModel;

import java.time.LocalDate;

public class StepCountDetailsViewModel implements IDisposable
{
    private static final String TAG = "StepCountDetailsViewModel";

    private static final String NullValue = "(null)";
    private static final String ErrorValue = "(error)";

    private final IStepCountDetailsView _view;
    private final IStepWidgetRepository _repository;

    private final LocalDate _day;

    public final String StepCount;
    public final String Goal;
    public final int GoalReachedPercentage;
    public final String GoalReachedPercentageText;
    public final String DateTime;

    public StepCountDetailsViewModel(
            final IStepCountDetailsView view,
            final IStepWidgetRepository repository,
            final IDateTimeConverter dateTimeConverter,
            final INumericValueConverter numericValueConverter,
            final LocalDate day)
    {

        _view = view;
        _repository = repository;
        _day = day;

        final StepCountRecord record;

        try
        {
            record = _repository.GetRecordForDay(_day);
        }
        catch (Exception exception)
        {
            Log.d(TAG, "Failed to get record.", exception);
            StepCount = Goal = DateTime = GoalReachedPercentageText = NullValue;
            GoalReachedPercentage = 0;
            return;
        }

        StepCount = ConversionHelper.TryConvertToString(record.StepCount, ErrorValue, numericValueConverter);
        Goal =  ConversionHelper.TryConvertToString(record.StepCount, ErrorValue, numericValueConverter);
        DateTime = ConversionHelper.TryConvertToString(record.TimeOfMeasurement, ErrorValue, dateTimeConverter);

        GoalReachedPercentage = record.CalculatePercentageOfGoalReached();
        GoalReachedPercentageText = ConversionHelper.TryConvertToString(GoalReachedPercentage, ErrorValue, numericValueConverter);
    }

    public void Edit()
    {
        _view.NavigateToEditView(_day);
    }

    public void Delete()
    {
        _view.ShowConfirmDeleteDialog(confirmDelete ->
        {
            if (confirmDelete)
            {
                try
                {
                    _repository.DeleteRecordOfDay(_day);
                }
                catch (Exception exception)
                {
                    Log.d(TAG, "Failed to delete record.", exception);
                    _view.NotifyUserThatRecordCouldNotBeDeleted();
                    return;
                }

                _view.GoBack();
            }
        });
    }

    /**
     * Performs application-defined tasks associated with freeing, releasing, or resetting resources.
     */
    @Override
    public void Dispose()
    {
    }

    /**
     * Interface for a step count record details user interface.
     */
    public interface IStepCountDetailsView
    {
        /**
         * Notifies the user that the record could not be deleted because of an error.
         */
        void NotifyUserThatRecordCouldNotBeDeleted();

        /**
         * Navigates the user to a UI for editing a specific step count record.
         *
         * @param day The day of the record that the edit view should be displayed for.
         */
        void NavigateToEditView(LocalDate day);

        /**
         * Shows the user a dialog to confirm that the record should be deleted.
         *
         * @param callback a reference to a callback mechanism when the user made a decision.
         */
        void ShowConfirmDeleteDialog(IConfirmDeleteDialogObserver callback);

        /**
         * The view should navigate up in the navigation stack.
         */
        void GoBack();

        /**
         * Callback mechanism for when the confirm delete dialog exits.
         */
        interface IConfirmDeleteDialogObserver
        {
            /**
             * Called when the confirm delete dialog exits.
             *
             * @param confirmDelete the decision of the user: {@code true} when the record should
             *                      be deleted; otherwise {@code false}.
             */
            void OnCompleted(boolean confirmDelete);
        }
    }
}
