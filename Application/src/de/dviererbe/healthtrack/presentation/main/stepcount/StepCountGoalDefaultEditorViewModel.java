package de.dviererbe.healthtrack.presentation.main.stepcount;

import de.dviererbe.healthtrack.IDisposable;
import de.dviererbe.healthtrack.infrastructure.ILogger;
import de.dviererbe.healthtrack.infrastructure.INumericValueConverter;
import de.dviererbe.healthtrack.persistence.IDefaultStepCountGoalGetter;
import de.dviererbe.healthtrack.persistence.IDefaultStepCountGoalSetter;
import de.dviererbe.healthtrack.presentation.ConversionHelper;
import de.dviererbe.healthtrack.presentation.ViewModel;

import java.util.Objects;

public class StepCountGoalDefaultEditorViewModel
    extends ViewModel<StepCountGoalDefaultEditorViewModel.IStepCountGoalDefaultEditorViewModelEventHandler>
    implements IDisposable
{
    private static final String TAG = "StepCountGoalDefaultEditorViewModel";
    private final IDefaultStepCountGoalSetter _defaultStepCountGoalSetter;
    private final INumericValueConverter _numericValueConverter;
    private final ILogger _logger;

    private Integer _goal;
    private String _goalText;
    private boolean _isGoalInvalid;
    private boolean _canValuesBeSaved;

    public StepCountGoalDefaultEditorViewModel(
        final IDefaultStepCountGoalGetter defaultStepCountGoalGetter,
        final IDefaultStepCountGoalSetter defaultStepCountGoalSetter,
        final INumericValueConverter numericValueConverter,
        final ILogger logger)
    {
        _defaultStepCountGoalSetter = defaultStepCountGoalSetter;
        _numericValueConverter = numericValueConverter;
        _logger = logger;

        try
        {
            _goal = defaultStepCountGoalGetter.GetDefaultStepCountGoal();
        }
        catch (Exception exception)
        {
            _goal = null;
        }

        _isGoalInvalid = false;
        _goalText = ConvertToString(_goal);
        _canValuesBeSaved = false;
    }

    public String GetGoalString()
    {
        return _goalText;
    }

    public void SetGoal(final String value)
    {
        try
        {
            SetGoal(_numericValueConverter.ToInteger(value), value);
        }
        catch (Exception exception)
        {
            _logger.LogDebug(TAG, "Failed to convert goal value.", exception);
            SetGoal(null, value);
        }
    }

    private void SetGoal(final Integer value, final String text)
    {
        if (Objects.equals(_goal, value)) return;

        _goal = value;
        _goalText = text != null ? text : ConvertToString(value);

        NotifyEventHandlers(IStepCountGoalDefaultEditorViewModelEventHandler::GoalValueChanged);
        SetGoalInvalid(value == null);
        ReevaluateIfValuesCanBeSaved();
    }

    public boolean IsPulseValueInvalid()
    {
        return _isGoalInvalid;
    }

    private void SetGoalInvalid(final boolean isInvalid)
    {
        if (_isGoalInvalid == isInvalid) return;

        _isGoalInvalid = isInvalid;
        NotifyEventHandlers(IStepCountGoalDefaultEditorViewModelEventHandler::GoalValueValidityChanged);
    }

    public boolean CanValuesBeSaved()
    {
        return _canValuesBeSaved;
    }

    private void ReevaluateIfValuesCanBeSaved()
    {
        final boolean canValuesBeSaved =
            _goal != null &&
            _goal > 0;

        if (_canValuesBeSaved == canValuesBeSaved) return;

        _canValuesBeSaved = canValuesBeSaved;
        NotifyEventHandlers(IStepCountGoalDefaultEditorViewModelEventHandler::CanValuesBeSavedChanged);
    }

    public void Save()
    {
        try
        {
            _defaultStepCountGoalSetter.SetDefaultStepCountGoal(_goal);
        }
        catch (Exception exception)
        {
            _logger.LogDebug(TAG, "Failed to save default step count goal.", exception);
            NotifyEventHandlers(IStepCountGoalDefaultEditorViewModelEventHandler::DefaultStepCountGoalCouldNotBeUpdated);
            return;
        }

        NotifyEventHandlers(IStepCountGoalDefaultEditorViewModelEventHandler::DefaultStepCountGoalCouldBeUpdated);
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
     * Performs application-defined tasks associated with freeing, releasing, or resetting resources.
     */
    @Override
    public void Dispose()
    {
        super.Dispose();
    }

    /**
     * Represents an actor that can react to events of the {@link StepCountGoalDefaultEditorViewModel}.
     */
    public interface IStepCountGoalDefaultEditorViewModelEventHandler
    {
        /**
         * Called when the goal property changed.
         */
        void GoalValueChanged();

        /**
         * Called when the validity of the goal property changed.
         */
        void GoalValueValidityChanged();

        /**
         * Called if the state changes if {@link StepCountGoalDefaultEditorViewModel#Save()} has any effects.
         */
        void CanValuesBeSavedChanged();

        /**
         * Called when the default step count goal was updated successfully.
         */
        void DefaultStepCountGoalCouldNotBeUpdated();

        /**
         * Called when the default step count goal could not be updated because of an error.
         */
        void DefaultStepCountGoalCouldBeUpdated();
    }
}
