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

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.fragment.app.Fragment;
import de.dviererbe.healthtrack.BR;
import de.dviererbe.healthtrack.IDisposable;
import de.dviererbe.healthtrack.R;
import de.dviererbe.healthtrack.databinding.FragmentStepcountMergeBinding;
import de.dviererbe.healthtrack.presentation.FragmentBase;
import de.dviererbe.healthtrack.presentation.main.stepcount.StepCountMergeViewModel.IStepCountMergeViewModelEventHandler;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StepCountMergeFragment extends FragmentBase
{
    private static final String PARAM_Day = "Day";

    private StepCountMergeViewModelDataBindingAdapter _viewModelAdapter;

    private FragmentStepcountMergeBinding _binding;

    private LocalDate _day;

    public static Bundle BundleParameter(final LocalDate day)
    {
        final Bundle parameter = new Bundle();
        parameter.putString(PARAM_Day, day.format(DateTimeFormatter.ISO_LOCAL_DATE));

        return parameter;
    }

    private void UnbundleParameter(Bundle parameter)
    {
        if (parameter == null || !parameter.containsKey(PARAM_Day)) return;

        _day = LocalDate.parse(parameter.getString(PARAM_Day), DateTimeFormatter.ISO_LOCAL_DATE);
    }

    /**
     * Called to do initial creation of a fragment. This is called after {@link Fragment#onAttach(Activity)} and
     * before {@link Fragment#onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * Note that this can be called while the fragment's activity is still in the process of being created.
     * As such, you can not rely on things like the activity's content view hierarchy being initialized at this point.
     * If you want to do work once the activity itself is created, see {@link Fragment#onActivityCreated(Bundle)}.
     * Any restored child fragments will be created before the base Fragment.onCreate method returns
     *
     * @param savedInstanceState
     *      If the fragment is being re-created from a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        UnbundleParameter(getArguments());
    }

    /**
     * Called to have the fragment instantiate its user interface view. This is optional, and non-graphical
     * fragments can return null. This will be called between {@link Fragment#onCreate(Bundle)} and
     * {@link Fragment#onActivityCreated(Bundle)}. A default View can be returned by calling
     * {@link Fragment#Fragment(int)} in your constructor. Otherwise, this method returns null.
     * It is recommended to only inflate the layout in this method and move logic that operates on the returned
     * View to {@link Fragment#onViewCreated(View, Bundle)}.
     * If you return a View from here, you will later be called in {@link Fragment#onDestroyView} when the view
     * is being released.
     *
     * @param inflater
     *      The LayoutInflater object that can be used to inflate
     *      any views in the fragment,
     * @param container
     *      If non-null, this is the parent view that the fragment's
     *      UI should be attached to.  The fragment should not add the view itself,
     *      but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState
     *      If non-null, this fragment is being re-constructed
     *      from a previous saved state as given here.
     *
     * @return Instantiate user interface view.
     */
    @Override
    public View onCreateView(
            @NotNull
            final LayoutInflater inflater,
            final ViewGroup container,
            final Bundle savedInstanceState)
    {
        _binding = FragmentStepcountMergeBinding.inflate(inflater, container, false);
        return _binding.getRoot();
    }

    /**
     * Called immediately after {@link Fragment#onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * has returned, but before any saved state has been restored in to the view.
     * This gives subclasses a chance to initialize themselves once they know their
     * view hierarchy has been completely created. The fragment's view hierarchy is not
     * however attached to its parent at this point.
     *
     * @param view
     *      The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState
     *      If non-null, this fragment is being re-constructed
     *      from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(
        @NonNull @NotNull
        final View view,
        @Nullable @org.jetbrains.annotations.Nullable
        final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        final StepCountMergeViewModel viewModel = GetViewModelFactory().CreateStepCountMergeViewModel(_day);

        _viewModelAdapter = new StepCountMergeViewModelDataBindingAdapter(viewModel);

        _binding.setViewModel(_viewModelAdapter);
        _binding.setLifecycleOwner(this);
    }

    /**
     * Called when the view previously created by {@link #onCreateView} has
     * been detached from the fragment.  The next time the fragment needs
     * to be displayed, a new view will be created.  This is called
     * after {@link #onStop()} and before {@link #onDestroy()}.  It is called
     * <em>regardless</em> of whether {@link #onCreateView} returned a
     * non-null view.  Internally it is called after the view's state has
     * been saved but before it has been removed from its parent.
     */
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();

        _viewModelAdapter.Dispose();
        _viewModelAdapter = null;
        _binding = null;
    }

    public class StepCountMergeViewModelDataBindingAdapter
        extends BaseObservable
        implements IDisposable
    {
        private final StepCountMergeViewModel _viewModel;

        private final IDisposable _viewModelEventListener;

        public final boolean IsEditable;

        public final int SaveButtonLabel;

        public final int RecordCouldNotLoadedErrorMessageVisibility;

        public StepCountMergeViewModelDataBindingAdapter(final StepCountMergeViewModel viewModel)
        {
            _viewModel = viewModel;

            if (_viewModel.IsExistingRecordEdited())
            {
                TrySetToolBarTitle(R.string.steps_merge_header_update);
                SaveButtonLabel = R.string.steps_merge_button_update;
            }
            else
            {
                TrySetToolBarTitle(R.string.steps_merge_header_create);
                SaveButtonLabel = R.string.steps_merge_button_create;
            }

            IsEditable = !_viewModel.IsReadOnly();

            RecordCouldNotLoadedErrorMessageVisibility =
                _viewModel.IsReadOnly()
                ? View.VISIBLE
                : View.INVISIBLE;

            _viewModelEventListener = _viewModel.RegisterEventHandler(new ViewModelEventListener());
        }

        @Bindable
        public String getStepCount()
        {
            return _viewModel.GetStepCountString();
        }

        public void setStepCount(String text)
        {
            _viewModel.SetStepCount(text);
        }

        @Bindable
        public int getStepCountErrorMessageVisibility()
        {
            return _viewModel.IsStepCountInvalid()
                   ? View.VISIBLE
                   : View.INVISIBLE;
        }

        @Bindable
        public String getGoal()
        {
            return _viewModel.GetGoalString();
        }

        public void setGoal(String text)
        {
            _viewModel.SetGoal(text);
        }

        @Bindable
        public int getGoalErrorMessageVisibility()
        {
            return _viewModel.IsGoalInvalid()
                ? View.VISIBLE
                : View.INVISIBLE;
        }

        @Bindable
        public String getDateTimeOfMeasurement()
        {
            return _viewModel.GetDateTimeOfMeasurementAsString();
        }

        public void OnPickDateTimeOfMeasurementButtonClicked()
        {
            final LocalDateTime dateTimeOfMeasurement = _viewModel.GetDateTimeOfMeasurement();
            PickDateTime(dateTimeOfMeasurement, _viewModel::SetDateTimeOfMeasurement);
        }

        public void OnSetDateTimeOfMeasurementToNowButtonClicked()
        {
            _viewModel.SetDateTimeOfMeasurementToNow();
        }

        @Bindable
        public boolean getIsSaveButtonEnabled()
        {
            return _viewModel.CanValuesBeSaved();
        }

        public void OnSaveButtonClicked()
        {
            _viewModel.Save();
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

        private class ViewModelEventListener
            implements IStepCountMergeViewModelEventHandler
        {
            /**
             * Called when the step-count property changed.
             */
            @Override
            public void StepCountValueChanged()
            {
                notifyPropertyChanged(BR.stepCount);
            }

            /**
             * Called when the validity of the step-count property changed.
             */
            @Override
            public void StepCountValueValidityChanged()
            {
                notifyPropertyChanged(BR.stepCountErrorMessageVisibility);
            }

            /**
             * Called when the step-count goal property changed.
             */
            @Override
            public void GoalValueChanged()
            {
                notifyPropertyChanged(BR.goal);
            }

            /**
             * Called when the validity of the step-count goal property changed.
             */
            @Override
            public void GoalValueValidityChanged()
            {
                notifyPropertyChanged(BR.goalErrorMessageVisibility);
            }

            /**
             * Called when the date-time of measurement property changed.
             */
            @Override
            public void DateTimeOfMeasurementChanged()
            {
                notifyPropertyChanged(BR.dateTimeOfMeasurement);
            }

            /**
             * Called if the state changes if {@link StepCountMergeViewModel#Save()} has any effects.
             */
            @Override
            public void CanValuesBeSavedChanged()
            {
                notifyPropertyChanged(BR.isSaveButtonEnabled);
            }

            /**
             * Called when the record was saved.
             */
            @Override
            public void RecordSaved()
            {
                TryGoBack();
            }

            /**
             * Called when the record could not be saved because of an error.
             */
            @Override
            public void RecordCouldNotBeSaved()
            {
                if (_viewModel.IsExistingRecordEdited())
                {
                    ShowToastWithLongDuration(R.string.steps_merge_notifications_update_failed);
                }
                else
                {
                    ShowToastWithLongDuration(R.string.steps_merge_notifications_creation_failed);
                }
            }
        }
    }
}