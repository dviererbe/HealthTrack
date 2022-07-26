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
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import de.dviererbe.healthtrack.IDisposable;
import de.dviererbe.healthtrack.domain.StepCountRecord;
import de.dviererbe.healthtrack.infrastructure.IDateTimeConverter;
import de.dviererbe.healthtrack.infrastructure.IDateTimeProvider;
import de.dviererbe.healthtrack.infrastructure.INavigationRouter;
import de.dviererbe.healthtrack.infrastructure.INumericValueConverter;
import de.dviererbe.healthtrack.persistence.IStepWidgetRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class StepCountMergeViewModel implements IDisposable
{
    private static final String TAG = "StepCountMergeViewModel";

    private final IStepCountMergeView _view;
    private final INavigationRouter _navigationRouter;
    private final IStepWidgetRepository _repository;
    private final IDateTimeProvider _dateTimeProvider;
    private final IDateTimeConverter _dateTimeConverter;
    private final INumericValueConverter _numericValueConverter;

    private final LocalDate _day;
    private final boolean _recordLoaded;
    private final boolean _isEditView;

    private Integer _stepCount;
    private Integer _goal;
    private LocalDateTime _dateTimeOfMeasurement;

    private final MutableLiveData<Boolean> _showStepCountInvalidErrorMessage;
    private final MutableLiveData<Boolean> _showGoalInvalidErrorMessage;
    private final MutableLiveData<Boolean> _isSaveButtonEnabled;

    public final LiveData<Boolean> ShowStepCountInvalidErrorMessage;
    public final LiveData<Boolean> ShowGoalInvalidErrorMessage;
    public final LiveData<Boolean> IsSaveButtonEnabled;

    public StepCountMergeViewModel(
        final IStepCountMergeView view,
        final INavigationRouter navigationRouter,
        final IStepWidgetRepository repository,
        final IDateTimeProvider dateTimeProvider,
        final IDateTimeConverter dateTimeConverter,
        final INumericValueConverter numericValueConverter,
        final LocalDate day)
    {
        _view = view;
        _navigationRouter = navigationRouter;
        _repository = repository;
        _dateTimeProvider = dateTimeProvider;
        _dateTimeConverter = dateTimeConverter;
        _numericValueConverter = numericValueConverter;
        _day = day;

        ShowStepCountInvalidErrorMessage = _showStepCountInvalidErrorMessage = new MutableLiveData<>(false);
        ShowGoalInvalidErrorMessage = _showGoalInvalidErrorMessage = new MutableLiveData<>(false);
        IsSaveButtonEnabled = _isSaveButtonEnabled = new MutableLiveData<>(false);

        boolean recordLoaded= false;

        if (_day == null)
        {
            _isEditView = false;

            _stepCount = null;
            _dateTimeOfMeasurement = _dateTimeProvider.Now();

            try
            {
                _goal = _repository.GetDefaultStepCountGoal();
            }
            catch (Exception exception)
            {
                _goal = null;
            }
        }
        else
        {
            _isEditView = true;

            try
            {
                StepCountRecord record = _repository.GetRecordForDay(_day);

                _stepCount = record.StepCount;
                _goal = record.Goal;
                _dateTimeOfMeasurement = record.TimeOfMeasurement;

                recordLoaded = true;
            }
            catch (Exception exception)
            {
                _stepCount = null;
                _goal = null;
                _dateTimeOfMeasurement = null;
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

    public String GetStepCountValue()
    {
        if (_stepCount == null) return "";

        try
        {
            return _numericValueConverter.ToString(_stepCount);
        }
        catch (Exception exception)
        {
            // fallback solution
            return String.valueOf(_stepCount);
        }
    }

    public void SetStepCountValue(String value)
    {
        try
        {
            _stepCount = _numericValueConverter.ToInteger(value);
            _showStepCountInvalidErrorMessage.setValue(false);
        }
        catch (Exception exception)
        {
            Log.d(TAG, "Failed to convert step count value.", exception);
            _stepCount = null;
            _showStepCountInvalidErrorMessage.setValue(true);
        }

        TryEnableSaveButton();
    }

    public String GetGoalValue()
    {
        if (_goal == null) return "";

        try
        {
            return _numericValueConverter.ToString(_goal);
        }
        catch (Exception exception)
        {
            // fallback solution
            return String.valueOf(_goal);
        }
    }

    public void SetGoalValue(String value)
    {
        try
        {
            _goal = _numericValueConverter.ToInteger(value);
            _showGoalInvalidErrorMessage.setValue(false);
        }
        catch (Exception exception)
        {
            Log.d(TAG, "Failed to convert goal value.", exception);
            _goal = null;
            _showGoalInvalidErrorMessage.setValue(true);
        }

        TryEnableSaveButton();
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

        final StepCountRecord record = new StepCountRecord(
                _stepCount,
                _goal,
                _dateTimeOfMeasurement);

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

        _isSaveButtonEnabled.setValue(_stepCount != null && _goal != null && _dateTimeOfMeasurement != null);
    }

    /**
     * Performs application-defined tasks associated with freeing, releasing, or resetting resources.
     */
    @Override
    public void Dispose()
    {
    }

    /**
     * Interface for a step count record merger user interface.
     */
    public interface IStepCountMergeView
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
