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

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.fragment.app.Fragment;
import de.dviererbe.healthtrack.BR;
import de.dviererbe.healthtrack.IDisposable;
import de.dviererbe.healthtrack.R;
import de.dviererbe.healthtrack.databinding.FragmentWeightMergeBinding;
import de.dviererbe.healthtrack.domain.BloodPressureUnit;
import de.dviererbe.healthtrack.domain.WeightUnit;
import de.dviererbe.healthtrack.presentation.FragmentBase;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public class WeightMergeFragment extends FragmentBase
{
    private static final String PARAM_Identifier = "Identifier";

    private WeightMergeViewModelDataBindingAdapter _viewModelAdapter;

    private FragmentWeightMergeBinding _binding;

    private UUID _recordIdentifier;

    public static Bundle BundleParameter(UUID recordIdentifier)
    {
        final Bundle parameter = new Bundle();
        parameter.putString(PARAM_Identifier, recordIdentifier.toString());

        return parameter;
    }

    private void UnbundleParameter(Bundle parameter)
    {
        if (parameter == null || !parameter.containsKey(PARAM_Identifier)) return;

        _recordIdentifier = UUID.fromString(parameter.getString(PARAM_Identifier));
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
    public void onCreate(
        @Nullable @org.jetbrains.annotations.Nullable
        final Bundle savedInstanceState)
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
        _binding = FragmentWeightMergeBinding.inflate(inflater, container, false);
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

        final WeightMergeViewModel viewModel = GetViewModelFactory().CreateWeightMergeViewModel(_recordIdentifier);

        _viewModelAdapter = new WeightMergeViewModelDataBindingAdapter(viewModel);

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
     * been saved, but before it has been removed from its parent.
     */
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();

        _viewModelAdapter.Dispose();
        _viewModelAdapter = null;
        _binding = null;
    }

    public class WeightMergeViewModelDataBindingAdapter
        extends BaseObservable
        implements IDisposable
    {
        private final WeightMergeViewModel _viewModel;

        private final IDisposable _viewModelEventListener;

        public final ArrayAdapter<CharSequence> WeightUnitAdapter;

        public final boolean IsEditable;

        public final int SaveButtonLabel;

        public final int RecordCouldNotLoadedErrorMessageVisibility;

        public WeightMergeViewModelDataBindingAdapter(
            final WeightMergeViewModel viewModel)
        {
            _viewModel = viewModel;

            if (_viewModel.IsExistingRecordEdited())
            {
                TrySetToolBarTitle(R.string.weight_merge_header_update);
                SaveButtonLabel = R.string.weight_merge_button_update;
            }
            else
            {
                TrySetToolBarTitle(R.string.weight_merge_header_create);
                SaveButtonLabel = R.string.weight_merge_button_create;
            }

            WeightUnitAdapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.weight_units,
                android.R.layout.simple_spinner_dropdown_item);

            IsEditable = !_viewModel.IsReadOnly();

            RecordCouldNotLoadedErrorMessageVisibility =
                    _viewModel.IsReadOnly()
                    ? View.VISIBLE
                    : View.INVISIBLE;

            _viewModelEventListener = _viewModel.RegisterEventHandler(new ViewModelEventListener());
        }

        @Bindable
        public String getWeight()
        {
            return _viewModel.GetWeightString();
        }

        public void setWeight(String text)
        {
            _viewModel.SetWeight(text);
        }

        @Bindable
        public int getWeightErrorMessageVisibility()
        {
            return _viewModel.IsWeightInvalid() ?
               View.VISIBLE :
               View.INVISIBLE;
        }

        @Bindable
        public int getSelectedWeightUnit()
        {
            switch (_viewModel.GetWeightUnit())
            {
                default:
                case Kilogram:
                    return 0;
                case Pound:
                    return 1;
            }
        }

        public void setSelectedWeightUnit(final int position)
        {
            if (position == 0)
                _viewModel.SetWeightUnit(WeightUnit.Kilogram);
            else if (position == 1)
                _viewModel.SetWeightUnit(WeightUnit.Pound);
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

        private class ViewModelEventListener implements WeightMergeViewModel.IWeightMergeViewModelEventHandler
        {
            /**
             * Called when the weight property changed.
             */
            @Override
            public void WeightChanged()
            {
                notifyPropertyChanged(BR.weight);
            }

            /**
             * Called when the validity of the weight property changed.
             */
            @Override
            public void WeightValidityChanged()
            {
                notifyPropertyChanged(BR.weightErrorMessageVisibility);
            }

            /**
             * Called when the weight-unit property changed.
             */
            @Override
            public void WeightUnitChanged()
            {
                notifyPropertyChanged(BR.selectedWeightUnit);
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
             * Called if the state changes if {@link WeightMergeViewModel#Save()} has any effects.
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
                    ShowToastWithLongDuration(R.string.weight_merge_notifications_update_failed);
                }
                else
                {
                    ShowToastWithLongDuration(R.string.weight_merge_notifications_creation_failed);
                }
            }
        }
    }
}