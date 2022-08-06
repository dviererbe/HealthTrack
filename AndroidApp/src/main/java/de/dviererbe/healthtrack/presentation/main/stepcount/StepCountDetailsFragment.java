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


package de.dviererbe.healthtrack.presentation.main.stepcount;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.fragment.NavHostFragment;
import de.dviererbe.healthtrack.R;
import de.dviererbe.healthtrack.databinding.FragmentStepcountDetailsBinding;
import de.dviererbe.healthtrack.infrastructure.INavigationRouter;
import de.dviererbe.healthtrack.presentation.FragmentBase;
import de.dviererbe.healthtrack.presentation.main.stepcount.StepCountDetailsViewModel.IStepCountDetailsView;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class StepCountDetailsFragment
        extends FragmentBase
        implements IStepCountDetailsView, INavigationRouter
{
    private static final String PARAM_Day = "Day";

    private StepCountDetailsViewModel _viewModel;

    private FragmentStepcountDetailsBinding _binding;

    private LocalDate _day;

    public static Bundle BundleParameter(final LocalDate day)
    {
        final Bundle parameter = new Bundle();
        parameter.putString(PARAM_Day, day.format(DateTimeFormatter.ISO_LOCAL_DATE));

        return parameter;
    }

    private void UnbundleParameter(Bundle parameter)
    {
        if (parameter == null || !parameter.containsKey(PARAM_Day)) return;

        _day = LocalDate.parse(parameter.getString(PARAM_Day), DateTimeFormatter.ISO_LOCAL_DATE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        UnbundleParameter(getArguments());
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        _viewModel = GetViewModelFactory()
                .CreateStepCountDetailsViewModel(getLifecycle(), this, this, _day);

        _binding = FragmentStepcountDetailsBinding
                .inflate(inflater, container, false);
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
        _viewModel = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater)
    {
        inflater.inflate(R.menu.fragment_steps_details_actions, menu);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_steps_details_edit:
                _viewModel.Edit();
                return true;
            case R.id.action_steps_details_delete:
                _viewModel.Delete();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Notifies the user that the record could not be deleted because of an error.
     */
    @Override
    public void NotifyUserThatRecordCouldNotBeDeleted()
    {
        ShowToastWithLongDuration(R.string.steps_details_notifications_delete_failure);
    }

    /**
     * Shows the user a dialog to confirm that the record should be deleted.
     *
     * @param callback a reference to a callback mechanism when the user made a decision.
     */
    @Override
    public void ShowConfirmDeleteDialog(IConfirmDeleteDialogObserver callback)
    {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setTitle(R.string.steps_details_dialog_delete_title);
        dialogBuilder.setMessage(R.string.steps_details_dialog_delete_message);

        dialogBuilder.setPositiveButton(
                R.string.steps_details_dialog_delete_confirm,
                (DialogInterface dialog, int which) ->
                {
                    callback.OnCompleted(true);
                });

        dialogBuilder.setNegativeButton(
                R.string.steps_details_dialog_delete_cancel,
                (DialogInterface dialog, int which) ->
                {
                    callback.OnCompleted(false);
                });

        dialogBuilder.show();
    }

    /**
     * Tries to navigate to the user settings UI (User Interface).
     *
     * @return {@code true} if the navigation attempt was successfully; otherwise {@code false}.
     */
    @Override
    public boolean TryNavigateToSettings()
    {
        return false;
    }

    /**
     * Tries to navigate to the create blood pressure record user interface.
     *
     * @return {@code true} if the navigation attempt was successfully; otherwise {@code false}.
     */
    @Override
    public boolean TryNavigateToCreateBloodPressureRecord()
    {
        return false;
    }

    /**
     * Tries to navigate to the blood pressure record details user interface for
     * a record with a specific identifier.
     *
     * @param recordIdentifier The identifier of the record to see the details for.
     * @return {@code true} if the navigation attempt was successfully; otherwise {@code false}.
     */
    @Override
    public boolean TryNavigateToBloodPressureRecordDetails(UUID recordIdentifier)
    {
        return false;
    }

    /**
     * Tries to navigate to the edit blood pressure record user interface for
     * a record with a specific identifier.
     *
     * @param recordIdentifier The identifier of the record to edit.
     * @return {@code true} if the navigation attempt was successfully; otherwise {@code false}.
     */
    @Override
    public boolean TryNavigateToEditBloodPressureRecord(UUID recordIdentifier)
    {
        return false;
    }

    /**
     * Tries to navigate to the default step count goal editor user interface.
     *
     * @return {@code true} if the navigation attempt was successfully; otherwise {@code false}.
     */
    @Override
    public boolean TryNavigateToDefaultStepCountGoalEditor()
    {
        return false;
    }

    /**
     * Tries to navigate to the create step count record user interface.
     *
     * @return {@code true} if the navigation attempt was successfully; otherwise {@code false}.
     */
    @Override
    public boolean TryNavigateToCreateStepCountRecord()
    {
        return false;
    }

    /**
     * Tries to navigate to the step count record details user interface for
     * a record with a specific identifier.
     *
     * @param dateOfDay The date of the day of the record to see the details for.
     * @return {@code true} if the navigation attempt was successfully; otherwise {@code false}.
     */
    @Override
    public boolean TryNavigateToStepCountRecordDetails(LocalDate dateOfDay)
    {
        return false;
    }

    /**
     * Tries to navigate to the edit step count record user interface for
     * a record with a specific identifier.
     *
     * @param dateOfDay The date of the day of the record to edit.
     * @return {@code true} if the navigation attempt was successfully; otherwise {@code false}.
     */
    @Override
    public boolean TryNavigateToEditStepCountRecord(LocalDate dateOfDay)
    {
        try
        {
            Bundle parameter = StepCountMergeFragment.BundleParameter(dateOfDay);
            NavHostFragment
                .findNavController(this)
                .navigate(R.id.action_nav_stepCountDetailsFragment_to_stepCountMergeFragment, parameter);

            return true;
        }
        catch (Exception exception)
        {
            return false;
        }
    }

    /**
     * Tries to navigate to the create weight record user interface.
     *
     * @return {@code true} if the navigation attempt was successfully; otherwise {@code false}.
     */
    @Override
    public boolean TryNavigateToCreateWeightRecord()
    {
        return false;
    }

    /**
     * Tries to navigate to the weight record details user interface for
     * a record with a specific identifier.
     *
     * @param recordIdentifier The identifier of the record to see the details for.
     * @return {@code true} if the navigation attempt was successfully; otherwise {@code false}.
     */
    @Override
    public boolean TryNavigateToWeightRecordDetails(UUID recordIdentifier)
    {
        return false;
    }

    /**
     * Tries to navigate to the edit weight record user interface for
     * a record with a specific identifier.
     *
     * @param recordIdentifier The identifier of the record to edit.
     * @return {@code true} if the navigation attempt was successfully; otherwise {@code false}.
     */
    @Override
    public boolean TryNavigateToEditWeightRecord(UUID recordIdentifier)
    {
        return false;
    }

    /**
     * Tries to navigate to the preceding UI (User Interface).
     *
     * @return {@code true} if the navigation attempt was successfully; otherwise {@code false}.
     */
    @Override
    public boolean TryNavigateBack()
    {
        return TryGoBack();
    }
}