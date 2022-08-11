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

package de.dviererbe.healthtrack.presentation.main.bloodpressure;

import de.dviererbe.healthtrack.IDisposable;
import de.dviererbe.healthtrack.domain.BloodPressureRecord;
import de.dviererbe.healthtrack.domain.BloodPressureUnit;
import de.dviererbe.healthtrack.infrastructure.IDateTimeConverter;
import de.dviererbe.healthtrack.infrastructure.ILogger;
import de.dviererbe.healthtrack.infrastructure.INavigationRouter;
import de.dviererbe.healthtrack.infrastructure.INumericValueConverter;
import de.dviererbe.healthtrack.persistence.IBulkDeletable;
import de.dviererbe.healthtrack.persistence.IBulkQueryable;
import de.dviererbe.healthtrack.persistence.repositories.IPreferredUnitRepository;

import java.util.List;

public class BloodPressureListViewModel implements IDisposable
{
    private final static String TAG = "BloodPressureListViewModel";
    private final IBloodPressureListView _view;
    private final INavigationRouter _navigationRouter;
    private final IBulkQueryable<BloodPressureRecord> _bloodPressureRecordReader;
    private final IBulkDeletable _bloodPressureRecordDeleter;
    private final IDateTimeConverter _dateTimeConverter;
    private final INumericValueConverter _numericValueConverter;

    private final BloodPressureUnit _preferredBloodPressureUnit;
    private final ILogger _logger;

    public BloodPressureListViewModel(
            final IBloodPressureListView view,
            final INavigationRouter navigationRouter,
            final IBulkQueryable<BloodPressureRecord> bloodPressureRecordReader,
            final IBulkDeletable bloodPressureRecordDeleter,
            final IDateTimeConverter dateTimeConverter,
            final INumericValueConverter numericValueConverter,
            final IPreferredUnitRepository preferredUnitRepository,
            final ILogger logger)
    {
        _view = view;
        _navigationRouter = navigationRouter;
        _bloodPressureRecordReader = bloodPressureRecordReader;
        _bloodPressureRecordDeleter = bloodPressureRecordDeleter;
        _dateTimeConverter = dateTimeConverter;
        _numericValueConverter = numericValueConverter;
        _preferredBloodPressureUnit = GetPreferredBloodPressureUnit(preferredUnitRepository);
        _logger = logger;
    }

    private static BloodPressureUnit GetPreferredBloodPressureUnit(final IPreferredUnitRepository preferredUnitRepository)
    {
        final IPreferredUnitRepository.BloodPressureUnit preferredMassUnit =
                preferredUnitRepository.GetPreferredBloodPressureUnit();

        switch (preferredMassUnit)
        {
            case MillimetreOfMercury:
                return BloodPressureUnit.MillimetreOfMercury;
            case Kilopascal:
            default:
                return BloodPressureUnit.Kilopascal;
        }
    }

    public int GetRecordCount()
    {
        try
        {
            final long recordCount = _bloodPressureRecordReader.GetRecordCount();

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

    public BloodPressureListItemViewModel GetRecord(int offset)
    {
        final BloodPressureRecord bloodPressureRecord = TryGetRecord(offset);

        return new BloodPressureListItemViewModel(
                _navigationRouter,
                _dateTimeConverter,
                _numericValueConverter,
                bloodPressureRecord,
                _preferredBloodPressureUnit);
    }

    private BloodPressureRecord TryGetRecord(int offset)
    {
        try
        {
            // TODO: implement caching solution
            final List<BloodPressureRecord> records = _bloodPressureRecordReader.GetRecordsDescending(offset, 1);
            return records.get(0);
        }
        catch (Exception exception)
        {
            _logger.LogDebug(TAG, "Failed to load record.", exception);
            return null;
        }
    }

    public void CreateRecord()
    {
        _navigationRouter.TryNavigateToCreateBloodPressureRecord();
    }

    public void DeleteAll()
    {
        _view.ShowConfirmDeleteAllDialog(confirmedDelete ->
        {
            if (confirmedDelete)
            {
                try
                {
                    _bloodPressureRecordDeleter.DeleteAllRecords();
                }
                catch (Exception exception)
                {
                    _logger.LogDebug(TAG, "Failed to delete records.", exception);
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
     * Interface for a blood pressure record list user interface.
     */
    public interface IBloodPressureListView
    {
        /**
         * Notifies the {@link IBloodPressureListView} that the item list has changed.
         */
        void OnListItemsChanged();

        /**
         * Shows the user a UI that asks for confirmation to delete all records.
         *
         * @param callback a reference to a callback mechanism when the user made a decision.
         */
        void ShowConfirmDeleteAllDialog(IConfirmDeleteAllDialogObserver callback);

        /**
         * Notifies the user that the blood pressure records have been deleted successfully.
         */
        void NotifyUserThatRecordsHaveBeenDeleted();

        /**
         * Notifies the user that the blood pressure records could not be deleted because of an error.
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
             * @param confirmedDelete {@code true} when all blood pressure records should be deleted; otherweise {@code false}.
             */
            void OnCompleted(boolean confirmedDelete);
        }
    }
}