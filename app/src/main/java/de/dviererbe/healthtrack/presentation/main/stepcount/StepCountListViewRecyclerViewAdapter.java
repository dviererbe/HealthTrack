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

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.dviererbe.healthtrack.databinding.ItemStepcountRecordBinding;
import org.jetbrains.annotations.NotNull;

public class StepCountListViewRecyclerViewAdapter
        extends RecyclerView.Adapter<StepCountListViewRecyclerViewAdapter.StepCountRecordItemViewHolder>
{
    private final StepCountListViewModel _viewModel;

    public StepCountListViewRecyclerViewAdapter(final StepCountListViewModel viewModel)
    {
        _viewModel = viewModel;
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @NotNull
    @Override
    public StepCountRecordItemViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemStepcountRecordBinding binding = ItemStepcountRecordBinding.inflate(layoutInflater, parent, false);

        return new StepCountRecordItemViewHolder(binding);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBindViewHolder(@NonNull @NotNull StepCountRecordItemViewHolder holder, int position)
    {
        holder.Bind(_viewModel.GetRecord(position));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getItemCount()
    {
        return _viewModel.GetRecordCount();
    }

    public static class StepCountRecordItemViewHolder extends RecyclerView.ViewHolder
    {
        @NotNull
        private final ItemStepcountRecordBinding _binding;

        public StepCountRecordItemViewHolder(ItemStepcountRecordBinding binding)
        {
            super(binding.getRoot());
            _binding = binding;
        }

        void Bind(StepCountListItemViewModel viewModel)
        {
            _binding.setViewModel(viewModel);
        }
    }
}
