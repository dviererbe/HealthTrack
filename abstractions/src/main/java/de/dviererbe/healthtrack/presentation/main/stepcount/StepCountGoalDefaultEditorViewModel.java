package de.dviererbe.healthtrack.presentation.main.stepcount;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import de.dviererbe.healthtrack.IDisposable;
import de.dviererbe.healthtrack.infrastructure.INavigationRouter;
import de.dviererbe.healthtrack.infrastructure.INumericValueConverter;
import de.dviererbe.healthtrack.persistence.IStepWidgetRepository;

public class StepCountGoalDefaultEditorViewModel implements IDisposable
{
    private static final String TAG = "StepCountGoalDefaultEditorViewModel";

    private final IStepCountGoalDefaultEditorView _view;
    private final INavigationRouter _navigationRouter;
    private final IStepWidgetRepository _repository;
    private final INumericValueConverter _numericValueConverter;

    private Integer _goal;

    private final MutableLiveData<Boolean> _showGoalInvalidErrorMessage;
    private final MutableLiveData<Boolean> _isSaveButtonEnabled;

    public final LiveData<Boolean> ShowGoalInvalidErrorMessage;
    public final LiveData<Boolean> IsSaveButtonEnabled;


    public StepCountGoalDefaultEditorViewModel(
            final IStepCountGoalDefaultEditorView view,
            final INavigationRouter navigationRouter,
            final IStepWidgetRepository repository,
            final INumericValueConverter numericValueConverter)
    {
        _view = view;
        _navigationRouter = navigationRouter;
        _repository = repository;
        _numericValueConverter = numericValueConverter;

        ShowGoalInvalidErrorMessage = _showGoalInvalidErrorMessage = new MutableLiveData<>(false);
        IsSaveButtonEnabled = _isSaveButtonEnabled = new MutableLiveData<>(false);

        try
        {
            _goal = _repository.GetDefaultStepCountGoal();
        }
        catch (Exception exception)
        {
            _goal = null;
        }
    }

    public String GetGoal()
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

    public void SetGoal(String value)
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

    public void Save()
    {
        try
        {
            _repository.SetDefaultStepCountGoal(_goal);
        }
        catch (Exception exception)
        {
            Log.d(TAG, "Failed to save default step count goal.", exception);
            _view.NotifyUserThatDefaultStepCountGoalCouldNotBeUpdated();
            return;
        }

        _navigationRouter.TryNavigateBack();
    }

    private void TryEnableSaveButton()
    {
        _isSaveButtonEnabled.setValue(_goal != null && _goal > 0);
    }

    /**
     * Performs application-defined tasks associated with freeing, releasing, or resetting resources.
     */
    @Override
    public void Dispose()
    {

    }

    /**
     * Interface for the default step count goal editor user interface.
     */
    public interface IStepCountGoalDefaultEditorView
    {
        /**
         * Notifies the user that the default step count goal could not be updated because of an error.
         */
        void NotifyUserThatDefaultStepCountGoalCouldNotBeUpdated();
    }
}
