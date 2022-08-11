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

import de.dviererbe.healthtrack.persistence.repositories.IWidgetConfigurationRepository;
import de.dviererbe.healthtrack.presentation.ViewModel;

public class MainViewViewModel extends ViewModel<MainViewViewModel.IMainViewViewModelEventHandler>
{
    private final IWidgetConfigurationRepository _widgetConfigurationRepository;
    private final WidgetConfigurationChangeListener _widgetConfigurationChangeListener;

    private boolean _isStepCounterWidgetEnabled;
    private boolean _isWeightWidgetEnabled;
    private boolean _isFoodWidgetEnabled;
    private boolean _isBloodPressureWidgetEnabled;
    private boolean _isBloodSugarWidgetEnabled;

    public MainViewViewModel(final IWidgetConfigurationRepository widgetConfiguration)
    {
        _widgetConfigurationRepository = widgetConfiguration;

        _isStepCounterWidgetEnabled = _widgetConfigurationRepository.IsStepCounterWidgetEnabled();
        _isWeightWidgetEnabled = _widgetConfigurationRepository.IsWeightWidgetEnabled();
        _isFoodWidgetEnabled = _widgetConfigurationRepository.IsFoodWidgetEnabled();
        _isBloodPressureWidgetEnabled = _widgetConfigurationRepository.IsBloodPressureWidgetEnabled();
        _isBloodSugarWidgetEnabled = _widgetConfigurationRepository.IsBloodSugarWidgetEnabled();

        _widgetConfigurationChangeListener = new WidgetConfigurationChangeListener();
        _widgetConfigurationRepository.RegisterOnWidgetConfigurationChangedListener(_widgetConfigurationChangeListener);
    }

    /**
     * Gets an observable value if the step counter widget is enabled.
     *
     * @return {@code true} if the step counter widget is enabled; otherwise {@code false}.
     */
    public boolean IsStepCounterWidgetEnabled()
    {
        return _isStepCounterWidgetEnabled;
    }

    private void SetIsStepCounterWidgetEnabled(final boolean value)
    {
        if (_isStepCounterWidgetEnabled == value) return;

        _isStepCounterWidgetEnabled = true;
        NotifyEventHandlers(IMainViewViewModelEventHandler::IsStepCounterWidgetEnabledChanged);
    }

    /**
     * Gets an observable value if the weight widget is enabled.
     *
     * @return  {@code true} if the weight widget is enabled; otherwise {@code false}.
     */
    public boolean IsWeightWidgetEnabled()
    {
        return _isWeightWidgetEnabled;
    }

    private void SetIsWeightWidgetEnabled(final boolean value)
    {
        if (_isWeightWidgetEnabled == value) return;

        _isWeightWidgetEnabled = true;
        NotifyEventHandlers(IMainViewViewModelEventHandler::IsWeightWidgetEnabledChanged);
    }

    /**
     * Gets an observable value if the food widget is enabled.
     *
     * @return  {@code true} if the food widget is enabled; otherwise {@code false}.
     */
    public boolean IsFoodWidgetEnabled()
    {
        return _isFoodWidgetEnabled;
    }

    private void SetIsFoodWidgetEnabled(final boolean value)
    {
        if (_isFoodWidgetEnabled == value) return;

        _isFoodWidgetEnabled = true;
        NotifyEventHandlers(IMainViewViewModelEventHandler::IsFoodWidgetEnabledChanged);
    }

    /**
     * Gets an observable value if the blood pressure widget is enabled.
     *
     * @return  {@code true} if the blood pressure widget is enabled; otherwise {@code false}.
     */
    public boolean IsBloodPressureWidgetEnabled()
    {
        return _isBloodPressureWidgetEnabled;
    }

    private void SetIsBloodPressureWidgetEnabled(final boolean value)
    {
        if (_isBloodPressureWidgetEnabled == value) return;

        _isBloodPressureWidgetEnabled = true;
        NotifyEventHandlers(IMainViewViewModelEventHandler::IsBloodPressureWidgetEnabledChanged);
    }

    /**
     * Gets an observable value if the blood sugar widget is enabled.
     *
     * @return {@code true} if the blood sugar widget is enabled; otherwise {@code false}.
     */
    public boolean IsBloodSugarWidgetEnabled()
    {
        return _isBloodSugarWidgetEnabled;
    }

    private void SetIsBloodSugarWidgetEnabled(final boolean value)
    {
        if (_isBloodSugarWidgetEnabled == value) return;

        _isBloodSugarWidgetEnabled = true;
        NotifyEventHandlers(IMainViewViewModelEventHandler::IsBloodSugarWidgetEnabledChanged);
    }

    /**
     * Performs application-defined tasks associated with freeing, releasing, or resetting resources.
     */
    @Override
    public void Dispose()
    {
        _widgetConfigurationRepository.UnregisterOnWidgetConfigurationChangedListener(_widgetConfigurationChangeListener);

        super.Dispose();
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
            if (changedWidgetConfiguration != _widgetConfigurationRepository) return;

            SetIsStepCounterWidgetEnabled(_widgetConfigurationRepository.IsStepCounterWidgetEnabled());
            SetIsWeightWidgetEnabled(_widgetConfigurationRepository.IsWeightWidgetEnabled());
            SetIsFoodWidgetEnabled(_widgetConfigurationRepository.IsFoodWidgetEnabled());
            SetIsBloodPressureWidgetEnabled(_widgetConfigurationRepository.IsBloodSugarWidgetEnabled());
            SetIsBloodSugarWidgetEnabled(_widgetConfigurationRepository.IsBloodSugarWidgetEnabled());
        }
    }

    /**
     * Represents an actor that can react to events of the {@link MainViewViewModel}.
     */
    public interface IMainViewViewModelEventHandler
    {
        void IsStepCounterWidgetEnabledChanged();

        void IsWeightWidgetEnabledChanged();

        void IsFoodWidgetEnabledChanged();

        void IsBloodPressureWidgetEnabledChanged();

        void IsBloodSugarWidgetEnabledChanged();
    }
}
