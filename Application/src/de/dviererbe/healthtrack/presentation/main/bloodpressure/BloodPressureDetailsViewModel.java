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
import de.dviererbe.healthtrack.persistence.IDeletableById;
import de.dviererbe.healthtrack.persistence.IQueryableById;
import de.dviererbe.healthtrack.persistence.repositories.IPreferredUnitRepository;
import de.dviererbe.healthtrack.presentation.ConversionHelper;

import java.util.UUID;

public class BloodPressureDetailsViewModel implements IDisposable
{
    private static final String TAG = "BloodPressureDetailsViewModel";

    private static final String NullValue = "(null)";
    private static final String ErrorValue = "(error)";

    private final IBloodPressureDetailsView _view;
    private final INavigationRouter _navigationRouter;
    private final IDeletableById _bloodPressureRecordDeleter;
    private final ILogger _logger;
    private final UUID _recordIdentifier;

    /**
     * The systolic blood pressure that was recorded.
     */
    public final String Systolic;

    /**
     * The diastolic blood pressure that was recorded.
     */
    public final String Diastolic;

    /**
     * {@code true} when the unit used for measuring {@link BloodPressureDetailsViewModel#Systolic} and
     * {@link BloodPressureDetailsViewModel#Diastolic} was converted; otherwise false.
     */
    public final boolean Converted;

    /**
     * The puls in beats per minute when the blood pressure was measured.
     */
    public final String Pulse;

    /**
     * The state of blood pressure related medication.
     */
    public final String Medication;

    /**
     * The date and time when the measurement was recorded.
     */
    public final String TimeOfMeasurement;

    /**
     * Additional notes related to the measurement.
     */
    public final String Note;

    public BloodPressureDetailsViewModel(
            final IBloodPressureDetailsView view,
            final INavigationRouter navigationRouter,
            final IQueryableById<BloodPressureRecord> bloodPressureRecordReader,
            final IDeletableById bloodPressureRecordDeleter,
            final IDateTimeConverter dateTimeConverter,
            final INumericValueConverter numericValueConverter,
            final IPreferredUnitRepository preferredUnitRepository,
            final ILogger logger,
            final UUID recordIdentifier)
    {
        _view = view;
        _navigationRouter = navigationRouter;
        _bloodPressureRecordDeleter = bloodPressureRecordDeleter;
        _logger = logger;
        _recordIdentifier = recordIdentifier;

        final BloodPressureRecord record;

        try
        {
            record = bloodPressureRecordReader.GetRecord(recordIdentifier);
        }
        catch (Exception exception)
        {
            _logger.LogDebug(TAG, "Failed to get record " + _recordIdentifier, exception);
            Systolic = Diastolic = Pulse = Medication = TimeOfMeasurement = Note = NullValue;
            Converted = false;
            return;
        }

        BloodPressureUnit destinationUnit = GetPreferredBloodPressureUnit(preferredUnitRepository);

        if (destinationUnit == null)
        {
            Converted = false;
            destinationUnit = record.Unit;
        }
        else
        {
            Converted = record.Unit != destinationUnit;
        }

        final String destinationUnitName =
            ConversionHelper.TryConvertToString(
                destinationUnit,
                ErrorValue,
                numericValueConverter);

        Systolic = GetBloodPressureValueAsString(
            record.Systolic,
            record.Unit,
            destinationUnit,
            destinationUnitName,
            numericValueConverter);

        Diastolic = GetBloodPressureValueAsString(
            record.Diastolic,
            record.Unit,
            destinationUnit,
            destinationUnitName,
            numericValueConverter);

        Pulse = ConversionHelper.TryConvertToString(
            record.Pulse,
            ErrorValue,
            numericValueConverter);

        Medication = ConversionHelper.TryConvertToString(
            record.Medication,
            ErrorValue,
            numericValueConverter);

        TimeOfMeasurement = ConversionHelper.TryConvertToString(
            record.TimeOfMeasurement,
            ErrorValue,
            dateTimeConverter);

        Note = record.Note;
    }

    private static BloodPressureUnit GetPreferredBloodPressureUnit(IPreferredUnitRepository preferredUnitRepository)
    {
        final IPreferredUnitRepository.BloodPressureUnit pressuredUnit =
                preferredUnitRepository.GetPreferredBloodPressureUnit();

        switch (pressuredUnit)
        {
            case Kilopascal:
                return BloodPressureUnit.Kilopascal;
            case MillimetreOfMercury:
                return BloodPressureUnit.MillimetreOfMercury;
            default:
                return null;
        }
    }

    private static String GetBloodPressureValueAsString(
        int value,
        final BloodPressureUnit sourceUnit,
        final BloodPressureUnit destinationUnit,
        final String nameOfDestinationUnit,
        final INumericValueConverter valueConverter)
    {
        if (sourceUnit != destinationUnit)
        {
            value = (int)Math.round(BloodPressureUnit.Convert(value, sourceUnit, destinationUnit));
        }

        return ConversionHelper.TryConvertToString(value, ErrorValue, valueConverter) + " " + nameOfDestinationUnit;
    }

    public void Edit()
    {
        if (_recordIdentifier == null) return;

        _navigationRouter.TryNavigateToEditBloodPressureRecord(_recordIdentifier);
    }

    public void Delete()
    {
        if (_recordIdentifier == null) return;

        _view.ShowConfirmDeleteDialog(confirmDelete ->
        {
            if (confirmDelete)
            {
                try
                {
                    _bloodPressureRecordDeleter.DeleteRecord(_recordIdentifier);
                }
                catch (Exception exception)
                {
                    _logger.LogDebug(TAG, "Failed to delete record " + _recordIdentifier, exception);
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
     * Interface for a blood pressure record details user interface.
     */
    public interface IBloodPressureDetailsView
    {
        /**
         * Notifies the user that the record could not be deleted because of an error.
         */
        void NotifyUserThatRecordCouldNotBeDeleted();

        /**
         * Shows the user a dialog to pick confirm that the record should be deleted.
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
