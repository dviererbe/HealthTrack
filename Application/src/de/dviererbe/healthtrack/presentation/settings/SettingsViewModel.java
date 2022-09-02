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

package de.dviererbe.healthtrack.presentation.settings;

import de.dviererbe.healthtrack.application.DeleteAllUserDataOperation;
import de.dviererbe.healthtrack.application.ExportUserDataAsJsonOperation;
import de.dviererbe.healthtrack.infrastructure.ILogger;
import de.dviererbe.healthtrack.presentation.ViewModel;

import java.util.function.Function;

public class SettingsViewModel extends ViewModel<SettingsViewModel.ISettingsViewModelEventHandler>
{
    private static final String TAG = "SettingsViewModel";

    private final Function<ExportUserDataAsJsonOperation.Options, ExportUserDataAsJsonOperation> _exportUserDataAsJsonOperationFactory;
    private final Function<Void, DeleteAllUserDataOperation> _deleteAllUserDataOperationFactory;
    private final ILogger _logger;

    public SettingsViewModel(
            final Function<ExportUserDataAsJsonOperation.Options, ExportUserDataAsJsonOperation> exportUserDataAsJsonOperationFactory,
            final Function<Void, DeleteAllUserDataOperation> deleteAllUserDataOperationLazyFactory,
            final ILogger logger)
    {
        _exportUserDataAsJsonOperationFactory = exportUserDataAsJsonOperationFactory;
        _deleteAllUserDataOperationFactory = deleteAllUserDataOperationLazyFactory;
        _logger = logger;
    }

    /**
     * The user requests to export all user related data.
     */
    public void ExportUserData(
        final ExportUserDataAsJsonOperation.Options exportOptions)
    {
        if (exportOptions == null) return;

        final ExportUserDataAsJsonOperation exportOperation =
            _exportUserDataAsJsonOperationFactory.apply(exportOptions);

        exportOperation.Execute((exception ->
        {
            if (exception == null)
                NotifyEventHandlers(ISettingsViewModelEventHandler::ExportingUserDataSucceeded);
            else
                NotifyEventHandlers(ISettingsViewModelEventHandler::ExportingUserDataFailed);
        }));
    }

    /**
     * The user requests to delete all user related data.
     */
    public void DeleteUserData()
    {
        final DeleteAllUserDataOperation operation = _deleteAllUserDataOperationFactory.apply(null);

        try
        {
            operation.Execute();
            NotifyEventHandlers(ISettingsViewModelEventHandler::DeletingUserDataSucceeded);
        }
        catch (Exception exception)
        {
            _logger.LogDebug(TAG, "Failed to delete all user data", exception);
            NotifyEventHandlers(ISettingsViewModelEventHandler::DeletingUserDataFailed);
        }
    }

    /**
     * Represents an actor that can react to events of the {@link SettingsViewModel}.
     */
    public interface ISettingsViewModelEventHandler
    {
        /**
         * Called when the request to export the user data succeeded.
         */
        void ExportingUserDataSucceeded();

        /**
         * Called when the request to export the user data failed.
         */
        void ExportingUserDataFailed();

        /**
         * Called when the request to delete the user data succeeded.
         */
        void DeletingUserDataSucceeded();

        /**
         * Called when the request to delete the user data failed.
         */
        void DeletingUserDataFailed();
    }
}
