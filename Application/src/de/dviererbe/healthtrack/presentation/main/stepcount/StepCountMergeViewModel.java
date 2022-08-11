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

import de.dviererbe.healthtrack.domain.StepCountRecord;
import de.dviererbe.healthtrack.infrastructure.IDateTimeConverter;
import de.dviererbe.healthtrack.infrastructure.IDateTimeProvider;
import de.dviererbe.healthtrack.infrastructure.ILogger;
import de.dviererbe.healthtrack.infrastructure.INumericValueConverter;
import de.dviererbe.healthtrack.persistence.IDefaultStepCountGoalGetter;
import de.dviererbe.healthtrack.persistence.IMergable;
import de.dviererbe.healthtrack.persistence.IQueryableById;
import de.dviererbe.healthtrack.presentation.ConversionHelper;
import de.dviererbe.healthtrack.presentation.ViewModel;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class StepCountMergeViewModel extends ViewModel<StepCountMergeViewModel.IStepCountMergeViewModelEventHandler>
{
    private static final String TAG = "StepCountMergeViewModel";

    // DEPENDENCIES:
    private final IMergable<StepCountRecord> _stepCountRecordWriter;
    private final IDateTimeProvider _dateTimeProvider;
    private final IDateTimeConverter _dateTimeConverter;
    private final INumericValueConverter _numericValueConverter;
    private final ILogger _logger;

    private final UUID _recordIdentifier;
    private final boolean _recordLoaded;

    private Integer _stepCount;
    private String _stepCountText;
    private boolean _isStepCountInvalid;
    private Integer _goal;
    private String _goalText;
    private boolean _isGoalInvalid;
    private LocalDateTime _dateTimeOfMeasurement;

    private boolean _canValuesBeSaved;

    public StepCountMergeViewModel(
        final IQueryableById<StepCountRecord> stepCountRecordReader,
        final IDefaultStepCountGoalGetter defaultStepCountGoalGetter,
        final IMergable<StepCountRecord> stepCountRecordWriter,
        final IDateTimeProvider dateTimeProvider,
        final IDateTimeConverter dateTimeConverter,
        final INumericValueConverter numericValueConverter,
        final ILogger logger,
        final UUID recordIdentifier)
    {
        _stepCountRecordWriter = stepCountRecordWriter;
        _dateTimeProvider = dateTimeProvider;
        _dateTimeConverter = dateTimeConverter;
        _numericValueConverter = numericValueConverter;
        _logger = logger;
        _recordIdentifier = recordIdentifier;

        boolean recordLoaded;

        if (recordIdentifier == null)
        {
            recordLoaded = false;

            _stepCount = null;
            _goal = TryGetDefaultStepCountGoal(defaultStepCountGoalGetter);
            _dateTimeOfMeasurement = dateTimeProvider.Now();
        }
        else
        {
            try
            {
                StepCountRecord record = stepCountRecordReader.GetRecord(recordIdentifier);

                _stepCount = record.StepCount;
                _goal = record.Goal;
                _dateTimeOfMeasurement = record.TimeOfMeasurement;

                recordLoaded = true;
            }
            catch (Exception exception)
            {
                _logger.LogDebug(TAG, "Failed to load record.", exception);

                recordLoaded = false;

                _stepCount = null;
                _goal = null;
                _dateTimeOfMeasurement = null;
            }
        }

        _recordLoaded = recordLoaded;

        _goalText = ConvertToString(_goal);
        _stepCountText = ConvertToString(_stepCount);
        _isStepCountInvalid = false;
        _isGoalInvalid = false;
        _canValuesBeSaved = false;
    }

    private static Integer TryGetDefaultStepCountGoal(final IDefaultStepCountGoalGetter defaultStepCountGoalGetter)
    {
        try
        {
            return defaultStepCountGoalGetter.GetDefaultStepCountGoal();
        }
        catch (Exception exception)
        {
            return null;
        }
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

    public String GetStepCountString()
    {
        return _stepCountText;
    }

    public void SetStepCount(final String value)
    {
        if (IsReadOnly()) return;

        try
        {
            SetStepCount(_numericValueConverter.ToInteger(value), value);
        }
        catch (Exception exception)
        {
            _logger.LogDebug(TAG, "Failed to convert step count value.", exception);
            SetStepCount(null, value);
        }
    }

    private void SetStepCount(final Integer value, final String text)
    {
        if (Objects.equals(_stepCount, value)) return;

        _stepCount = value;
        _stepCountText = text != null ? text : ConvertToString(value);

        NotifyEventHandlers(IStepCountMergeViewModelEventHandler::StepCountValueChanged);
        SetStepCountInvalid(value == null);
        ReevaluateIfValuesCanBeSaved();
    }

    public boolean IsStepCountInvalid()
    {
        return _isStepCountInvalid;
    }

    private void SetStepCountInvalid(final boolean isInvalid)
    {
        if (_isStepCountInvalid == isInvalid) return;

        _isStepCountInvalid = isInvalid;
        NotifyEventHandlers(IStepCountMergeViewModelEventHandler::StepCountValueValidityChanged);
    }

    public String GetGoalString()
    {
        return _goalText;
    }

    public void SetGoal(String value)
    {
        if (IsReadOnly()) return;

        try
        {
            SetGoal(_numericValueConverter.ToInteger(value), value);
        }
        catch (Exception exception)
        {
            _logger.LogDebug(TAG, "Failed to convert step count goal value.", exception);
            SetGoal(null, value);
        }
    }

    private void SetGoal(final Integer value, final String text)
    {
        if (Objects.equals(_goal, value)) return;

        _goal = value;
        _goalText = text != null ? text : ConvertToString(value);

        NotifyEventHandlers(IStepCountMergeViewModelEventHandler::GoalValueChanged);
        SetGoalInvalid(value == null);
        ReevaluateIfValuesCanBeSaved();
    }

    public boolean IsGoalInvalid()
    {
        return _isGoalInvalid;
    }

    private void SetGoalInvalid(final boolean isInvalid)
    {
        if (_isGoalInvalid == isInvalid) return;

        _isGoalInvalid = isInvalid;
        NotifyEventHandlers(IStepCountMergeViewModelEventHandler::GoalValueValidityChanged);
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

        NotifyEventHandlers(IStepCountMergeViewModelEventHandler::DateTimeOfMeasurementChanged);
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
            _stepCount != null &&
            _goal != null &&
            _dateTimeOfMeasurement != null;

        if (_canValuesBeSaved == canValuesBeSaved) return;

        _canValuesBeSaved = canValuesBeSaved;
        NotifyEventHandlers(IStepCountMergeViewModelEventHandler::CanValuesBeSavedChanged);
    }

    public void Save()
    {
        if (IsReadOnly() || !CanValuesBeSaved()) return;

        final StepCountRecord record;

        if (_recordIdentifier == null)
        {
            record = new StepCountRecord(
                _stepCount,
                _goal,
                _dateTimeOfMeasurement);
        }
        else
        {
            record = new StepCountRecord(
                _recordIdentifier,
                _stepCount,
                _goal,
                _dateTimeOfMeasurement);
        }

        try
        {
            _stepCountRecordWriter.CreateOrUpdateRecord(record);
        }
        catch (Exception exception)
        {
            _logger.LogDebug(TAG, "Failed to save record.", exception);
            NotifyEventHandlers(IStepCountMergeViewModelEventHandler::RecordCouldNotBeSaved);

            return;
        }

        NotifyEventHandlers(IStepCountMergeViewModelEventHandler::RecordSaved);
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
     * Represents an actor that can react to events of the {@link StepCountMergeViewModel}.
     */
    public interface IStepCountMergeViewModelEventHandler
    {
        /**
         * Called when the step-count property changed.
         */
        void StepCountValueChanged();

        /**
         * Called when the validity of the step-count property changed.
         */
        void StepCountValueValidityChanged();

        /**
         * Called when the step-count goal property changed.
         */
        void GoalValueChanged();

        /**
         * Called when the validity of the step-count goal property changed.
         */
        void GoalValueValidityChanged();

        /**
         * Called when the date-time of measurement property changed.
         */
        void DateTimeOfMeasurementChanged();

        /**
         * Called if the state changes if {@link StepCountMergeViewModel#Save()} has any effects.
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
