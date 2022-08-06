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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import de.dviererbe.healthtrack.R;
import de.dviererbe.healthtrack.databinding.FragmentStepcountListBinding;
import de.dviererbe.healthtrack.infrastructure.INavigationRouter;
import de.dviererbe.healthtrack.presentation.FragmentBase;
import de.dviererbe.healthtrack.presentation.main.stepcount.StepCountListViewModel.IStepCountListView;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public class StepCountListFragment
        extends FragmentBase
        implements IStepCountListView, INavigationRouter
{
    private StepCountListViewModel _viewModel;
    private FragmentStepcountListBinding _binding;

    private StepCountListViewRecyclerViewAdapter _adapter;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState)
    {
        _viewModel = GetViewModelFactory().CreateStepCountListViewModel(getLifecycle(), this,this);
        _binding = FragmentStepcountListBinding.inflate(inflater, container, false);

        _adapter = new StepCountListViewRecyclerViewAdapter(_viewModel);
        _binding.StepCountListRecyclerView.setAdapter(_adapter);
        _binding.StepCountListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return _binding.getRoot();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        _viewModel = null;
        _binding = null;
        _adapter = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreateOptionsMenu(
            @NonNull @NotNull Menu menu,
            @NonNull @NotNull MenuInflater inflater)
    {
        inflater.inflate(R.menu.fragment_steps_list_actions, menu);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_steps_record_create:
                _viewModel.CreateRecord();
                return true;
            case R.id.action_steps_record_reset:
                _viewModel.DeleteAll();
                return true;
            case R.id.action_steps_change_default_stepgoal:
                _viewModel.EditDefaultStepCountGoal();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Notifies the {@link IStepCountListView} that the item list has changed.
     */
    @Override
    public void OnListItemsChanged()
    {
        _adapter.notifyDataSetChanged();
    }

    /**
     * Shows the user a UI that asks for confirmation to delete all records.
     *
     * @param callback a reference to a callback mechanism when the user made a decision.
     */
    @Override
    public void ShowConfirmDeleteAllDialog(IConfirmDeleteAllDialogObserver callback)
    {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setTitle(R.string.steps_list_dialog_delete_title);
        dialogBuilder.setMessage(R.string.steps_list_dialog_delete_message);

        dialogBuilder.setPositiveButton(
            R.string.steps_list_dialog_delete_confirm,
            (DialogInterface dialog, int which) ->
            {
                callback.OnCompleted(true);
            });

        dialogBuilder.setNegativeButton(
            R.string.steps_list_dialog_delete_cancel,
            (DialogInterface dialog, int which) ->
            {
                callback.OnCompleted(false);
            });

        dialogBuilder.show();
    }

    /**
     * Notifies the user that the step count records have been deleted successfully.
     */
    @Override
    public void NotifyUserThatRecordsHaveBeenDeleted()
    {
        MakeToastWithShortDuration(R.string.steps_list_notifications_delete_success);
    }

    /**
     * Notifies the user that the step count records could not be deleted because of an error.
     */
    @Override
    public void NotifyUserThatRecordsCouldNotBeDeleted()
    {
        MakeToastWithShortDuration(R.string.steps_list_notifications_delete_failure);
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
        try
        {
            NavHostFragment
                .findNavController(this)
                .navigate(R.id.action_nav_stepcounter_to_stepCountGoalDefaultEditorFragment);

            return true;
        }
        catch (Exception exception)
        {
            return false;
        }
    }

    /**
     * Tries to navigate to the create step count record user interface.
     *
     * @return {@code true} if the navigation attempt was successfully; otherwise {@code false}.
     */
    @Override
    public boolean TryNavigateToCreateStepCountRecord()
    {
        try
        {
            NavHostFragment
                .findNavController(this)
                .navigate(R.id.action_nav_stepcounter_to_stepCountMergeFragment);

            return true;
        }
        catch (Exception exception)
        {
            return false;
        }
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
        try
        {
            final Bundle parameter = StepCountDetailsFragment.BundleParameter(dateOfDay);

            NavHostFragment
                    .findNavController(this)
                    .navigate(R.id.action_nav_stepcounter_to_stepCountDetailsFragment, parameter);

            return true;
        }
        catch (Exception exception)
        {
            return false;
        }
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
        return false;
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
        return false;
    }
}