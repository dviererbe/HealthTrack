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
import de.dviererbe.healthtrack.infrastructure.ILogger;
import de.dviererbe.healthtrack.infrastructure.INavigationRouter;
import de.dviererbe.healthtrack.infrastructure.INumericValueConverter;
import de.dviererbe.healthtrack.persistence.IStepWidgetRepository;
import de.dviererbe.healthtrack.presentation.ConversionHelper;

import java.time.LocalDate;
import java.util.UUID;

public class StepCountDetailsViewModel implements IDisposable
{
    private static final String TAG = "StepCountDetailsViewModel";

    private static final String NullValue = "(null)";
    private static final String ErrorValue = "(error)";

    private final IStepCountDetailsView _view;
    private final INavigationRouter _navigationRouter;
    private final IStepWidgetRepository _repository;

    private final ILogger _logger;
    private final UUID _identifier;

    public final String StepCount;
    public final String Goal;
    public final int GoalReachedPercentage;
    public final String GoalReachedPercentageText;
    public final String DateTime;

    public StepCountDetailsViewModel(
            final IStepCountDetailsView view,
            final INavigationRouter navigationRouter,
            final IStepWidgetRepository repository,
            final IDateTimeConverter dateTimeConverter,
            final INumericValueConverter numericValueConverter,
            final ILogger logger,
            final UUID identifier)
    {
        _view = view;
        _navigationRouter = navigationRouter;
        _repository = repository;
        _logger = logger;
        _identifier = identifier;

        final StepCountRecord record;

        try
        {
            record = _repository.GetRecordForDay(identifier);
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

    public void Edit()
    {
        _navigationRouter.TryNavigateToEditStepCountRecord(_day);
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
                    _logger.LogDebug(TAG, "Failed to delete record.", exception);
                    _view.NotifyUserThatRecordCouldNotBeDeleted();
                    return;
                }

                _navigationRouter.TryNavigateBack();
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
         * Shows the user a dialog to confirm that the record should be deleted.
         *
         * @param callback a reference to a callback mechanism when the user made a decision.
         */
        void ShowConfirmDeleteDialog(IConfirmDeleteDialogObserver callback);

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
