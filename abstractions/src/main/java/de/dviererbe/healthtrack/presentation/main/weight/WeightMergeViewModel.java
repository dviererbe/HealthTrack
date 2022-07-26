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
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import de.dviererbe.healthtrack.IDisposable;
import de.dviererbe.healthtrack.domain.WeightRecord;
import de.dviererbe.healthtrack.domain.WeightUnit;
import de.dviererbe.healthtrack.infrastructure.IDateTimeConverter;
import de.dviererbe.healthtrack.infrastructure.IDateTimeProvider;
import de.dviererbe.healthtrack.infrastructure.INavigationRouter;
import de.dviererbe.healthtrack.infrastructure.INumericValueConverter;
import de.dviererbe.healthtrack.persistence.IPreferredUnitRepository;
import de.dviererbe.healthtrack.persistence.IWeightWidgetRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public class WeightMergeViewModel implements IDisposable
{
    private static final String TAG = "WeightMergeViewModel";

    // DEPENDENCIES
    private final IWeightMergeView _view;
    private final INavigationRouter _navigationRouter;
    private final IWeightWidgetRepository _repository;
    private final IDateTimeProvider _dateTimeProvider;
    private final IDateTimeConverter _dateTimeConverter;
    private final INumericValueConverter _numericValueConverter;

    private final UUID _weightRecordIdentifier;
    private final boolean _recordLoaded;
    private final boolean _isEditView;

    private Double _weightValue;
    private WeightUnit _weightUnit;
    private LocalDateTime _dateTimeOfMeasurement;

    private final MutableLiveData<Boolean> _showValueErrorMessage;
    private final MutableLiveData<Boolean> _isSaveButtonEnabled;

    public final LiveData<Boolean> ShowValueErrorMessage;
    public final LiveData<Boolean> IsSaveButtonEnabled;

    public WeightMergeViewModel(
        final IWeightMergeView view,
        final INavigationRouter navigationRouter,
        final IWeightWidgetRepository repository,
        final IPreferredUnitRepository preferredUnitRepository,
        final IDateTimeProvider dateTimeProvider,
        final IDateTimeConverter dateTimeConverter,
        final INumericValueConverter numericValueConverter,
        final UUID weightRecordIdentifier)
    {
        _view = view;
        _navigationRouter = navigationRouter;
        _repository = repository;
        _dateTimeProvider = dateTimeProvider;
        _dateTimeConverter = dateTimeConverter;
        _numericValueConverter = numericValueConverter;
        _weightRecordIdentifier = weightRecordIdentifier;

        ShowValueErrorMessage =_showValueErrorMessage = new MutableLiveData<>(false);
        IsSaveButtonEnabled = _isSaveButtonEnabled = new MutableLiveData<>(false);

        boolean recordLoaded= false;

        if (weightRecordIdentifier == null)
        {
            _isEditView = false;

            _weightValue = null;
            _dateTimeOfMeasurement = _dateTimeProvider.Now();
        }
        else
        {
            _isEditView = true;

            try
            {
                WeightRecord record = _repository.GetRecord(weightRecordIdentifier);

                _weightValue = record.Value;
                _weightUnit = record.Unit;
                _dateTimeOfMeasurement = record.TimeOfMeasurement;

                recordLoaded = true;
            }
            catch (Exception exception)
            {
                _weightValue = null;
                _dateTimeOfMeasurement = null;

                Log.d(TAG, "Failed to load record.", exception);
            }
        }

        _weightUnit = preferredUnitRepository.GetPreferredMassUnit().ToDomainWeightUnit();
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

    public String GetWeightValue()
    {
        if (_weightValue == null) return "";

        try
        {
            return _numericValueConverter.ToString(_weightValue);
        }
        catch (Exception exception)
        {
            // fallback solution
            return String.valueOf(_weightValue);
        }
    }

    public void SetWeightValue(String value)
    {
        try
        {
            _weightValue = _numericValueConverter.ToDouble(value);
            _showValueErrorMessage.setValue(false);
        }
        catch (Exception exception)
        {
            Log.d(TAG, "Failed to convert weight value.", exception);
            _weightValue = null;
            _showValueErrorMessage.setValue(true);
        }

        TryEnableSaveButton();
    }

    public WeightUnit GetWeightUnit()
    {
        return _weightUnit;
    }

    public void SetWeightUnit(final WeightUnit weightUnit)
    {
        if (_weightUnit != weightUnit)
        {
            final WeightUnit source = _weightUnit;
            _weightUnit = weightUnit;

            if (_weightValue != null)
            {
                _weightValue = WeightUnit.Convert(_weightValue, source, weightUnit);
                _view.PullValues();
            }

            TryEnableSaveButton();
        }
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

    public void Save()
    {
        if (AreInputControlsDisabled()) return;

        final WeightRecord record;

        if (IsEditView())
        {
            record = new WeightRecord(
                    _weightRecordIdentifier,
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
            _repository.CreateOrUpdateRecord(record);
        }
        catch (Exception exception)
        {
            Log.d(TAG, "Failed to save record.", exception);
            NotifySaveFailure();
            return;
        }

        _navigationRouter.TryNavigateBack();
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

        _isSaveButtonEnabled.setValue(_weightValue != null && _dateTimeOfMeasurement != null);
    }

    /**
     * Performs application-defined tasks associated with freeing, releasing, or resetting resources.
     */
    @Override
    public void Dispose()
    {
    }

    /**
     * Interface for the merge weight record user interface.
     */
    public interface IWeightMergeView
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
         * Callback mechanism for when the pick date time dialog exits.
         */
        interface IPickDateTimeDialogObserver
        {
            /**
             * Called when the dialog exits.
             *
             * @param pickedDateTime the date &amp; time the user picked; {@code null} when dialog was cancelled.
             */
            void OnComplete(LocalDateTime pickedDateTime);
        }
    }
}
