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

package de.dviererbe.healthtrack.persistence;

/**
 * A storage mechanism for the widget configuration.
 */
public interface IWidgetConfigurationRepository
{
    /**
     * Gets if the step counter widget is enabled.
     *
     * @return true if the step counter widget is enabled; otherwise false.
     */
    boolean IsStepCounterWidgetEnabled();

    /**
     * Gets if the weight widget is enabled.
     *
     * @return true if the weight widget is enabled; otherwise false.
     */
    boolean IsWeightWidgetEnabled();

    /**
     * Gets if the food widget is enabled.
     *
     * @return true if the food widget is enabled; otherwise false.
     */
    boolean IsFoodWidgetEnabled();

    /**
     * Gets if the blood pressure widget is enabled.
     *
     * @return true if the blood pressure widget is enabled; otherwise false.
     */
    boolean IsBloodPressureWidgetEnabled();

    /**
     * Gets if the blood sugar widget is enabled.
     *
     * @return true if the blood sugar widget is enabled; otherwise false.
     */
    boolean IsBloodSugarWidgetEnabled();

    /**
     * Registers a listener instance that gets notified when the
     * widget configuration changed.
     *
     * @param listener The listener instance that gets notified.
     */
    void RegisterOnWidgetConfigurationChangedListener(OnWidgetConfigurationChangedListener listener);

    /**
     * Removes a listener instance from the notification list when
     * the widget configuration changed.
     *
     * @param listener The listener instance that gets removed from the notification list.
     */
    void UnregisterOnWidgetConfigurationChangedListener(OnWidgetConfigurationChangedListener listener);

    /**
     * Interface definition for a callback to be invoked when
     * the widget configuration has changed.
     */
    interface OnWidgetConfigurationChangedListener
    {
        /**
         * Called when the widget configuration has changed.
         *
         * @param changedWidgetConfiguration The {@link IWidgetConfigurationRepository} instance that has changed.
         */
        void OnWidgetConfigurationChanged(IWidgetConfigurationRepository changedWidgetConfiguration);
    }
}
