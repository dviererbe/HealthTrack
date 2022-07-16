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
import de.dviererbe.healthtrack.presentation.settings.SettingsViewModel.ISettingsView.IExportUserDataDialogObserver.ExportUserDataDialogResult;

public class ExportDataDialogViewModel implements IDisposable
{
    private final IExportDataDialogView _view;

    public ExportDataDialogViewModel(IExportDataDialogView view)
    {
        _view = view;
    }

    /**
     * User requests to export the selected user data.
     */
    public void ExportData()
    {
        _view.Close(new ExportUserDataDialogResult(
            _view.GetExportStepsData(),
            _view.GetExportFoodData(),
            _view.GetExportWeightData(),
            _view.GetExportBloodPressureData(),
            _view.GetExportBloodSugarData()));
    }

    /**
     * User requests to close the user interface to export user data.
     */
    public void Close()
    {
        _view.Close(null);
    }

    /**
     * Performs application-defined tasks associated with freeing, releasing, or resetting resources.
     */
    @Override
    public void Dispose()
    {
        // nothing to do here
    }

    /**
     * Interface for the user interface to export user data.
     */
    public interface IExportDataDialogView
    {
        /**
         * Gets if the user wants to export steps data.
         *
         * @return {@code true} if the user wants to export steps data; otherwise {@code false}.
         */
        boolean GetExportStepsData();

        /**
         * Gets if the user wants to export weight data.
         *
         * @return {@code true} if the user wants to export weight data; otherwise {@code false}.
         */
        boolean GetExportWeightData();

        /**
         * Gets if the user wants to export food data.
         *
         * @return {@code true} if the user wants to export food data; otherwise {@code false}.
         */
        boolean GetExportFoodData();

        /**
         * Gets if the user wants to export blood pressure data.
         *
         * @return {@code true} if the user wants to export blood pressure data; otherwise {@code false}.
         */
        boolean GetExportBloodPressureData();

        /**
         * Gets if the user wants to export blood sugar data.
         *
         * @return {@code true} if the user wants to export blood sugar data; otherwise {@code false}.
         */
        boolean GetExportBloodSugarData();

        /**
         * Closes the user interface to export user data.
         */
        void Close(ExportUserDataDialogResult result);
    }
}
