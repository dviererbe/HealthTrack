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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import de.dviererbe.healthtrack.databinding.FragmentHomeBinding;
import de.dviererbe.healthtrack.presentation.FragmentBase;

public class HomeFragment extends FragmentBase
{
    private HomeViewModel _viewModel;
    private FragmentHomeBinding _binding;

    /**
     * {@inheritDoc}
     */
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState)
    {
        _viewModel = GetViewModelFactory().CreateHomeViewModel(getLifecycle());
        _binding = FragmentHomeBinding.inflate(inflater, container, false);
        _binding.DaySummariesList.setAdapter(new DaySummariesRecyclerViewAdapter(_viewModel.DaySummaries));
        _binding.DaySummariesList.setLayoutManager(new LinearLayoutManager(getActivity()));

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
}