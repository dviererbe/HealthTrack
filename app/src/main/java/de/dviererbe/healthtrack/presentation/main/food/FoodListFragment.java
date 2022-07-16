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

package de.dviererbe.healthtrack.presentation.main.food;

import android.os.Bundle;
import android.util.Log;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import de.dviererbe.healthtrack.R;
import de.dviererbe.healthtrack.databinding.FragmentFoodListBinding;
import de.dviererbe.healthtrack.presentation.FragmentBase;
import org.jetbrains.annotations.NotNull;

public class FoodListFragment extends FragmentBase
{
    private FoodListViewModel _viewModel;
    private FragmentFoodListBinding _binding;

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
        _viewModel = GetViewModelFactory().CreateFoodListViewModel(getLifecycle());
        _binding = FragmentFoodListBinding.inflate(inflater, container, false);

        View root = _binding.getRoot();

        return root;
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
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater)
    {
        inflater.inflate(R.menu.fragment_food_list_actions, menu);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_food_record_create:
                _viewModel.CreateRecord();
                return true;
            case R.id.action_food_record_reset:
                _viewModel.DeleteAll();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}