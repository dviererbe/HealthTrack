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

package de.dviererbe.healthtrack.presentation.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import de.dviererbe.healthtrack.IDisposable;
import de.dviererbe.healthtrack.persistence.IWidgetConfigurationRepository;

public class MainViewViewModel implements IDisposable
{
    private final MutableLiveData<Boolean> _isStepCounterWidgetEnabled;
    private final MutableLiveData<Boolean> _isWeightWidgetEnabled;
    private final MutableLiveData<Boolean> _isFoodWidgetEnabled;
    private final MutableLiveData<Boolean> _isBloodPressureWidgetEnabled;
    private final MutableLiveData<Boolean> _isBloodSugarWidgetEnabled;

    private final IWidgetConfigurationRepository _widgetConfigurationRepository;
    private final IMainView _mainActivityNavigation;
    private final WidgetConfigurationChangeListener _widgetConfigurationChangeListener;

    public MainViewViewModel(
            IWidgetConfigurationRepository widgetConfiguration,
            IMainView mainActivityNavigation)
    {
        _isStepCounterWidgetEnabled = new MutableLiveData<>(widgetConfiguration.IsStepCounterWidgetEnabled());
        _isWeightWidgetEnabled = new MutableLiveData<>(widgetConfiguration.IsWeightWidgetEnabled());
        _isFoodWidgetEnabled = new MutableLiveData<>(widgetConfiguration.IsFoodWidgetEnabled());
        _isBloodPressureWidgetEnabled = new MutableLiveData<>(widgetConfiguration.IsBloodPressureWidgetEnabled());
        _isBloodSugarWidgetEnabled = new MutableLiveData<>(widgetConfiguration.IsBloodSugarWidgetEnabled());

        _mainActivityNavigation = mainActivityNavigation;
        _widgetConfigurationRepository = widgetConfiguration;
        _widgetConfigurationChangeListener = new WidgetConfigurationChangeListener();
        _widgetConfigurationRepository.RegisterOnWidgetConfigurationChangedListener(_widgetConfigurationChangeListener);
    }

    /**
     * Gets an observable value if the step counter widget is enabled.
     *
     * @return {@link LiveData<Boolean>} that is true if the step counter widget is enabled; otherwise false.
     */
    public LiveData<Boolean> IsStepCounterWidgetEnabled()
    {
        return _isStepCounterWidgetEnabled;
    }

    /**
     * Gets an observable value if the weight widget is enabled.
     *
     * @return {@link LiveData<Boolean>} that is true if the weight widget is enabled; otherwise false.
     */
    public LiveData<Boolean> IsWeightWidgetEnabled()
    {
        return _isWeightWidgetEnabled;
    }

    /**
     * Gets an observable value if the food widget is enabled.
     *
     * @return {@link LiveData<Boolean>} that is true if the food widget is enabled; otherwise false.
     */
    public LiveData<Boolean> IsFoodWidgetEnabled()
    {
        return _isFoodWidgetEnabled;
    }

    /**
     * Gets an observable value if the blood pressure widget is enabled.
     *
     * @return {@link LiveData<Boolean>} that is true if the blood pressure widget is enabled; otherwise false.
     */
    public LiveData<Boolean> IsBloodPressureWidgetEnabled()
    {
        return _isBloodPressureWidgetEnabled;
    }

    /**
     * Gets an observable value if the blood sugar widget is enabled.
     *
     * @return {@link LiveData<Boolean>} that is true if the blood sugar widget is enabled; otherwise false.
     */
    public LiveData<Boolean> IsBloodSugarWidgetEnabled()
    {
        return _isBloodSugarWidgetEnabled;
    }

    /**
     * Navigates the user to a UI for editing the settings of the application.
     */
    public void NavigateToSettings()
    {
        _mainActivityNavigation.NavigateToSettings();
    }

    /**
     * Shows the user a UI dialog for sending feedback.
     */
    public void ShowFeedbackDialog()
    {
        _mainActivityNavigation.ShowFeedbackDialog();
    }

    /**
     * Performs application-defined tasks associated with freeing, releasing, or resetting resources.
     */
    @Override
    public void Dispose()
    {
        _widgetConfigurationRepository.UnregisterOnWidgetConfigurationChangedListener(_widgetConfigurationChangeListener);
    }

    private class WidgetConfigurationChangeListener
            implements IWidgetConfigurationRepository.OnWidgetConfigurationChangedListener
    {
        /**
         * Called when the widget configuration has changed.
         *
         * @param changedWidgetConfiguration The {@link IWidgetConfigurationRepository} instance that has changed.
         */
        @Override
        public void OnWidgetConfigurationChanged(IWidgetConfigurationRepository changedWidgetConfiguration)
        {
            _isStepCounterWidgetEnabled.setValue(_widgetConfigurationRepository.IsStepCounterWidgetEnabled());
            _isWeightWidgetEnabled.setValue(_widgetConfigurationRepository.IsWeightWidgetEnabled());
            _isFoodWidgetEnabled.setValue(_widgetConfigurationRepository.IsFoodWidgetEnabled());
            _isBloodPressureWidgetEnabled.setValue(_widgetConfigurationRepository.IsBloodPressureWidgetEnabled());
            _isBloodSugarWidgetEnabled.setValue(_widgetConfigurationRepository.IsBloodSugarWidgetEnabled());
        }
    }

    /**
     * Interface for the main user interface.
     */
    public interface IMainView
    {
        /**
         * Navigates the user to a UI for editing the settings of the application.
         */
        void NavigateToSettings();

        /**
         * Shows the user a UI dialog for sending feedback.
         */
        void ShowFeedbackDialog();
    }
}
