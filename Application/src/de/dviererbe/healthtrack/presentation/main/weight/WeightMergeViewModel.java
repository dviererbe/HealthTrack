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

import de.dviererbe.healthtrack.domain.WeightRecord;
import de.dviererbe.healthtrack.domain.WeightUnit;
import de.dviererbe.healthtrack.infrastructure.*;
import de.dviererbe.healthtrack.persistence.IMergable;
import de.dviererbe.healthtrack.persistence.IQueryableById;
import de.dviererbe.healthtrack.persistence.repositories.IPreferredUnitRepository;
import de.dviererbe.healthtrack.presentation.ConversionHelper;
import de.dviererbe.healthtrack.presentation.ViewModel;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class WeightMergeViewModel extends ViewModel<WeightMergeViewModel.IWeightMergeViewModelEventHandler>
{
    private static final String TAG = "WeightMergeViewModel";

    // DEPENDENCIES:
    private final IMergable<WeightRecord> _weightRecordWriter;
    private final IDateTimeProvider _dateTimeProvider;
    private final IDateTimeConverter _dateTimeConverter;
    private final INumericValueConverter _numericValueConverter;
    private final ILogger _logger;

    private final UUID _recordIdentifier;
    private final boolean _recordLoaded;

    private Double _weightValue;
    private String _weightText;
    private boolean _isWeightInvalid;

    private WeightUnit _weightUnit;
    private LocalDateTime _dateTimeOfMeasurement;

    private boolean _canValuesBeSaved;

    public WeightMergeViewModel(
        final IQueryableById<WeightRecord> weightRecordReader,
        final IMergable<WeightRecord> weightRecordWriter,
        final IPreferredUnitRepository preferredUnitRepository,
        final IDateTimeProvider dateTimeProvider,
        final IDateTimeConverter dateTimeConverter,
        final INumericValueConverter numericValueConverter,
        final ILogger logger,
        final UUID weightRecordIdentifier)
    {
        _weightRecordWriter = weightRecordWriter;
        _dateTimeProvider = dateTimeProvider;
        _dateTimeConverter = dateTimeConverter;
        _numericValueConverter = numericValueConverter;
        _logger = logger;
        _recordIdentifier = weightRecordIdentifier;

        boolean recordLoaded= false;

        if (weightRecordIdentifier == null)
        {
            _weightValue = null;
            _weightUnit = preferredUnitRepository.GetPreferredMassUnit().ToDomainWeightUnit();
            _dateTimeOfMeasurement = _dateTimeProvider.Now();
        }
        else
        {
            try
            {
                WeightRecord record = weightRecordReader.GetRecord(weightRecordIdentifier);

                _weightValue = record.Value;
                _weightUnit = record.Unit;
                _dateTimeOfMeasurement = record.TimeOfMeasurement;

                recordLoaded = true;
            }
            catch (Exception exception)
            {
                _weightValue = null;
                _weightUnit = preferredUnitRepository.GetPreferredMassUnit().ToDomainWeightUnit();
                _dateTimeOfMeasurement = null;

                _logger.LogDebug(TAG, "Failed to load record.", exception);
            }
        }

        _recordLoaded = recordLoaded;

        _weightText = ConvertToString(_weightValue);
        _isWeightInvalid = false;
        _canValuesBeSaved = false;
    }

    /**
     * Gets if that view model edits an existing record or creates a new.
     *
     * @return
     *      {@code true} if an existing record is being edited;
     *      {@code false} if a new record is being created.
     */
    public boolean IsExistingRecordEdited()
    {
        return _recordIdentifier != null;
    }

    /**
     * Gets if the properties values can not be changed.
     *
     * @return
     *      {@code true} no property should be changed;
     *      {@code false} if properties can be changed.
     */
    public boolean IsReadOnly()
    {
        return IsExistingRecordEdited() && !_recordLoaded;
    }

    public String GetWeightString()
    {
        return _weightText;
    }

    public void SetWeight(String value)
    {
        if (IsReadOnly()) return;

        try
        {
            SetWeight(_numericValueConverter.ToDouble(value), value);
        }
        catch (Exception exception)
        {
            _logger.LogDebug(TAG, "Failed to convert weight value.", exception);
            SetWeight(null, value);
        }
    }

    private void SetWeight(final Double value, final String text)
    {
        if (Objects.equals(_weightValue, value)) return;

        _weightValue = value;
        _weightText = text != null ? text : ConvertToString(value);

        NotifyEventHandlers(IWeightMergeViewModelEventHandler::WeightChanged);
        SetWeightInvalid(value == null);
        ReevaluateIfValuesCanBeSaved();
    }

    public boolean IsWeightInvalid()
    {
        return _isWeightInvalid;
    }

    private void SetWeightInvalid(final boolean isInvalid)
    {
        if (_isWeightInvalid == isInvalid) return;

        _isWeightInvalid = isInvalid;
        NotifyEventHandlers(IWeightMergeViewModelEventHandler::WeightValidityChanged);
    }

    public WeightUnit GetWeightUnit()
    {
        return _weightUnit;
    }

    public void SetWeightUnit(final WeightUnit weightUnit)
    {
        if (IsReadOnly()) return;
        if (_weightUnit == weightUnit) return;

        ConvertWeightValue(_weightUnit, weightUnit);

        _weightUnit = weightUnit;

        NotifyEventHandlers(IWeightMergeViewModelEventHandler::WeightUnitChanged);
        ReevaluateIfValuesCanBeSaved();
    }

    private void ConvertWeightValue(
        final WeightUnit originalUnit,
        final WeightUnit newUnit)
    {
        if (_weightValue == null) return;

        SetWeight(WeightUnit.Convert(_weightValue, originalUnit, newUnit), null);
    }

    public LocalDateTime GetDateTimeOfMeasurement()
    {
        return _dateTimeOfMeasurement;
    }

    public String GetDateTimeOfMeasurementAsString()
    {
        if (_dateTimeOfMeasurement == null) return "(null)";

        try
        {
            return _dateTimeConverter.GetString(_dateTimeOfMeasurement);
        }
        catch (Exception exception)
        {
            _logger.LogDebug(TAG, "Failed to convert DateTime.", exception);
            return "(error)";
        }
    }

    public void SetDateTimeOfMeasurementToNow()
    {
        if (IsReadOnly()) return;

        final LocalDateTime now = _dateTimeProvider.Now();
        SetDateTimeOfMeasurement(now);
    }

    public void SetDateTimeOfMeasurement(final LocalDateTime dateTimeOfMeasurement)
    {
        if (IsReadOnly() ||
                dateTimeOfMeasurement == null ||
                _dateTimeOfMeasurement == dateTimeOfMeasurement) return;

        _dateTimeOfMeasurement = dateTimeOfMeasurement;

        NotifyEventHandlers(IWeightMergeViewModelEventHandler::DateTimeOfMeasurementChanged);
        ReevaluateIfValuesCanBeSaved();
    }

    public boolean CanValuesBeSaved()
    {
        return _canValuesBeSaved;
    }

    private void ReevaluateIfValuesCanBeSaved()
    {
        if (IsReadOnly()) return;

        final boolean canValuesBeSaved =
            _weightValue != null &&
            _dateTimeOfMeasurement != null;

        if (_canValuesBeSaved == canValuesBeSaved) return;

        _canValuesBeSaved = canValuesBeSaved;
        NotifyEventHandlers(IWeightMergeViewModelEventHandler::CanValuesBeSavedChanged);
    }

    public void Save()
    {
        if (IsReadOnly() || !CanValuesBeSaved()) return;

        final WeightRecord record;

        if (IsExistingRecordEdited())
        {
            record = new WeightRecord(
                    _recordIdentifier,
                    _weightValue,
                    _weightUnit,
                    _dateTimeOfMeasurement);
        }
        else
        {
            record = new WeightRecord(
                    _weightValue,
                    _weightUnit,
                    _dateTimeOfMeasurement);
        }

        try
        {
            _weightRecordWriter.CreateOrUpdateRecord(record);
        }
        catch (Exception exception)
        {
            _logger.LogDebug(TAG, "Failed to save record.", exception);
            NotifyEventHandlers(IWeightMergeViewModelEventHandler::RecordCouldNotBeSaved);

            return;
        }

        NotifyEventHandlers(IWeightMergeViewModelEventHandler::RecordSaved);
    }

    private String ConvertToString(Double value)
    {
        if (value == null) return "";

        return ConvertToString((double)value);
    }

    private String ConvertToString(double value)
    {
        return ConversionHelper.TryConvertToString(value, String.valueOf(value), _numericValueConverter);
    }

    /**
     * Represents an actor that can react to events of the {@link WeightMergeViewModel}.
     */
    public interface IWeightMergeViewModelEventHandler
    {
        /**
         * Called when the weight property changed.
         */
        void WeightChanged();

        /**
         * Called when the validity of the weight property changed.
         */
        void WeightValidityChanged();

        /**
         * Called when the weight-unit property changed.
         */
        void WeightUnitChanged();

        /**
         * Called when the date-time of measurement property changed.
         */
        void DateTimeOfMeasurementChanged();

        /**
         * Called if the state changes if {@link WeightMergeViewModel#Save()} has any effects.
         */
        void CanValuesBeSavedChanged();

        /**
         * Called when the record was saved.
         */
        void RecordSaved();

        /**
         * Called when the record could not be saved because of an error.
         */
        void RecordCouldNotBeSaved();
    }
}
