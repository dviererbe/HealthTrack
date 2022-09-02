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
import de.dviererbe.healthtrack.application.ExportUserDataAsJsonOperation;
import de.dviererbe.healthtrack.infrastructure.*;
import de.dviererbe.healthtrack.persistence.repositories.*;
import de.dviererbe.healthtrack.presentation.IUIThemeSetter;

/**
 * Abstraction for resolving dependencies.
 */
public interface IDependencyResolver extends IDisposable
{
    /**
     * Resolves an {@link ILogger} implementation.
     *
     * @return {@link ILogger} implementation
     */
    ILogger GetLogger();

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
     * Initializes an {@link ExportUserDataAsJsonOperation} instance.
     *
     * @param options Specifies which data should be exported.
     * @param userDataJsonFileOutputStreamProvider Mechanism for opening a json file stream.
     * @return Initialized {@link ExportUserDataAsJsonOperation} instance.
     */
    ExportUserDataAsJsonOperation CreateExportUserDataAsJsonOperation(
            final ExportUserDataAsJsonOperation.Options options,
            final IUserDataJsonTextWriterProvider userDataJsonFileOutputStreamProvider);

    /**
     * Initializes an {@link DeleteAllUserDataOperation} instance.
     *
     * @return {@link DeleteAllUserDataOperation} instance
     */
    DeleteAllUserDataOperation CreateDeleteAllUserDataOperation();

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
