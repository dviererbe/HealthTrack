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

package de.dviererbe.healthtrack.presentation.main.weight;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import de.dviererbe.healthtrack.R;
import de.dviererbe.healthtrack.databinding.FragmentWeightListBinding;
import de.dviererbe.healthtrack.presentation.FragmentBase;
import de.dviererbe.healthtrack.presentation.main.weight.WeightListViewModel.IWeightListView;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class WeightListFragment extends FragmentBase implements IWeightListView
{
    private WeightListViewModel _viewModel;
    private FragmentWeightListBinding _binding;

    private WeightListViewModelRecyclerViewAdapter _adapter;

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
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState)
    {
        _viewModel = GetViewModelFactory().CreateWeightListViewModel(getLifecycle(), this);
        _binding = FragmentWeightListBinding.inflate(inflater, container, false);

        _adapter = new WeightListViewModelRecyclerViewAdapter(_viewModel);
        _binding.WeightListRecyclerView.setAdapter(_adapter);
        _binding.WeightListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

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
        _adapter = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater)
    {
        inflater.inflate(R.menu.fragment_weight_list_actions, menu);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_weight_record_create:
                _viewModel.CreateRecord();
                return true;
            case R.id.action_weight_record_reset:
                _viewModel.DeleteAll();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Forces the list to reload.
     */
    @Override
    public void OnListItemsChanged()
    {
        _adapter.notifyDataSetChanged();
    }

    /**
     * Navigates the user to a UI for creating a specific record.
     */
    @Override
    public void NavigateToCreateView()
    {
        NavHostFragment.findNavController(this).navigate(R.id.action_nav_weight_to_weightMergeFragment);
    }

    /**
     * Navigates the user to a UI for showing the details of a specific record.
     *
     * @param recordIdentifier
     */
    @Override
    public void NavigateToDetailsView(UUID recordIdentifier)
    {
        final Bundle parameter = WeightDetailsFragment.BundleParameter(recordIdentifier);

        NavHostFragment
            .findNavController(this)
            .navigate(R.id.action_nav_weight_to_weightDetailsFragment, parameter);
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
        dialogBuilder.setTitle(R.string.weight_list_dialog_delete_title);
        dialogBuilder.setMessage(R.string.weight_list_dialog_delete_message);

        dialogBuilder.setPositiveButton(
            R.string.weight_list_dialog_delete_confirm,
            (DialogInterface dialog, int which) ->
            {
                callback.OnCompleted(true);
            });

        dialogBuilder.setNegativeButton(
            R.string.weight_list_dialog_delete_cancel,
            (DialogInterface dialog, int which) ->
            {
                callback.OnCompleted(false);
            });

        dialogBuilder.show();
    }

    /**
     * Notifies the user that the weight records have been deleted successfully.
     */
    @Override
    public void NotifyUserThatRecordsHaveBeenDeleted()
    {
        MakeToastWithShortDuration(R.string.weight_list_notifications_delete_success);
    }

    /**
     * Notifies the user that the weight record could not be deleted because of an error.
     */
    @Override
    public void NotifyUserThatRecordsCouldNotBeDeleted()
    {
        ShowToastWithLongDuration(R.string.weight_list_notifications_delete_failure);
    }
}