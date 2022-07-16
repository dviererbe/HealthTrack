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

package de.dviererbe.healthtrack.presentation.main.weight;

import android.util.Log;
import de.dviererbe.healthtrack.IDisposable;
import de.dviererbe.healthtrack.domain.WeightRecord;
import de.dviererbe.healthtrack.infrastructure.IDateTimeConverter;
import de.dviererbe.healthtrack.infrastructure.INumericValueConverter;
import de.dviererbe.healthtrack.persistence.IWeightWidgetRepository;
import de.dviererbe.healthtrack.presentation.ConversionHelper;

import java.util.UUID;

public class WeightDetailsViewModel implements IDisposable
{
    private static final String TAG = "WeightDetailsViewModel";

    private static final String ErrorValue = "(error)";

    private final IWeightDetailsView _view;

    private final IWeightWidgetRepository _repository;

    private final UUID _recordIdentifier;
    public final String Value;
    public final String Unit;
    public final String DateTime;

    public WeightDetailsViewModel(
        final IWeightDetailsView view,
        final IWeightWidgetRepository weightWidgetRepository,
        final IDateTimeConverter dateTimeConverter,
        final INumericValueConverter numericValueConverter,
        final UUID recordIdentifier)
    {
        _view = view;
        _repository = weightWidgetRepository;
        _recordIdentifier = recordIdentifier;

        final WeightRecord record;

        try
        {
            record = _repository.GetRecord(recordIdentifier);
        }
        catch (Exception exception)
        {
            Log.d(TAG, "Failed to get record " + _recordIdentifier, exception);
            Value = Unit = DateTime = "(error)";
            return;
        }

        Value = ConversionHelper.TryConvertToString(record.Value, ErrorValue, numericValueConverter);
        Unit = ConversionHelper.TryConvertToString(record.Unit, ErrorValue, numericValueConverter);
        DateTime = ConversionHelper.TryConvertToString(record.TimeOfMeasurement, ErrorValue, dateTimeConverter);
    }

    public void Edit()
    {
        _view.NavigateToEditView(_recordIdentifier);
    }

    public void Delete()
    {
        _view.ShowConfirmDeleteDialog(confirmDelete ->
        {
            if (confirmDelete)
            {
                try
                {
                    _repository.DeleteRecord(_recordIdentifier);
                }
                catch (Exception exception)
                {
                    Log.d(TAG, "Failed to delete record " + _recordIdentifier, exception);
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
     * Interface for a weight record details user interface.
     */
    public interface IWeightDetailsView
    {
        /**
         * Notifies the user that the record could not be deleted because of an error.
         */
        void NotifyUserThatRecordCouldNotBeDeleted();

        /**
         * Navigates the user to a UI for editing a specific weight record.
         */
        void NavigateToEditView(UUID record);

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
