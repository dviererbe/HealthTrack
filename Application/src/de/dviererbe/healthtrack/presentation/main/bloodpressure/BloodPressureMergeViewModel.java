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

import de.dviererbe.healthtrack.domain.BloodPressureRecord;
import de.dviererbe.healthtrack.domain.BloodPressureUnit;
import de.dviererbe.healthtrack.domain.MedicationState;
import de.dviererbe.healthtrack.infrastructure.*;
import de.dviererbe.healthtrack.persistence.IBloodPressureWidgetRepository;
import de.dviererbe.healthtrack.persistence.IPreferredUnitRepository;
import de.dviererbe.healthtrack.presentation.ConversionHelper;
import de.dviererbe.healthtrack.presentation.ViewModel;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class BloodPressureMergeViewModel extends ViewModel<BloodPressureMergeViewModel.IBloodPressureMergeViewModelEventHandler>
{
    private static final String TAG = "BloodPressureMergeViewModel";

    private final IBloodPressureWidgetRepository _repository;
    private final IDateTimeProvider _dateTimeProvider;
    private final IDateTimeConverter _dateTimeConverter;
    private final INumericValueConverter _numericValueConverter;
    private final ILogger _logger;
    private final UUID _recordIdentifier;
    private final boolean _recordLoaded;

    private Integer _systolicValue;
    private String _systolicText;
    private boolean _isSystolicValueInvalid;
    private Integer _diastolicValue;
    private String _diastolicText;
    private boolean _isDiastolicValueInvalid;
    private BloodPressureUnit _bloodPressureUnit;
    private Integer _pulseValue;
    private String _pulseText;
    private boolean _isPulseValueInvalid;
    private MedicationState _medicationState;
    private LocalDateTime _dateTimeOfMeasurement;
    private String _note;

    private boolean _canValuesBeSaved;

    public BloodPressureMergeViewModel(
            final IBloodPressureWidgetRepository repository,
            final IDateTimeProvider dateTimeProvider,
            final IDateTimeConverter dateTimeConverter,
            final INumericValueConverter numericValueConverter,
            final IPreferredUnitRepository preferredUnitRepository,
            final ILogger logger,
            final UUID recordIdentifier)
    {
        _repository = repository;
        _dateTimeProvider = dateTimeProvider;
        _dateTimeConverter = dateTimeConverter;
        _numericValueConverter = numericValueConverter;
        _logger = logger;
        _recordIdentifier = recordIdentifier;

        boolean recordLoaded= false;

        if (recordIdentifier == null)
        {
            _systolicValue = null;
            _diastolicValue = null;
            _bloodPressureUnit = preferredUnitRepository.GetPreferredBloodPressureUnit().ToDomainBloodPressureUnit();
            _pulseValue = null;
            _medicationState = MedicationState.None;
            _dateTimeOfMeasurement = dateTimeProvider.Now();
            _note = "";
        }
        else
        {
            try
            {
                BloodPressureRecord record = _repository.GetRecord(recordIdentifier);

                _systolicValue = record.Systolic;
                _diastolicValue = record.Diastolic;
                _bloodPressureUnit = record.Unit;
                _pulseValue = record.Pulse;
                _medicationState = record.Medication;
                _dateTimeOfMeasurement = record.TimeOfMeasurement;
                _note = record.Note;

                recordLoaded = true;
            }
            catch (Exception exception)
            {
                _logger.LogDebug(TAG, "Failed to load record.", exception);
            }
        }

        _recordLoaded = recordLoaded;

        _systolicText = ConvertToString(_systolicValue);
        _diastolicText = ConvertToString(_diastolicValue);
        _pulseText = ConvertToString(_pulseValue);
        _isSystolicValueInvalid = false;
        _isDiastolicValueInvalid = false;
        _isPulseValueInvalid = false;
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

    /**
     * Gets the systolic value as a {@link String}.
     *
     * @return {@link String} representation of the systolic value.
     */
    public String GetSystolicString()
    {
        return _systolicText;
    }

    public void SetSystolic(final String value)
    {
        if (IsReadOnly()) return;

        try
        {
            SetSystolic(_numericValueConverter.ToInteger(value), value);
        }
        catch (Exception exception)
        {
            _logger.LogDebug(TAG, "Failed to convert systolic value.", exception);
            SetSystolic(null, value);
        }
    }

    private void SetSystolic(final Integer value, final String text)
    {
        if (Objects.equals(_systolicValue, value)) return;

        _systolicValue = value;
        _systolicText = text != null ? text : ConvertToString(value);

        NotifyEventHandlers(IBloodPressureMergeViewModelEventHandler::SystolicValueChanged);
        SetSystolicInvalid(value == null);
        ReevaluateIfValuesCanBeSaved();
    }

    public boolean IsSystolicValueInvalid()
    {
        return _systolicValue == null;
    }

    private void SetSystolicInvalid(final boolean isInvalid)
    {
        if (_isSystolicValueInvalid == isInvalid) return;

        _isSystolicValueInvalid = isInvalid;
        NotifyEventHandlers(IBloodPressureMergeViewModelEventHandler::SystolicValueValidityChanged);
    }

    /**
     * Gets the diastolic value as a {@link String}.
     *
     * @return {@link String} representation of the diastolic value.
     */
    public String GetDiastolicString()
    {
        return _diastolicText;
    }

    public void SetDiastolic(final String value)
    {
        if (IsReadOnly()) return;

        try
        {
            SetDiastolic(_numericValueConverter.ToInteger(value), value);
        }
        catch (Exception exception)
        {
            _logger.LogDebug(TAG, "Failed to convert diastolic value.", exception);
            SetDiastolic(null, value);
        }
    }

    private void SetDiastolic(final Integer value, final String text)
    {
        if (Objects.equals(_diastolicValue, value)) return;

        _diastolicValue = value;
        _diastolicText = text != null ? text : ConvertToString(value);

        NotifyEventHandlers(IBloodPressureMergeViewModelEventHandler::DiastolicValueChanged);
        SetDiastolicInvalid(value == null);
        ReevaluateIfValuesCanBeSaved();
    }

    public boolean IsDiastolicValueInvalid()
    {
        return _isDiastolicValueInvalid;
    }

    private void SetDiastolicInvalid(final boolean isInvalid)
    {
        if (_isDiastolicValueInvalid == isInvalid) return;

        _isDiastolicValueInvalid = isInvalid;
        NotifyEventHandlers(IBloodPressureMergeViewModelEventHandler::DiastolicValueValidityChanged);
    }

    public BloodPressureUnit GetBloodPressureUnit()
    {
        return _bloodPressureUnit;
    }

    public void SetBloodPressureUnit(final BloodPressureUnit bloodPressureUnit)
    {
        if (IsReadOnly()) return;
        if (_bloodPressureUnit == bloodPressureUnit) return;

        ConvertBloodPressureValues(_bloodPressureUnit, bloodPressureUnit);

        _bloodPressureUnit = bloodPressureUnit;

        NotifyEventHandlers(IBloodPressureMergeViewModelEventHandler::BloodPressureUnitChanged);
        ReevaluateIfValuesCanBeSaved();
    }

    private void ConvertBloodPressureValues(
        final BloodPressureUnit originalUnit,
        final BloodPressureUnit newUnit)
    {
        if (_systolicValue != null)
        {
            SetSystolic((int)Math.round(BloodPressureUnit.Convert(_systolicValue, originalUnit, newUnit)), null);
        }

        if (_diastolicValue != null)
        {
            SetDiastolic((int)Math.round(BloodPressureUnit.Convert(_diastolicValue, originalUnit, newUnit)), null);
        }
    }

    public String GetPulseString()
    {
        return _pulseText;
    }

    public void SetPulse(String value)
    {
        if (IsReadOnly()) return;

        try
        {
            SetPulse(_numericValueConverter.ToInteger(value), value);
        }
        catch (Exception exception)
        {
            _logger.LogDebug(TAG, "Failed to convert pulse value.", exception);
            SetPulse(null, value);
        }
    }

    private void SetPulse(final Integer value, final String text)
    {
        if (Objects.equals(_pulseValue, value)) return;

        _pulseValue = value;
        _pulseText = text != null ? text : ConvertToString(value);

        NotifyEventHandlers(IBloodPressureMergeViewModelEventHandler::PulseValueChanged);
        SetPulseInvalid(value == null);
        ReevaluateIfValuesCanBeSaved();
    }

    public boolean IsPulseValueInvalid()
    {
        return _isPulseValueInvalid;
    }

    private void SetPulseInvalid(final boolean isInvalid)
    {
        if (_isPulseValueInvalid == isInvalid) return;

        _isPulseValueInvalid = isInvalid;
        NotifyEventHandlers(IBloodPressureMergeViewModelEventHandler::PulseValueValidityChanged);
    }

    public MedicationState GetMedicationState()
    {
        return _medicationState;
    }

    public void SetMedicationState(final MedicationState medicationState)
    {
        if (IsReadOnly()) return;

        if (medicationState == null)
        {
            SetMedicationState(MedicationState.None);
            return;
        }

        if (_medicationState == medicationState) return;

        _medicationState = medicationState;
        NotifyEventHandlers(IBloodPressureMergeViewModelEventHandler::MedicationStateChanged);
        ReevaluateIfValuesCanBeSaved();
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

        NotifyEventHandlers(IBloodPressureMergeViewModelEventHandler::DateTimeOfMeasurementChanged);
        ReevaluateIfValuesCanBeSaved();
    }

    public String GetNote()
    {
        return _note;
    }

    public void SetNote(final String text)
    {
        if (text == null)
        {
            SetNote("");
            return;
        }

        if (IsReadOnly() || _note.equals(text)) return;

        _note = text;
        NotifyEventHandlers(IBloodPressureMergeViewModelEventHandler::NoteChanged);
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
            _systolicValue != null &&
            _diastolicValue != null &&
            _bloodPressureUnit != null &&
            _pulseValue != null &&
            _medicationState != null &&
            _dateTimeOfMeasurement != null &&
            _note != null;

        if (_canValuesBeSaved == canValuesBeSaved) return;

        _canValuesBeSaved = canValuesBeSaved;
        NotifyEventHandlers(IBloodPressureMergeViewModelEventHandler::CanValuesBeSavedChanged);
    }

    public void Save()
    {
        if (IsReadOnly() || !CanValuesBeSaved()) return;

        final BloodPressureRecord record;

        if (IsExistingRecordEdited())
        {
            record = new BloodPressureRecord(
                    _recordIdentifier,
                    _systolicValue,
                    _diastolicValue,
                    _bloodPressureUnit,
                    _pulseValue,
                    _medicationState,
                    _dateTimeOfMeasurement,
                    _note);
        }
        else
        {
            record = new BloodPressureRecord(
                    _systolicValue,
                    _diastolicValue,
                    _bloodPressureUnit,
                    _pulseValue,
                    _medicationState,
                    _dateTimeOfMeasurement,
                    _note);
        }

        try
        {
            _repository.CreateOrUpdateRecord(record);
        }
        catch (Exception exception)
        {
            _logger.LogDebug(TAG, "Failed to save record.", exception);
            NotifyEventHandlers(IBloodPressureMergeViewModelEventHandler::RecordCouldNotBeSaved);

            return;
        }

        NotifyEventHandlers(IBloodPressureMergeViewModelEventHandler::RecordSaved);
    }

    private String ConvertToString(Integer value)
    {
        if (value == null) return "";

        return ConvertToString((int)value);
    }

    private String ConvertToString(int value)
    {
        return ConversionHelper.TryConvertToString(value, String.valueOf(value), _numericValueConverter);
    }

    /**
     * Represents an actor that can react to events of the {@link BloodPressureMergeViewModel}.
     */
    public interface IBloodPressureMergeViewModelEventHandler
    {
        /**
         * Called when the systolic property changed.
         */
        void SystolicValueChanged();

        /**
         * Called when the validity of the systolic property changed.
         */
        void SystolicValueValidityChanged();

        /**
         * Called when the diastolic property changed.
         */
        void DiastolicValueChanged();

        /**
         * Called when the validity of the diastolic property changed.
         */
        void DiastolicValueValidityChanged();

        /**
         * Called when the blood-pressure unit property changed.
         */
        void BloodPressureUnitChanged();

        /**
         * Called when the pulse property changed.
         */
        void PulseValueChanged();

        /**
         * Called when the validity of the pulse property changed.
         */
        void PulseValueValidityChanged();

        /**
         * Called when the medication state property changed.
         */
        void MedicationStateChanged();

        /**
         * Called when the date-time of measurement property changed.
         */
        void DateTimeOfMeasurementChanged();

        /**
         * Called when the note property changed.
         */
        void NoteChanged();

        /**
         * Called if the state changes if {@link BloodPressureMergeViewModel#Save()} has any effects.
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
