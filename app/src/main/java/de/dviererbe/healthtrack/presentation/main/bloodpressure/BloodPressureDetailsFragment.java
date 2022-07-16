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

package de.dviererbe.healthtrack.presentation.main.bloodpressure;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.fragment.NavHostFragment;
import de.dviererbe.healthtrack.R;
import de.dviererbe.healthtrack.databinding.FragmentBloodpressureDetailsBinding;
import de.dviererbe.healthtrack.presentation.FragmentBase;
import de.dviererbe.healthtrack.presentation.main.bloodpressure.BloodPressureDetailsViewModel.IBloodPressureDetailsView;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class BloodPressureDetailsFragment
        extends FragmentBase
        implements IBloodPressureDetailsView
{
    private static final String PARAM_Identifier = "Identifier";

    private BloodPressureDetailsViewModel _viewModel;

    private FragmentBloodpressureDetailsBinding _binding;

    private UUID _recordIdentifier;

    public static Bundle BundleParameter(UUID recordIdentifier)
    {
        final Bundle parameter = new Bundle();
        parameter.putString(PARAM_Identifier, recordIdentifier.toString());

        return parameter;
    }

    private void UnbundleParameter(Bundle parameter)
    {
        if (parameter == null || !parameter.containsKey(PARAM_Identifier)) return;

        _recordIdentifier = UUID.fromString(parameter.getString(PARAM_Identifier));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        UnbundleParameter(getArguments());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        _viewModel = GetViewModelFactory().CreateBloodPressureDetailsViewModel(
                getLifecycle(), this, _recordIdentifier);

        _binding = FragmentBloodpressureDetailsBinding.inflate(inflater, container, false);
        _binding.setViewModel(_viewModel);

        return _binding.getRoot();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater)
    {
        inflater.inflate(R.menu.fragment_weight_details_actions, menu);
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
     * Navigates the user to a UI for editing a specific blood pressure record.
     *
     * @param recordIdentifier  the identifier of the record
     */
    @Override
    public void NavigateToEditView(final UUID recordIdentifier)
    {
        final Bundle parameter = BloodPressureMergeFragment.BundleParameter(recordIdentifier);

        NavHostFragment
                .findNavController(this)
                .navigate(R.id.action_nav_bloodPressureDetailsFragment_to_bloodPressureMergeFragment, parameter);
    }

    /**
     * Shows the user a dialog to pick confirm that the record should be deleted.
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
     * The view should navigate up in the navigation stack.
     */
    @Override
    public void GoBack()
    {
        NavHostFragment.findNavController(this).popBackStack();
    }
}