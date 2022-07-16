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

package de.dviererbe.healthtrack.presentation.main.home;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;
import de.dviererbe.healthtrack.databinding.ItemDaySummaryBinding;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DaySummariesRecyclerViewAdapter
        extends RecyclerView.Adapter<DaySummariesRecyclerViewAdapter.DailySummaryViewHolder>
{
    private final LiveData<List<DaySummaryViewModel>> _daySummaries;

    public DaySummariesRecyclerViewAdapter(LiveData<List<DaySummaryViewModel>> daySummaries)
    {
        _daySummaries = daySummaries;
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @NotNull
    @Override
    public DailySummaryViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemDaySummaryBinding binding = ItemDaySummaryBinding.inflate(layoutInflater, parent, false);

        return new DailySummaryViewHolder(binding);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBindViewHolder(
            @NonNull @NotNull DailySummaryViewHolder holder,
            int position)
    {
        holder.Bind(_daySummaries.getValue().get(position));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getItemCount()
    {
        return _daySummaries.getValue().size();
    }

    public static class DailySummaryViewHolder extends RecyclerView.ViewHolder
    {
        private final ItemDaySummaryBinding _binding;

        public DailySummaryViewHolder(ItemDaySummaryBinding binding)
        {
            super(binding.getRoot());
            _binding = binding;
        }

        void Bind(DaySummaryViewModel viewModel)
        {
            _binding.setViewModel(viewModel);
        }
    }
}
