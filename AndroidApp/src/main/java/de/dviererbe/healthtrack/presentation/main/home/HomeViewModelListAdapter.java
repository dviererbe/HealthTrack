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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import de.dviererbe.healthtrack.databinding.ItemBloodpressureRecordBinding;
import de.dviererbe.healthtrack.databinding.ItemStepcountRecordBinding;
import de.dviererbe.healthtrack.databinding.ItemWeightRecordBinding;
import de.dviererbe.healthtrack.presentation.main.bloodpressure.BloodPressureListItemViewModel;
import de.dviererbe.healthtrack.presentation.main.stepcount.StepCountListItemViewModel;
import de.dviererbe.healthtrack.presentation.main.weight.WeightListItemViewModel;

public class HomeViewModelListAdapter extends BaseAdapter
{
    private final Object[] _viewModels;
    private final Runnable _stepCountContextAction;
    private final Context _context;

    public HomeViewModelListAdapter(
        final Context context,
        final HomeViewModel homeViewModel)
    {
        _context = context;
        int i = 0;
        Object[] viewModels = new Object[3];

        if (homeViewModel.LatestBloodPressureOfToday != null)
        {
            viewModels[i] = homeViewModel.LatestBloodPressureOfToday;
            i++;
        }

        if (homeViewModel.StepCountOfToday != null)
        {
            viewModels[i] = homeViewModel.StepCountOfToday;
            i++;
        }

        _stepCountContextAction = homeViewModel.StepCountContextCommand;

        if (homeViewModel.LatestWeightOfToday != null)
        {
            viewModels[i] = homeViewModel.LatestWeightOfToday;
            i++;
        }

        if (i == 3)
        {
            _viewModels = viewModels;
        }
        else
        {
            _viewModels = new Object[i];
            System.arraycopy(viewModels, 0, _viewModels, 0, i);
        }
    }

    @Override
    public int getCount()
    {
        return _viewModels.length;
    }

    @Override
    public Object getItem(int position)
    {
        return _viewModels[position];
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup)
    {
        final LayoutInflater inflater = LayoutInflater.from(_context);

        if (_viewModels[position] instanceof BloodPressureListItemViewModel)
        {
            ItemBloodpressureRecordBinding binding =  ItemBloodpressureRecordBinding.inflate(inflater, viewGroup, false);
            binding.setViewModel((BloodPressureListItemViewModel)_viewModels[position]);

            return binding.getRoot();
        }

        if (_viewModels[position] instanceof StepCountListItemViewModel)
        {
            ItemStepcountRecordBinding binding =  ItemStepcountRecordBinding.inflate(inflater, viewGroup, false);
            binding.setViewModel((StepCountListItemViewModel)_viewModels[position]);
            binding.setShowDetailsCommand(_stepCountContextAction);

            return binding.getRoot();
        }

        if (_viewModels[position] instanceof WeightListItemViewModel)
        {
            ItemWeightRecordBinding binding =  ItemWeightRecordBinding.inflate(inflater, viewGroup, false);
            binding.setViewModel((WeightListItemViewModel)_viewModels[position]);

            return binding.getRoot();
        }

        return view;
    }
}
