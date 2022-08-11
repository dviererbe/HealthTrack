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

import de.dviererbe.healthtrack.IDisposable;
import de.dviererbe.healthtrack.application.DeleteAllUserDataOperation;
import de.dviererbe.healthtrack.application.ExportUserDataAsJsonOperation;
import de.dviererbe.healthtrack.infrastructure.ILogger;
import de.dviererbe.healthtrack.infrastructure.IUserDataJsonTextWriterProvider;
import de.dviererbe.healthtrack.infrastructure.UserDataJsonTextWriterCouldNotBeProvided;
import de.dviererbe.healthtrack.infrastructure.json.JsonTextWriter;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.function.Function;

public class SettingsViewModel implements IDisposable
{
    public static class ExportParams
    {
        public final ExportUserDataAsJsonOperation.Options Options;
        public final IUserDataJsonTextWriterProvider UserDataJsonFileOutputStreamProvider;

        public ExportParams(
            final ExportUserDataAsJsonOperation.Options options,
            final IUserDataJsonTextWriterProvider userDataJsonTextWriterProvider)
        {
            Options = options;
            UserDataJsonFileOutputStreamProvider = userDataJsonTextWriterProvider;
        }
    }

    private static final String TAG = "SettingsViewModel";

    private final ISettingsView _view;
    private final Function<ExportParams, ExportUserDataAsJsonOperation> _exportUserDataAsJsonOperationFactory;
    private final Function<Void, DeleteAllUserDataOperation> _deleteAllUserDataOperationFactory;
    private final ILogger _logger;

    public SettingsViewModel(
            final ISettingsView view,
            final Function<ExportParams, ExportUserDataAsJsonOperation> exportUserDataAsJsonOperationFactory,
            final Function<Void, DeleteAllUserDataOperation> deleteAllUserDataOperationLazyFactory,
            final ILogger logger)
    {
        _view = view;
        _exportUserDataAsJsonOperationFactory = exportUserDataAsJsonOperationFactory;
        _deleteAllUserDataOperationFactory = deleteAllUserDataOperationLazyFactory;
        _logger = logger;
    }

    /**
     * The user requests to export all user related data.
     */
    public void ExportUserData()
    {
        _view.ShowExportUserDataDialog((exportUserDataResult)->
        {
            // User canceled export
            if (exportUserDataResult == null) return;

            final String mediaType = "application/json";
            final String suggestedName = "healthtrack-data.json";

            _view.ShowSelectStorageLocationDialog(mediaType, suggestedName, (OutputStream storageFile) ->
            {
                // User canceled export
                if (storageFile == null) return;

                final ExportUserDataAsJsonOperation.Options exportOptions =
                    new ExportUserDataAsJsonOperation.Options(
                        exportUserDataResult.ExportBloodPressureData,
                        exportUserDataResult.ExportBloodSugarData,
                        exportUserDataResult.ExportFoodData,
                        exportUserDataResult.ExportStepsData,
                        exportUserDataResult.ExportWeightData);

                final ExportUserDataAsJsonOperation exportOperation =
                    _exportUserDataAsJsonOperationFactory.apply(
                            new ExportParams(exportOptions, () ->
                            {
                                Writer writer;

                                try
                                {
                                    writer = new OutputStreamWriter(storageFile);
                                }
                                catch (Exception exception)
                                {
                                    throw new UserDataJsonTextWriterCouldNotBeProvided(exception);
                                }

                                return new JsonTextWriter(writer);
                            }));

                try
                {
                    exportOperation.Execute();
                    _view.NotifyUserThatExportingUserDataSucceeded();
                }
                catch (Exception exception)
                {
                    _view.NotifyUserThatExportingUserDataFailed();
                }
            });
        });
    }

    /**
     * The user requests to delete all user related data.
     */
    public void DeleteUserData()
    {
        _view.ShowDeleteUserDataDialog((confirmDeleteAllUserData) ->
        {
            if (!confirmDeleteAllUserData) return;

            final DeleteAllUserDataOperation operation = _deleteAllUserDataOperationFactory.apply(null);

            try
            {
                operation.Execute();
                _view.NotifyUserThatDeletingUserDataSucceeded();
            }
            catch (Exception exception)
            {
                _logger.LogDebug(TAG, "Failed to delete all user data", exception);
                _view.NotifyUserThatDeletingUserDataFailed();
            }
        });
    }

    /**
     * Performs application-defined tasks associated with freeing, releasing, or resetting resources.
     */
    @Override
    public void Dispose()
    {
    }

    /**
     * Interface for the user interface to edit the user preferences.
     */
    public interface ISettingsView
    {
        /**
         * Notifies the user that the attempt to export the user data succeeded.
         */
        void NotifyUserThatExportingUserDataSucceeded();

        /**
         * Notifies the user that the attempt to export the user data failed.
         */
        void NotifyUserThatExportingUserDataFailed();

        /**
         * Notifies the user that the attempt to delete all user data succeeded.
         */
        void NotifyUserThatDeletingUserDataSucceeded();

        /**
         * Notifies the user that the attempt to delete all user data failed.
         */
        void NotifyUserThatDeletingUserDataFailed();

        /**
         * Shows the user a UI dialog for deleting all user related data.
         *
         * @param callback reference to a callback mechanism for when the user data delete dialog finishes.
         */
        void ShowExportUserDataDialog(final IExportUserDataDialogObserver callback);

        /**
         * Shows the user a UI dialog for selection a location for storing the user data.
         *
         * @param mediaType MIME type (IANA media; defined by IETF RFC 6838) of the document that should be created.
         * @param nameSuggestion Suggestion for the user for the name of document that should be created.
         * @param callback reference to a callback mechanism for when the select storage path dialog finishes.
         */
        void ShowSelectStorageLocationDialog(final String mediaType, final String nameSuggestion, final ISelectStoragePathDialogObserver callback);

        /**
         * Shows the user a UI dialog for deleting all user related data.
         *
         * @param callback reference to a callback mechanism for when the user data delete dialog finishes.
         */
        void ShowDeleteUserDataDialog(final IDeleteUserDataDialogObserver callback);

        /**
         * Callback mechanism for when export user data dialog finishes.
         */
        interface IExportUserDataDialogObserver
        {
            /**
             * Called when the export user data dialog finished.
             *
             * @param dialogResult contains the user selections which data should be exported; {@code null} when the user cancelled the dialog.
             */
            void OnCompleted(final ExportUserDataDialogResult dialogResult);

            /**
             * Holds the result values of the export user data dialog when the user confirms the export.
             */
            class ExportUserDataDialogResult
            {
                /**
                 * If the user wants to export step count related data.
                 *
                 * {@code true} when the user wants to export step count related data; otherwise {@code false}.
                 */
                public final boolean ExportStepsData;

                /**
                 * If the user wants to export food related data.
                 *
                 * {@code true} when the user wants to export food related data; otherwise {@code false}.
                 */
                public final boolean ExportFoodData;

                /**
                 * If the user wants to export weight related data.
                 *
                 * {@code true} when the user wants to export weight related data; otherwise {@code false}.
                 */
                public final boolean ExportWeightData;

                /**
                 * If the user wants to export blood pressure related data.
                 *
                 * {@code true} when the user wants to export blood pressure related data; otherwise {@code false}.
                 */
                public final boolean ExportBloodPressureData;

                /**
                 * If the user wants to export blood sugar related data.
                 *
                 * {@code true} when the user wants to export blood sugar related data; otherwise {@code false}.
                 */
                public final boolean ExportBloodSugarData;

                /**
                 * Initializes a new {@link ExportUserDataDialogResult} instance with the user selections.
                 *
                 * @param exportStepsData {@code true} when the user wants to export step count related data; otherwise {@code false}.
                 * @param exportFoodData {@code true} when the user wants to export food related data; otherwise {@code false}.
                 * @param exportWeightData {@code true} when the user wants to export weight related data; otherwise {@code false}.
                 * @param exportBloodPressureData {@code true} when the user wants to export blood pressure related data; otherwise {@code false}.
                 * @param exportBloodSugarData {@code true} when the user wants to export blood sugar related data; otherwise {@code false}.
                 */
                public ExportUserDataDialogResult(
                    final boolean exportStepsData,
                    final boolean exportFoodData,
                    final boolean exportWeightData,
                    final boolean exportBloodPressureData,
                    final boolean exportBloodSugarData)
                {
                    ExportStepsData = exportStepsData;
                    ExportFoodData = exportFoodData;
                    ExportWeightData = exportWeightData;
                    ExportBloodPressureData = exportBloodPressureData;
                    ExportBloodSugarData = exportBloodSugarData;
                }
            }
        }

        /**
         * Callback mechanism for when the select storage path dialog finishes.
         */
        interface ISelectStoragePathDialogObserver
        {
            /**
             * Called when the select storage path dialog finishes.
             *
             * @param storageFile The {@link OutputStream} to the location where the user data should be stored; {@code null} when the user cancelled.
             */
            void OnCompleted(final OutputStream storageFile);
        }

        /**
         * Callback mechanism for when delete user data dialog finishes.
         */
        interface IDeleteUserDataDialogObserver
        {
            /**
             * Called when the delete user data dialog finishes.
             *
             * @param confirmDeleteAllUserData {@code true} when the user confirmed that all user data
             *                                 should be deleted; otherwise {@code false}.
             */
            void OnCompleted(final boolean confirmDeleteAllUserData);
        }
    }
}
