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
import de.dviererbe.healthtrack.domain.WeightUnit;
import de.dviererbe.healthtrack.infrastructure.IDateTimeConverter;
import de.dviererbe.healthtrack.infrastructure.INavigationRouter;
import de.dviererbe.healthtrack.infrastructure.INumericValueConverter;
import de.dviererbe.healthtrack.persistence.IPreferredUnitRepository;
import de.dviererbe.healthtrack.persistence.IWeightWidgetRepository;

import java.util.List;
import java.util.UUID;

public class WeightListViewModel implements IDisposable
{
    private final static String TAG = "WeightListViewModel";

    private final IWeightListView _view;
    private final INavigationRouter _navigationRouter;
    private final IWeightWidgetRepository _repository;
    private final IDateTimeConverter _dateTimeConverter;
    private final INumericValueConverter _numericValueConverter;
    private final WeightUnit _preferredWeightUnit;

    public WeightListViewModel(
            final IWeightListView view,
            final INavigationRouter navigationRouter,
            final IWeightWidgetRepository repository,
            final IDateTimeConverter dateTimeConverter,
            final INumericValueConverter numericValueConverter,
            final IPreferredUnitRepository preferredUnitRepository)
    {
        _navigationRouter = navigationRouter;
        _repository = repository;
        _view = view;
        _dateTimeConverter = dateTimeConverter;
        _numericValueConverter = numericValueConverter;
        _preferredWeightUnit = GetPreferredWeightUnit(preferredUnitRepository);
    }

    private static WeightUnit GetPreferredWeightUnit(final IPreferredUnitRepository preferredUnitRepository)
    {
        final IPreferredUnitRepository.MassUnit preferredMassUnit =
                preferredUnitRepository.GetPreferredMassUnit();

        switch (preferredMassUnit)
        {
            case Pound:
                return WeightUnit.Pound;
            case Kilogram:
            default:
                return WeightUnit.Kilogram;
        }
    }

    public int GetRecordCount()
    {
        try
        {
            final long recordCount = _repository.GetRecordCount();

            if (recordCount > Integer.MAX_VALUE)
                return Integer.MAX_VALUE;
            else
                return (int)recordCount;
        }
        catch (Exception exception)
        {
            Log.d(TAG, "Failed to load record count.", exception);
            return 0;
        }
    }

    public WeightListItemViewModel GetRecord(int offset)
    {
        final WeightRecord weightRecord = TryGetRecord(offset);

        return new WeightListItemViewModel(
                _navigationRouter,
                _dateTimeConverter,
                _numericValueConverter,
                weightRecord,
                _preferredWeightUnit);
    }

    private WeightRecord TryGetRecord(int offset)
    {
        try
        {
            // TODO: implement caching solution
            final List<WeightRecord> records = _repository.GetRecordsDescending(offset, 1);
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
        _navigationRouter.TryNavigateToCreateWeightRecord();
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
     * Interface for the weight record list user interface.
     */
    public interface IWeightListView
    {
        /**
         * Notifies the {@link IWeightListView} that the item list has changed.
         */
        void OnListItemsChanged();

        /**
         * Shows the user a UI that asks for confirmation to delete all records.
         *
         * @param callback a reference to a callback mechanism when the user made a decision.
         */
        void ShowConfirmDeleteAllDialog(IConfirmDeleteAllDialogObserver callback);

        /**
         * Notifies the user that the weight records have been deleted successfully.
         */
        void NotifyUserThatRecordsHaveBeenDeleted();

        /**
         * Notifies the user that the weight records could not be deleted because of an error.
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
             * @param confirmedDelete {@code true} when all weight records should be deleted; otherweise {@code false}.
             */
            void OnCompleted(boolean confirmedDelete);
        }
    }
}