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

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.dviererbe.healthtrack.databinding.ItemWeightRecordBinding;
import org.jetbrains.annotations.NotNull;

public class WeightListViewModelRecyclerViewAdapter
        extends RecyclerView.Adapter<WeightListViewModelRecyclerViewAdapter.WeightRecordItemViewHolder>
{
    private final WeightListViewModel _viewModel;

    public WeightListViewModelRecyclerViewAdapter(final WeightListViewModel viewModel)
    {
        _viewModel = viewModel;
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @NotNull
    @Override
    public WeightRecordItemViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemWeightRecordBinding binding = ItemWeightRecordBinding.inflate(layoutInflater, parent, false);

        return new WeightRecordItemViewHolder(binding);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBindViewHolder(@NonNull @NotNull WeightRecordItemViewHolder holder, int position)
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

    public static class WeightRecordItemViewHolder extends RecyclerView.ViewHolder
    {
        @NotNull
        private final de.dviererbe.healthtrack.databinding.ItemWeightRecordBinding _binding;

        public WeightRecordItemViewHolder(ItemWeightRecordBinding binding)
        {
            super(binding.getRoot());
            _binding = binding;
        }

        void Bind(WeightListItemViewModel viewModel)
        {
            _binding.setViewModel(viewModel);
        }
    }
}
