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

package de.dviererbe.healthtrack;

import de.dviererbe.healthtrack.application.DeleteAllUserDataOperation;
import de.dviererbe.healthtrack.infrastructure.IDateTimeConverter;
import de.dviererbe.healthtrack.infrastructure.IDateTimeProvider;
import de.dviererbe.healthtrack.infrastructure.IUserDataExporter;
import de.dviererbe.healthtrack.infrastructure.INumericValueConverter;
import de.dviererbe.healthtrack.persistence.*;
import de.dviererbe.healthtrack.presentation.IUIThemeSetter;

/**
 * Abstraction for resolving dependencies.
 */
public interface IDependencyResolver extends IDisposable
{
    /**
     * Resolves an {@link IDateTimeProvider} implementation.
     *
     * @return {@link IDateTimeProvider} implementation
     */
    IDateTimeProvider GetDateTimeProvider();

    /**
     * Resolves an {@link IDateTimeConverter} implementation.
     *
     * @return {@link IDateTimeConverter} implementation
     */
    IDateTimeConverter GetDateTimeConverter();

    /**
     * Resolves an {@link INumericValueConverter} implementation.
     *
     * @return {@link INumericValueConverter} implementation
     */
    INumericValueConverter GetNumericValueConverter();

    /**
     * Initializes an {@link DeleteAllUserDataOperation} implementation.
     *
     * @return {@link DeleteAllUserDataOperation} implementation
     */
    DeleteAllUserDataOperation CreateDeleteAllUserDataOperation();

    /**
     * Initializes an {@link IUserDataExporter} implementation.
     *
     * @return {@link IUserDataExporter} implementation
     */
    IUserDataExporter CreateUserDataExporter();

    /**
     * Resolves an {@link IPreferredThemeRepository} implementation.
     *
     * @return {@link IPreferredThemeRepository} implementation
     */
    IPreferredThemeRepository GetPreferredThemeRepository();

    /**
     * Resolves an {@link IPreferredUnitRepository} implementation.
     *
     * @return {@link IPreferredUnitRepository} implementation
     */
    IPreferredUnitRepository GetPreferredUnitRepository();

    /**
     * Resolves an {@link IWidgetConfigurationRepository} implementation.
     *
     * @return {@link IWidgetConfigurationRepository} implementation
     */
    IWidgetConfigurationRepository GetWidgetConfigurationRepository();

    /**
     * Initializes an {@link IStepWidgetRepository} implementation.
     *
     * @return {@link IStepWidgetRepository} implementation
     */
    IStepWidgetRepository CreateStepWidgetRepository();

    /**
     * Initializes an {@link } implementation.
     *
     * @return {@link } implementation
     */
    IFoodWidgetRepository CreateFoodWidgetRepository();

    /**
     * Initializes an {@link IWeightWidgetRepository} implementation.
     *
     * @return {@link IWeightWidgetRepository} implementation
     */
    IWeightWidgetRepository CreateWeightWidgetRepository();

    /**
     * Initializes an {@link IBloodPressureWidgetRepository} implementation.
     *
     * @return {@link IBloodPressureWidgetRepository} implementation
     */
    IBloodPressureWidgetRepository CreateBloodPressureWidgetRepository();

    /**
     * Initializes an {@link IBloodSugarWidgetRepository} implementation.
     *
     * @return {@link IBloodSugarWidgetRepository} implementation
     */
    IBloodSugarWidgetRepository CreateBloodSugarWidgetRepository();

    /**
     * Resolves an {@link IUIThemeSetter} implementation.
     *
     * @return {@link IUIThemeSetter} implementation
     */
    IUIThemeSetter GetUIThemeSetter();

    /**
     * Resolves an {@link ViewModelFactory} instance.
     *
     * @return {@link ViewModelFactory} instance
     */
    ViewModelFactory GetViewModelFactory();
}
