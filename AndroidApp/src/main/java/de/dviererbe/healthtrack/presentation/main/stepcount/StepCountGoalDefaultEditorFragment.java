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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import de.dviererbe.healthtrack.BR;
import de.dviererbe.healthtrack.IDisposable;
import de.dviererbe.healthtrack.R;
import de.dviererbe.healthtrack.databinding.FragmentStepcountGoalDefaultEditorBinding;
import de.dviererbe.healthtrack.presentation.FragmentBase;
import de.dviererbe.healthtrack.presentation.main.stepcount.StepCountGoalDefaultEditorViewModel.IStepCountGoalDefaultEditorViewModelEventHandler;
import org.jetbrains.annotations.NotNull;

public class StepCountGoalDefaultEditorFragment extends FragmentBase
{
    private StepCountGoalDefaultEditorViewModelDataBindingAdapter _viewModelAdapter;
    private FragmentStepcountGoalDefaultEditorBinding _binding;

    /**
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */
    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            final ViewGroup container,
            final Bundle savedInstanceState)
    {
        _binding = FragmentStepcountGoalDefaultEditorBinding.inflate(inflater, container, false);
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
        super.onViewCreated(view, savedInstanceState);

        final StepCountGoalDefaultEditorViewModel viewModel =
            GetViewModelFactory()
            .CreateStepCountGoalDefaultEditorViewModel();

        _viewModelAdapter = new StepCountGoalDefaultEditorViewModelDataBindingAdapter(viewModel);
        _binding.setViewModel(_viewModelAdapter);
        _binding.setLifecycleOwner(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();

        _viewModelAdapter.Dispose();
        _viewModelAdapter = null;
        _binding = null;
    }

    public class StepCountGoalDefaultEditorViewModelDataBindingAdapter
        extends BaseObservable
        implements IDisposable
    {
        private final StepCountGoalDefaultEditorViewModel _viewModel;
        private final IDisposable _viewModelEventListener;

        public StepCountGoalDefaultEditorViewModelDataBindingAdapter(
            final StepCountGoalDefaultEditorViewModel viewModel)
        {
            _viewModel = viewModel;
            _viewModelEventListener = _viewModel.RegisterEventHandler(new ViewModelEventHandler());
        }

        @Bindable
        public String getGoal()
        {
            return _viewModel.GetGoalString();
        }

        public void setGoal(final String text)
        {
            _viewModel.SetGoal(text);
        }

        @Bindable
        public int getGoalErrorMessageVisibility()
        {
            return _viewModel.IsPulseValueInvalid()
                ? View.VISIBLE
                : View.INVISIBLE;
        }

        @Bindable
        public boolean getIsSaveButtonEnabled()
        {
            return _viewModel.CanValuesBeSaved();
        }

        /**
         * Performs application-defined tasks associated with freeing, releasing, or resetting resources.
         */
        @Override
        public void Dispose()
        {
            _viewModelEventListener.Dispose();
            _viewModel.Dispose();
        }

        private class ViewModelEventHandler
            implements IStepCountGoalDefaultEditorViewModelEventHandler
        {

            /**
             * Called when the goal property changed.
             */
            @Override
            public void GoalValueChanged()
            {
                notifyPropertyChanged(BR.goal);
            }

            /**
             * Called when the validity of the goal property changed.
             */
            @Override
            public void GoalValueValidityChanged()
            {
                notifyPropertyChanged(BR.goalErrorMessageVisibility);
            }

            /**
             * Called if the state changes if {@link StepCountGoalDefaultEditorViewModel#Save()} has any effects.
             */
            @Override
            public void CanValuesBeSavedChanged()
            {
                notifyPropertyChanged(BR.isSaveButtonEnabled);
            }

            /**
             * Called when the default step count goal was updated successfully.
             */
            @Override
            public void DefaultStepCountGoalCouldNotBeUpdated()
            {
                ShowToastWithLongDuration(R.string.steps_editdefaultstepcountgoal_notifications_saveerror);
            }

            /**
             * Called when the default step count goal could not be updated because of an error.
             */
            @Override
            public void DefaultStepCountGoalCouldBeUpdated()
            {
                TryGoBack();
            }
        }
    }
}