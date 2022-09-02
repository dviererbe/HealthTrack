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
import de.dviererbe.healthtrack.R;
import de.dviererbe.healthtrack.databinding.FragmentStepcountDetailsBinding;
import de.dviererbe.healthtrack.presentation.FragmentBase;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class StepCountDetailsFragment extends FragmentBase
{
    private static final String PARAM_Identifier = "Identifier";

    private StepCountDetailsViewModel _viewModel;

    private FragmentStepcountDetailsBinding _binding;

    private UUID _identifier;

    public static Bundle BundleParameter(final UUID identifier)
    {
        final Bundle parameter = new Bundle();
        parameter.putString(PARAM_Identifier, identifier.toString());

        return parameter;
    }

    private void UnbundleParameter(Bundle parameter)
    {
        if (parameter == null || !parameter.containsKey(PARAM_Identifier)) return;

        _identifier = UUID.fromString(parameter.getString(PARAM_Identifier));
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
        _binding = FragmentStepcountDetailsBinding.inflate(inflater, container, false);
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
        _viewModel = GetViewModelFactory().CreateStepCountDetailsViewModel(_identifier);

        _binding.setViewModel(_viewModel);
        _binding.setLifecycleOwner(this.getViewLifecycleOwner());

        _viewModel.RegisterEventHandler(new ViewModelEventListener());
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
                TryNavigateToEditStepCountRecord();
                return true;
            case R.id.action_steps_details_delete:
                ShowConfirmDeleteDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Shows the user a dialog to confirm that the record should be deleted.
     */
    private void ShowConfirmDeleteDialog()
    {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setTitle(R.string.steps_details_dialog_delete_title);
        dialogBuilder.setMessage(R.string.steps_details_dialog_delete_message);

        dialogBuilder.setPositiveButton(
                R.string.steps_details_dialog_delete_confirm,
                (DialogInterface dialog, int which) ->
                {
                    _viewModel.Delete();
                });

        dialogBuilder.setNegativeButton(
                R.string.steps_details_dialog_delete_cancel,
                (DialogInterface dialog, int which) -> {});

        dialogBuilder.show();
    }

    private void TryNavigateToEditStepCountRecord()
    {
        try
        {
            Bundle parameter = StepCountMergeFragment.BundleParameter(_identifier);
            NavHostFragment
                .findNavController(this)
                .navigate(R.id.action_nav_stepCountDetailsFragment_to_stepCountMergeFragment, parameter);
        }
        catch (Exception exception)
        {
        }
    }

    private class ViewModelEventListener implements StepCountDetailsViewModel.IStepCountDetailsViewModelEventHandler
    {

        /**
         * Called when the record could not be deleted because of an error.
         */
        @Override
        public void RecordCouldNotBeDeleted()
        {
            ShowToastWithLongDuration(R.string.steps_details_notifications_delete_failure);
        }

        /**
         * Called when the record could be deleted successfully.
         */
        @Override
        public void RecordDeleted()
        {
            TryGoBack();
        }
    }
}