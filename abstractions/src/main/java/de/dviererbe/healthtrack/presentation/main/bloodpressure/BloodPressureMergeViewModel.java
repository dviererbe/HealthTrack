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

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import de.dviererbe.healthtrack.IDisposable;
import de.dviererbe.healthtrack.domain.BloodPressureRecord;
import de.dviererbe.healthtrack.domain.BloodPressureUnit;
import de.dviererbe.healthtrack.domain.MedicationState;
import de.dviererbe.healthtrack.infrastructure.IDateTimeConverter;
import de.dviererbe.healthtrack.infrastructure.IDateTimeProvider;
import de.dviererbe.healthtrack.infrastructure.INumericValueConverter;
import de.dviererbe.healthtrack.persistence.IBloodPressureWidgetRepository;
import de.dviererbe.healthtrack.persistence.IPreferredUnitRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public class BloodPressureMergeViewModel implements IDisposable
{
    private static final String TAG = "BloodPressureMergeViewModel";

    private final IBloodPressureMergeView _view;
    private final IBloodPressureWidgetRepository _repository;
    private final IDateTimeProvider _dateTimeProvider;
    private final IDateTimeConverter _dateTimeConverter;
    private final INumericValueConverter _numericValueConverter;
    private final UUID _recordIdentifier;
    private final boolean _recordLoaded;
    private final boolean _isEditView;

    private Integer _systolicValue;
    private Integer _diastolicValue;
    private BloodPressureUnit _bloodPressureUnit;
    private Integer _pulsValue;
    private MedicationState _medicationValue;
    private LocalDateTime _dateTimeOfMeasurement;
    private String _note;

    private final MutableLiveData<Boolean> _showSystolicValueErrorMessage;
    private final MutableLiveData<Boolean> _showDiastolicValueErrorMessage;
    private final MutableLiveData<Boolean> _showPulsValueErrorMessage;
    private final MutableLiveData<Boolean> _isSaveButtonEnabled;

    public final LiveData<Boolean> ShowSystolicValueErrorMessage;
    public final LiveData<Boolean> ShowDiastolicValueErrorMessage;
    public final LiveData<Boolean> ShowPulsValueErrorMessage;
    public final LiveData<Boolean> IsSaveButtonEnabled;

    public BloodPressureMergeViewModel(
            final IBloodPressureMergeView view,
            final IBloodPressureWidgetRepository repository,
            final IDateTimeProvider dateTimeProvider,
            final IDateTimeConverter dateTimeConverter,
            final INumericValueConverter numericValueConverter,
            final IPreferredUnitRepository preferredUnitRepository,
            final UUID recordIdentifier)
    {
        _view = view;
        _repository = repository;
        _dateTimeProvider = dateTimeProvider;
        _dateTimeConverter = dateTimeConverter;
        _numericValueConverter = numericValueConverter;
        _recordIdentifier = recordIdentifier;

        ShowSystolicValueErrorMessage =_showSystolicValueErrorMessage = new MutableLiveData<>(false);
        ShowDiastolicValueErrorMessage = _showDiastolicValueErrorMessage = new MutableLiveData<>(false);
        ShowPulsValueErrorMessage = _showPulsValueErrorMessage = new MutableLiveData<>(false);
        IsSaveButtonEnabled = _isSaveButtonEnabled = new MutableLiveData<>(false);

        boolean recordLoaded= false;

        if (recordIdentifier == null)
        {
            _isEditView = false;

            _systolicValue = null;
            _diastolicValue = null;
            _bloodPressureUnit = preferredUnitRepository.GetPreferredBloodPressureUnit().ToDomainBloodPressureUnit();
            _pulsValue = null;
            _medicationValue = MedicationState.None;
            _dateTimeOfMeasurement = dateTimeProvider.Now();
            _note = "";
        }
        else
        {
            _isEditView = true;

            try
            {
                BloodPressureRecord record = _repository.GetRecord(recordIdentifier);

                _systolicValue = record.Systolic;
                _diastolicValue = record.Diastolic;
                _bloodPressureUnit = record.Unit;
                _pulsValue = record.Pulse;
                _medicationValue = record.Medication;
                _dateTimeOfMeasurement = record.TimeOfMeasurement;
                _note = record.Note;

                recordLoaded = true;
            }
            catch (Exception exception)
            {
                Log.d(TAG, "Failed to load record.", exception);
            }
        }

        _recordLoaded = recordLoaded;
    }

    public boolean IsEditView()
    {
        return _isEditView;
    }

    public boolean AreInputControlsDisabled()
    {
        return _isEditView && !_recordLoaded;
    }

    public String GetSystolicValue()
    {
        if (_systolicValue == null) return "";

        try
        {
            return _numericValueConverter.ToString(_systolicValue);
        }
        catch (Exception exception)
        {
            // fallback solution
            return String.valueOf(_systolicValue);
        }
    }

    public void SetSystolicValue(String value)
    {
        try
        {
            _systolicValue = _numericValueConverter.ToInteger(value);
            _showSystolicValueErrorMessage.setValue(false);
        }
        catch (Exception exception)
        {
            Log.d(TAG, "Failed to convert systolic value.", exception);
            _systolicValue = null;
            _showSystolicValueErrorMessage.setValue(true);
        }

        TryEnableSaveButton();
    }

    public String GetDiastolicValue()
    {
        if (_diastolicValue == null) return "";

        try
        {
            return _numericValueConverter.ToString(_diastolicValue);
        }
        catch (Exception exception)
        {
            // fallback solution
            return String.valueOf(_diastolicValue);
        }
    }

    public void SetDiastolicValue(String value)
    {
        try
        {
            _diastolicValue = _numericValueConverter.ToInteger(value);
            _showDiastolicValueErrorMessage.setValue(false);
        }
        catch (Exception exception)
        {
            Log.d(TAG, "Failed to convert diastolic value.", exception);
            _diastolicValue = null;
            _showDiastolicValueErrorMessage.setValue(true);
        }

        TryEnableSaveButton();
    }

    public BloodPressureUnit GetBloodPressureUnit()
    {
        return _bloodPressureUnit;
    }

    public void SetBloodPressureUnit(final BloodPressureUnit bloodPressureUnit)
    {
        if (_bloodPressureUnit != bloodPressureUnit)
        {
            final BloodPressureUnit source = _bloodPressureUnit;
            _bloodPressureUnit = bloodPressureUnit;

            boolean valueConverted = false;

            if (_systolicValue != null)
            {
                _systolicValue = BloodPressureUnit.Convert(_systolicValue, source, bloodPressureUnit);
                valueConverted = true;
            }

            if (_diastolicValue != null)
            {
                _diastolicValue = BloodPressureUnit.Convert(_diastolicValue, source, bloodPressureUnit);
                valueConverted = true;
            }

            if (valueConverted)
            {
                _view.PullValues();
            }

            TryEnableSaveButton();
        }
    }

    public String GetPulsValue()
    {
        if (_pulsValue == null) return "";

        try
        {
            return _numericValueConverter.ToString(_pulsValue);
        }
        catch (Exception exception)
        {
            // fallback solution
            return String.valueOf(_pulsValue);
        }
    }

    public void SetPulsValue(String value)
    {
        try
        {
            _pulsValue = _numericValueConverter.ToInteger(value);
            _showPulsValueErrorMessage.setValue(false);
        }
        catch (Exception exception)
        {
            Log.d(TAG, "Failed to convert puls value.", exception);
            _pulsValue = null;
            _showPulsValueErrorMessage.setValue(true);
        }

        TryEnableSaveButton();
    }

    public MedicationState GetMedicationValue()
    {
        return _medicationValue;
    }

    public void SetMedication(MedicationState medicationValue)
    {
        if (medicationValue == null) medicationValue = MedicationState.None;

        _medicationValue = medicationValue;
    }

    public LocalDateTime GetDateTime()
    {
        return _dateTimeOfMeasurement;
    }

    public String GetDateTimeString()
    {
        if (_dateTimeOfMeasurement == null) return "(null)";

        try
        {
            return _dateTimeConverter.GetString(_dateTimeOfMeasurement);
        }
        catch (Exception exception)
        {
            Log.d(TAG, "Failed to convert DateTime.", exception);
            return "(error)";
        }
    }

    public void PickDateAndTime()
    {
        _view.PickDateTimeDialog((dateTime) ->
             {
                 if (dateTime == null) return;

                 _dateTimeOfMeasurement = dateTime;
                 _view.PullValues();
                 TryEnableSaveButton();
             });
    }

    public void SetDateTimeToNow()
    {
        _dateTimeOfMeasurement = _dateTimeProvider.Now();
        _view.PullValues();
        TryEnableSaveButton();
    }

    public String GetNote()
    {
        return _note;
    }

    public void SetNote(String text)
    {
        if (text == null) text = "";

        _note = text;
    }

    public void Save()
    {
        if (AreInputControlsDisabled()) return;

        final BloodPressureRecord record;

        if (_note == null) _note = "";

        if (IsEditView())
        {
            record = new BloodPressureRecord(
                    _recordIdentifier,
                    _systolicValue,
                    _diastolicValue,
                    _bloodPressureUnit,
                    _pulsValue,
                    _medicationValue,
                    _dateTimeOfMeasurement,
                    _note);
        }
        else
        {
            record = new BloodPressureRecord(
                    _systolicValue,
                    _diastolicValue,
                    _bloodPressureUnit,
                    _pulsValue,
                    _medicationValue,
                    _dateTimeOfMeasurement,
                    _note);
        }

        try
        {
            _repository.CreateOrUpdateRecord(record);
        }
        catch (Exception exception)
        {
            Log.d(TAG, "Failed to save record.", exception);
            NotifySaveFailure();
            return;
        }

        _view.GoBack();
    }

    private void NotifySaveFailure()
    {
        if (IsEditView())
            _view.NotifyUserThatRecordCouldNotBeUpdated();
        else
            _view.NotifyUserThatRecordCouldNotBeCreated();
    }

    private void TryEnableSaveButton()
    {
        if (AreInputControlsDisabled()) return;

        final boolean error =
            _systolicValue == null ||
            _diastolicValue == null ||
            _bloodPressureUnit == null ||
            _pulsValue == null ||
            _medicationValue == null ||
            _dateTimeOfMeasurement == null ||
            _note == null;

        _isSaveButtonEnabled.setValue(!error);
    }

    /**
     * Performs application-defined tasks associated with freeing, releasing, or resetting resources.
     */
    @Override
    public void Dispose()
    {

    }

    /**
     * Interface for a blood pressure record merger user interface.
     */
    public interface IBloodPressureMergeView
    {
        /**
         * Notifies the user that the record could not be created because of an error.
         */
        void NotifyUserThatRecordCouldNotBeCreated();

        /**
         * Notifies the user that the record could not be updated because of an error.
         */
        void NotifyUserThatRecordCouldNotBeUpdated();

        /**
         * Shows the user a dialog to pick a date and time.
         *
         * @param callback a reference to a callback mechanism when the user made a decision.
         */
        void PickDateTimeDialog(IPickDateTimeDialogObserver callback);

        /**
         * Notifies the view that it should update its values.
         */
        void PullValues();

        /**
         * The view should navigate up in the navigation stack.
         */
        void GoBack();

        /**
         * Callback mechanism for when the pick date time dialog exits.
         */
        interface IPickDateTimeDialogObserver
        {
            /**
             * Called when the dialog exits.
             *
             * @param pickedDateTime the date & time the user picked; {@code null} when dialog was cancelled.
             */
            void OnComplete(LocalDateTime pickedDateTime);
        }
    }
}
