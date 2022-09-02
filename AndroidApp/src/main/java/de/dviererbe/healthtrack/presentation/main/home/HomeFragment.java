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
import androidx.navigation.fragment.NavHostFragment;
import de.dviererbe.healthtrack.R;
import de.dviererbe.healthtrack.databinding.FragmentHomeBinding;
import de.dviererbe.healthtrack.infrastructure.INavigationRouter;
import de.dviererbe.healthtrack.presentation.FragmentBase;
import de.dviererbe.healthtrack.presentation.main.bloodpressure.BloodPressureDetailsFragment;
import de.dviererbe.healthtrack.presentation.main.bloodpressure.BloodPressureMergeFragment;
import de.dviererbe.healthtrack.presentation.main.stepcount.StepCountDetailsFragment;
import de.dviererbe.healthtrack.presentation.main.stepcount.StepCountMergeFragment;
import de.dviererbe.healthtrack.presentation.main.weight.WeightDetailsFragment;
import de.dviererbe.healthtrack.presentation.main.weight.WeightMergeFragment;

import java.time.LocalDate;
import java.util.UUID;

public class HomeFragment extends FragmentBase implements INavigationRouter
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
        _viewModel = GetViewModelFactory().CreateHomeViewModel(getLifecycle(), this);
        _binding = FragmentHomeBinding.inflate(inflater, container, false);

        _binding.WidgetList.setAdapter(new HomeViewModelListAdapter(getContext(), _viewModel));

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

    /**
     * Tries to navigate to the user settings UI (User Interface).
     *
     * @return {@code true} if the navigation attempt was successfully; otherwise {@code false}.
     */
    @Override
    public boolean TryNavigateToSettings()
    {
        return false;
    }

    /**
     * Tries to navigate to the create blood pressure record user interface.
     *
     * @return {@code true} if the navigation attempt was successfully; otherwise {@code false}.
     */
    @Override
    public boolean TryNavigateToCreateBloodPressureRecord()
    {
        try
        {
            NavHostFragment
                .findNavController(this)
                .navigate(R.id.action_nav_home_to_bloodPressureMergeFragment);

            return true;
        }
        catch (Exception exception)
        {
            return false;
        }
    }

    /**
     * Tries to navigate to the blood pressure record details user interface for
     * a record with a specific identifier.
     *
     * @param recordIdentifier The identifier of the record to see the details for.
     * @return {@code true} if the navigation attempt was successfully; otherwise {@code false}.
     */
    @Override
    public boolean TryNavigateToBloodPressureRecordDetails(UUID recordIdentifier)
    {
        try
        {
            final Bundle parameter = BloodPressureDetailsFragment.BundleParameter(recordIdentifier);

            NavHostFragment
                    .findNavController(this)
                    .navigate(R.id.action_nav_home_to_bloodPressureDetailsFragment, parameter);

            return true;
        }
        catch (Exception exception)
        {
            return false;
        }
    }

    /**
     * Tries to navigate to the edit blood pressure record user interface for
     * a record with a specific identifier.
     *
     * @param recordIdentifier The identifier of the record to edit.
     * @return {@code true} if the navigation attempt was successfully; otherwise {@code false}.
     */
    @Override
    public boolean TryNavigateToEditBloodPressureRecord(UUID recordIdentifier)
    {
        try
        {
            final Bundle parameter = BloodPressureMergeFragment.BundleParameter(recordIdentifier);

            NavHostFragment
                    .findNavController(this)
                    .navigate(R.id.action_nav_home_to_bloodPressureMergeFragment, parameter);

            return true;
        }
        catch (Exception exception)
        {
            return false;
        }
    }

    /**
     * Tries to navigate to the default step count goal editor user interface.
     *
     * @return {@code true} if the navigation attempt was successfully; otherwise {@code false}.
     */
    @Override
    public boolean TryNavigateToDefaultStepCountGoalEditor()
    {
        return false;
    }

    /**
     * Tries to navigate to the create step count record user interface.
     *
     * @return {@code true} if the navigation attempt was successfully; otherwise {@code false}.
     */
    @Override
    public boolean TryNavigateToCreateStepCountRecord()
    {
        try
        {
            NavHostFragment
                    .findNavController(this)
                    .navigate(R.id.action_nav_home_to_stepCountMergeFragment);

            return true;
        }
        catch (Exception exception)
        {
            return false;
        }
    }

    /**
     * Tries to navigate to the step count record details user interface for
     * a record with a specific identifier.
     *
     * @param recordIdentifier The identifier of the record to see the details for.
     * @return {@code true} if the navigation attempt was successfully; otherwise {@code false}.
     */
    @Override
    public boolean TryNavigateToStepCountRecordDetails(final UUID recordIdentifier)
    {
        try
        {
            final Bundle parameter = StepCountDetailsFragment.BundleParameter(recordIdentifier);

            NavHostFragment
                    .findNavController(this)
                    .navigate(R.id.action_nav_home_to_stepCountDetailsFragment, parameter);

            return true;
        }
        catch (Exception exception)
        {
            return false;
        }
    }

    /**
     * Tries to navigate to the edit step count record user interface for
     * a record with a specific identifier.
     *
     * @param recordIdentifier The identifier of the record to edit.
     * @return {@code true} if the navigation attempt was successfully; otherwise {@code false}.
     */
    @Override
    public boolean TryNavigateToEditStepCountRecord(final UUID recordIdentifier)
    {
        try
        {
            final Bundle parameter = StepCountMergeFragment.BundleParameter(recordIdentifier);

            NavHostFragment
                    .findNavController(this)
                    .navigate(R.id.action_nav_home_to_stepCountMergeFragment, parameter);

            return true;
        }
        catch (Exception exception)
        {
            return false;
        }
    }

    /**
     * Tries to navigate to the create weight record user interface.
     *
     * @return {@code true} if the navigation attempt was successfully; otherwise {@code false}.
     */
    @Override
    public boolean TryNavigateToCreateWeightRecord()
    {
        try
        {
            NavHostFragment
                    .findNavController(this)
                    .navigate(R.id.action_nav_home_to_weightMergeFragment);

            return true;
        }
        catch (Exception exception)
        {
            return false;
        }
    }

    /**
     * Tries to navigate to the weight record details user interface for
     * a record with a specific identifier.
     *
     * @param recordIdentifier The identifier of the record to see the details for.
     * @return {@code true} if the navigation attempt was successfully; otherwise {@code false}.
     */
    @Override
    public boolean TryNavigateToWeightRecordDetails(UUID recordIdentifier)
    {
        try
        {
            final Bundle parameter = WeightDetailsFragment.BundleParameter(recordIdentifier);

            NavHostFragment
                    .findNavController(this)
                    .navigate(R.id.action_nav_home_to_weightDetailsFragment, parameter);

            return true;
        }
        catch (Exception exception)
        {
            return false;
        }
    }

    /**
     * Tries to navigate to the edit weight record user interface for
     * a record with a specific identifier.
     *
     * @param recordIdentifier The identifier of the record to edit.
     * @return {@code true} if the navigation attempt was successfully; otherwise {@code false}.
     */
    @Override
    public boolean TryNavigateToEditWeightRecord(UUID recordIdentifier)
    {
        try
        {
            final Bundle parameter = WeightMergeFragment.BundleParameter(recordIdentifier);

            NavHostFragment
                .findNavController(this)
                .navigate(R.id.action_nav_home_to_weightMergeFragment, parameter);

            return true;
        }
        catch (Exception exception)
        {
            return false;
        }
    }

    /**
     * Tries to navigate to the preceding UI (User Interface).
     *
     * @return {@code true} if the navigation attempt was successfully; otherwise {@code false}.
     */
    @Override
    public boolean TryNavigateBack()
    {
        return false;
    }
}