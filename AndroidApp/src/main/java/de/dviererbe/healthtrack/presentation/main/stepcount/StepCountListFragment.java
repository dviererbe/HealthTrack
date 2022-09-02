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
import de.dviererbe.healthtrack.presentation.FragmentBase;
import de.dviererbe.healthtrack.presentation.main.stepcount.StepCountListViewModel.IStepCountListViewModelEventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class StepCountListFragment extends FragmentBase
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
        _binding = FragmentStepcountListBinding.inflate(inflater, container, false);
        return _binding.getRoot();
    }

    /**
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * has returned, but before any saved state has been restored in to the view.
     * This gives subclasses a chance to initialize themselves once
     * they know their view hierarchy has been completely created.  The fragment's
     * view hierarchy is not however attached to its parent at this point.
     *
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(
            @NonNull @NotNull View view,
            @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        _viewModel = GetViewModelFactory().CreateStepCountListViewModel();
        _adapter = new StepCountListViewRecyclerViewAdapter(_viewModel, (final UUID uuid) -> () -> TryNavigateToStepCountRecordDetails(uuid));
        _binding.StepCountListRecyclerView.setAdapter(_adapter);
        _binding.StepCountListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        _viewModel.RegisterEventHandler(new StepCountListViewModelEventListener());
    }



    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();

        _viewModel.Dispose();
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
                TryNavigateToCreateStepCountRecord();
                return true;
            case R.id.action_steps_record_reset:
                ShowConfirmDeleteAllDialog();
                return true;
            case R.id.action_steps_change_default_stepgoal:
                TryNavigateToDefaultStepCountGoalEditor();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Shows the user a UI that asks for confirmation to delete all records.
     */
    public void ShowConfirmDeleteAllDialog()
    {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setTitle(R.string.steps_list_dialog_delete_title);
        dialogBuilder.setMessage(R.string.steps_list_dialog_delete_message);

        dialogBuilder.setPositiveButton(
            R.string.steps_list_dialog_delete_confirm,
            (DialogInterface dialog, int which) ->
            {
                _viewModel.DeleteAll();
            });

        dialogBuilder.setNegativeButton(
            R.string.steps_list_dialog_delete_cancel,
            (DialogInterface dialog, int which) -> {});

        dialogBuilder.show();
    }

    /**
     * Tries to navigate to the default step count goal editor user interface.
     */
    public void TryNavigateToDefaultStepCountGoalEditor()
    {
        try
        {
            NavHostFragment
                .findNavController(this)
                .navigate(R.id.action_nav_stepcounter_to_stepCountGoalDefaultEditorFragment);
        }
        catch (Exception exception)
        {
        }
    }

    /**
     * Tries to navigate to the create step count record user interface.
     */
    private void TryNavigateToCreateStepCountRecord()
    {
        try
        {
            NavHostFragment
                .findNavController(this)
                .navigate(R.id.action_nav_stepcounter_to_stepCountMergeFragment);
        }
        catch (Exception exception)
        {
        }
    }

    /**
     * Tries to navigate to the step count record details user interface for
     * a record with a specific identifier.
     *
     * @param recordIdentifier The identifier of the record to see the details for.
     * @return {@code true} if the navigation attempt was successfully; otherwise {@code false}.
     */
    private void TryNavigateToStepCountRecordDetails(final UUID recordIdentifier)
    {
        try
        {
            final Bundle parameter = StepCountDetailsFragment.BundleParameter(recordIdentifier);

            NavHostFragment
                    .findNavController(this)
                    .navigate(R.id.action_nav_stepcounter_to_stepCountDetailsFragment, parameter);
        }
        catch (Exception exception)
        {
        }
    }

    private class StepCountListViewModelEventListener implements IStepCountListViewModelEventHandler
    {

        /**
         * Called when the item list has changed.
         */
        @Override
        public void ListItemsChanged()
        {
            _adapter.notifyDataSetChanged();
        }

        /**
         * Called when the step count records have been deleted successfully.
         */
        @Override
        public void RecordsHaveBeenDeleted()
        {
            MakeToastWithShortDuration(R.string.steps_list_notifications_delete_success);
        }

        /**
         * Called when the step count records could not be deleted because of an error.
         */
        @Override
        public void RecordsCouldNotBeDeleted()
        {
            MakeToastWithShortDuration(R.string.steps_list_notifications_delete_failure);
        }
    }
}