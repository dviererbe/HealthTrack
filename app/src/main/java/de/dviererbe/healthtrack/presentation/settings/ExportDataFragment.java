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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.DialogFragment;
import de.dviererbe.healthtrack.HealthTrackApp;
import de.dviererbe.healthtrack.databinding.FragmentExportDataBinding;
import de.dviererbe.healthtrack.presentation.settings.ExportDataDialogViewModel.IExportDataDialogView;
import de.dviererbe.healthtrack.presentation.settings.SettingsViewModel.ISettingsView.IExportUserDataDialogObserver.ExportUserDataDialogResult;

public class ExportDataFragment
        extends DialogFragment
        implements IExportDataDialogView
{
    private ExportDataDialogViewModel _viewModel;
    private FragmentExportDataBinding _binding;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment {@link ExportDataFragment}.
     */
    public static ExportDataFragment NewInstance()
    {
        return new ExportDataFragment();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState)
    {
        _viewModel = HealthTrackApp
                .GetDependenciesViaActivity(getActivity())
                .GetViewModelFactory()
                .CreateExportDataViewModel(getLifecycle(), this);

        _binding = FragmentExportDataBinding.inflate(inflater, container, false);
        _binding.setViewModel(_viewModel);

        return _binding.getRoot();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        _binding = null;
    }

    /**
     * Gets if the user wants to export steps data.
     *
     * @return {@code true} if the user wants to export steps data; otherwise {@code false}.
     */
    @Override
    public boolean GetExportStepsData()
    {
        return _binding.checkboxExportStepsData.isChecked();
    }

    /**
     * Gets if the user wants to export weight data.
     *
     * @return {@code true} if the user wants to export weight data; otherwise {@code false}.
     */
    @Override
    public boolean GetExportWeightData()
    {
        return _binding.checkboxExportWeightData.isChecked();
    }

    /**
     * Gets if the user wants to export food data.
     *
     * @return {@code true} if the user wants to export food data; otherwise {@code false}.
     */
    @Override
    public boolean GetExportFoodData()
    {
        return _binding.checkboxExportFoodData.isChecked();
    }

    /**
     * Gets if the user wants to export blood pressure data.
     *
     * @return {@code true} if the user wants to export blood pressure data; otherwise {@code false}.
     */
    @Override
    public boolean GetExportBloodPressureData()
    {
        return _binding.checkboxExportBloodpressureData.isChecked();
    }

    /**
     * Gets if the user wants to export blood sugar data.
     *
     * @return {@code true} if the user wants to export blood sugar data; otherwise {@code false}.
     */
    @Override
    public boolean GetExportBloodSugarData()
    {
        return _binding.checkboxExportBloodsugarData.isChecked();
    }

    /**
     * Closes the user interface to export user data.
     *
     * @param result the user selected data that should be exported.
     */
    @Override
    public void Close(ExportUserDataDialogResult result)
    {
        int resultCode;
        final Intent intent = new Intent();

        if (result != null)
        {
            resultCode = Activity.RESULT_OK;

            Bundle bundle = new Bundle();
            bundle.putBooleanArray("ExportUserDataDialogResult", new boolean[]
                {
                    result.ExportStepsData,
                    result.ExportFoodData,
                    result.ExportWeightData,
                    result.ExportBloodPressureData,
                    result.ExportBloodSugarData,
                });

            intent.putExtras(bundle);
        }
        else
        {
            resultCode = Activity.RESULT_CANCELED;
        }

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
        dismiss();
    }

}